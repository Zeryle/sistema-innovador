import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BillingService, CheckoutDetails, GatewayCheckout } from '../../../core/services/billing.service';
import { MockStripeClient, simulateCardPayment } from '../../../core/services/mock-stripe.service';

type Step = 'loading' | 'review' | 'card' | 'processing';

@Component({
  selector: 'app-checkout-pay',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex flex-col">
      <!-- Header with progress -->
      <header class="bg-white border-b border-gray-200">
        <div class="max-w-3xl mx-auto px-6 py-4 flex items-center justify-between">
          <a routerLink="/" class="flex items-center gap-2 text-gray-700 hover:text-gray-900">
            <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-600 to-primary-800 flex items-center justify-center text-white font-bold shadow-sm">🔧</div>
            <span class="font-bold text-lg">AutoTaller</span>
          </a>
          <span class="text-xs text-gray-500">Pago seguro · Simulado (modo test)</span>
        </div>

        <!-- Step indicator -->
        <div class="max-w-3xl mx-auto px-6 pb-4">
          <ol class="flex items-center gap-2 text-xs text-gray-600">
            <li *ngFor="let s of ['review','card','processing']; let i = index"
                class="flex items-center gap-2">
              <span class="w-6 h-6 rounded-full flex items-center justify-center font-bold text-xs"
                    [class.bg-primary-600]="currentStep === s || stepIndex(s) < stepIndex(currentStep)"
                    [class.text-white]="currentStep === s || stepIndex(s) < stepIndex(currentStep)"
                    [class.bg-gray-200]="stepIndex(s) > stepIndex(currentStep)"
                    [class.text-gray-500]="stepIndex(s) > stepIndex(currentStep)">
                {{ i + 1 }}
              </span>
              <span>{{ stepLabel(s) }}</span>
              <span *ngIf="i < 2" class="text-gray-300">—</span>
            </li>
          </ol>
        </div>
      </header>

      <main class="flex-1 flex items-start justify-center p-6">
        <div class="w-full max-w-3xl">
          <!-- Loading -->
          <div *ngIf="currentStep === 'loading'" class="card text-center text-gray-500 py-12">
            <div class="animate-pulse">Cargando detalles del pago…</div>
          </div>

          <!-- Error -->
          <div *ngIf="error" class="card border-red-300 bg-red-50 text-red-700">
            <strong>Error:</strong> {{ error }}
          </div>

          <!-- Review step -->
          <div *ngIf="currentStep === 'review' && details" class="space-y-4">
            <h1 class="text-2xl font-bold text-gray-900">Confirma tu suscripción</h1>
            <p class="text-gray-600">Estás a punto de actualizar tu plan a <strong>{{ details.targetPlanName }}</strong>.</p>

            <div class="card">
              <div class="flex items-baseline justify-between mb-4">
                <div>
                  <div class="text-sm text-gray-500">Plan</div>
                  <div class="text-2xl font-bold text-gray-900">{{ details.targetPlanName }}</div>
                </div>
                <div class="text-right">
                  <div class="text-3xl font-extrabold text-primary-700">S/ {{ details.expectedAmount | number:'1.0-0' }}</div>
                  <div class="text-xs text-gray-500">/ mes</div>
                </div>
              </div>
              <ul class="space-y-2 text-sm text-gray-700 border-t border-gray-200 pt-4">
                <li class="flex items-center gap-2">
                  <svg class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Cobro mensual recurrente
                </li>
                <li class="flex items-center gap-2">
                  <svg class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Cancela cuando quieras
                </li>
                <li class="flex items-center gap-2">
                  <svg class="w-4 h-4 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Soporte prioritario por email
                </li>
              </ul>
            </div>

            <button (click)="goToCard()"
                    class="w-full px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold hover:bg-primary-700 transition shadow-sm">
              Continuar al pago
            </button>
            <a routerLink="/pricing"
               class="block text-center text-sm text-gray-500 hover:text-gray-700 mt-2">
              ← Volver a planes
            </a>
          </div>

          <!-- Card step -->
          <div *ngIf="currentStep === 'card' && details" class="space-y-4">
            <h1 class="text-2xl font-bold text-gray-900">Datos de pago</h1>
            <p class="text-gray-600">Estás pagando <strong>S/ {{ details.expectedAmount | number:'1.0-0' }}</strong> por el plan <strong>{{ details.targetPlanName }}</strong>.</p>

            <div class="card">
              <div *ngIf="paymentError" class="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
                <strong>✗</strong> {{ paymentError }}
              </div>

              <form (ngSubmit)="pay()" #cardForm="ngForm">
                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">Número de tarjeta</label>
                  <input type="text"
                         [(ngModel)]="cardNumber"
                         name="cardNumber"
                         (input)="formatCardNumber()"
                         maxlength="19"
                         placeholder="4242 4242 4242 4242"
                         class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none font-mono"
                         [class.border-red-400]="paymentError && !cardNumber">
                  <div class="text-xs text-gray-500 mt-1">
                    💡 Tarjetas de prueba:
                    <code class="bg-gray-100 px-1 rounded">4242…4242</code> (éxito),
                    <code class="bg-gray-100 px-1 rounded">4000…0002</code> (rechazada)
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Vencimiento (MM/AA)</label>
                    <input type="text"
                           [(ngModel)]="cardExpiry"
                           name="cardExpiry"
                           maxlength="5"
                           placeholder="12/30"
                           class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none font-mono">
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">CVC</label>
                    <input type="text"
                           [(ngModel)]="cardCvc"
                           name="cardCvc"
                           maxlength="4"
                           placeholder="123"
                           class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none font-mono">
                  </div>
                </div>

                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">Nombre del titular</label>
                  <input type="text"
                         [(ngModel)]="cardName"
                         name="cardName"
                         placeholder="Como aparece en la tarjeta"
                         class="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-primary-500 focus:ring-2 focus:ring-primary-200 outline-none">
                </div>

                <button type="submit"
                        [disabled]="!canSubmit()"
                        class="w-full px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold hover:bg-primary-700 transition shadow-sm disabled:opacity-50 disabled:cursor-not-allowed">
                  Pagar S/ {{ details.expectedAmount | number:'1.0-0' }} y suscribirme
                </button>
                <button type="button"
                        (click)="cancel()"
                        class="w-full mt-2 px-6 py-3 rounded-xl text-gray-600 font-medium hover:bg-gray-100 transition">
                  Cancelar
                </button>
              </form>
            </div>

            <div class="text-xs text-gray-500 text-center">
              🔒 Esta pantalla es una simulación. No se transmite ninguna tarjeta real.
            </div>
          </div>

          <!-- Processing -->
          <div *ngIf="currentStep === 'processing'" class="card text-center py-12">
            <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-primary-100 mb-4">
              <svg class="w-8 h-8 text-primary-600 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" class="opacity-25"/>
                <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
              </svg>
            </div>
            <h2 class="text-xl font-bold text-gray-900 mb-1">Procesando tu pago…</h2>
            <p class="text-sm text-gray-500">Estamos confirmando la transacción con el proveedor.</p>
          </div>
        </div>
      </main>
    </div>
  `
})
export class CheckoutPayComponent implements OnInit {
  currentStep: Step = 'loading';
  details: CheckoutDetails | null = null;
  error: string | null = null;
  paymentError: string | null = null;

  cardNumber = '4242 4242 4242 4242';
  cardExpiry = '12/30';
  cardCvc = '123';
  cardName = 'Test User';   // pre-filled in the mock so the Pay button is enabled

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private billing: BillingService,
    private mockStripe: MockStripeClient
  ) {}

  ngOnInit(): void {
    const sessionId = this.route.snapshot.paramMap.get('id');
    if (!sessionId) {
      this.error = 'No se especificó una sesión de checkout.';
      return;
    }
    this.billing.getCheckout(sessionId).subscribe({
      next: (d) => {
        this.details = d;
        if (d.isFinal) {
          if (d.status === 'SUCCEEDED') this.router.navigate(['/billing/success'], { queryParams: { plan: d.targetPlan } });
          else if (d.status === 'CANCELLED') this.router.navigate(['/billing/cancel']);
          else this.router.navigate(['/pricing']);
          return;
        }
        this.currentStep = 'review';
      },
      error: () => {
        this.error = 'No se pudo cargar la sesión de pago. Es posible que haya expirado.';
      }
    });
  }

  stepIndex(s: string): number {
    return ['review','card','processing'].indexOf(s);
  }
  stepLabel(s: string): string {
    return s === 'review' ? 'Resumen' : s === 'card' ? 'Pago' : 'Procesando';
  }

  goToCard(): void {
    this.paymentError = null;
    this.currentStep = 'card';
  }

  canSubmit(): boolean {
    const num = this.cardNumber.replace(/\s+/g, '');
    return num.length >= 13 && this.cardCvc.length >= 3 && this.cardName.trim().length >= 3;
  }

  formatCardNumber(): void {
    const digits = this.cardNumber.replace(/\s+/g, '').replace(/\D/g, '');
    const groups = digits.match(/.{1,4}/g) || [];
    this.cardNumber = groups.join(' ').substr(0, 19);
  }

  pay(): void {
    if (!this.canSubmit() || !this.details) return;
    this.paymentError = null;
    this.currentStep = 'processing';

    const result = simulateCardPayment(this.cardNumber, this.cardCvc);
    if (result.outcome === 'failed') {
      this.paymentError = result.message || 'El pago falló.';
      this.currentStep = 'card';
      return;
    }

    // Fire the mock webhook
    this.mockStripe.triggerPaymentSuccess(
      this.details.id,
      this.details.providerIntentId
    ).subscribe({
      next: () => {
        this.router.navigate(['/billing/success'], { queryParams: { plan: this.details!.targetPlan } });
      },
      error: () => {
        this.paymentError = 'No se pudo contactar al proveedor. Inténtalo de nuevo.';
        this.currentStep = 'card';
      }
    });
  }

  cancel(): void {
    if (!this.details) {
      this.router.navigate(['/pricing']);
      return;
    }
    this.mockStripe.triggerPaymentCancel(this.details.id).subscribe({
      next: () => this.router.navigate(['/billing/cancel']),
      error: () => this.router.navigate(['/billing/cancel'])
    });
  }
}