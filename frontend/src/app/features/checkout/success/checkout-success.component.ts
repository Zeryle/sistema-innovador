import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { BillingService, PlanCode } from '../../../core/services/billing.service';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6">
      <div class="card max-w-md w-full text-center py-10">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-green-100 mb-4 animate-bounce">
          <svg class="w-10 h-10 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
          </svg>
        </div>
        <h1 class="text-3xl font-extrabold text-gray-900 mb-2">¡Listo!</h1>
        <p class="text-gray-600 mb-1">Tu suscripción ha sido actualizada al plan</p>
        <p class="text-2xl font-bold text-primary-700 mb-6">{{ planName }}</p>

        <div class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-6 text-sm text-blue-800">
          ✉️ Recibirás un email con la factura en los próximos minutos.
        </div>

        <div class="space-y-2">
          <a routerLink="/app/dashboard"
             class="block w-full px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold hover:bg-primary-700 transition shadow-sm">
            Ir al dashboard
          </a>
          <a routerLink="/pricing"
             class="block w-full px-6 py-3 rounded-xl border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition">
            Ver mi nuevo plan
          </a>
        </div>

        <div *ngIf="latestStatus" class="mt-6 pt-6 border-t border-gray-200 text-left text-xs text-gray-500">
          <div><strong>Plan actual:</strong> {{ latestStatus.currentPlanName }}</div>
          <div><strong>Próximo cobro:</strong> {{ latestStatus.nextBillingAt || '—' }}</div>
          <div><strong>Próximo ciclo:</strong> S/ {{ latestStatus.currentMonthlyPrice | number:'1.0-0' }}</div>
        </div>
      </div>
    </div>
  `
})
export class CheckoutSuccessComponent implements OnInit {
  planName = '';
  latestStatus: any = null;

  constructor(private route: ActivatedRoute, private billing: BillingService) {}

  ngOnInit(): void {
    const code = (this.route.snapshot.queryParamMap.get('plan') || '') as PlanCode;
    const names: Record<PlanCode, string> = {
      FREE: 'Free',
      BASIC: 'Basic',
      PREMIUM: 'Premium'
    };
    this.planName = names[code] || 'tu nuevo plan';

    // Reload the status so the dashboard shows the upgraded plan on next visit.
    this.billing.getSubscription().subscribe({
      next: (s) => this.latestStatus = s,
      error: () => {}
    });
  }
}