import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PublicApiService, PublicPlan } from '../../core/services/public-api.service';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <!-- NAVBAR (mismo look que la landing) -->
    <header class="fixed top-0 inset-x-0 z-50 backdrop-blur-md bg-white/80 border-b border-gray-200/60">
      <nav class="max-w-7xl mx-auto px-6 py-3 flex items-center justify-between">
        <a routerLink="/" class="flex items-center gap-2">
          <div class="w-9 h-9 rounded-lg bg-gradient-to-br from-primary-600 to-primary-800 flex items-center justify-center text-white font-bold text-lg shadow-md">🔧</div>
          <span class="font-bold text-gray-900 text-lg">AutoTaller</span>
        </a>
        <div class="flex items-center gap-3">
          <a routerLink="/" class="px-4 py-2 text-sm font-medium text-primary-700 hover:bg-primary-50 rounded-lg transition">← Volver al inicio</a>
          <a routerLink="/login" class="px-4 py-2 text-sm font-medium text-primary-700 hover:bg-primary-50 rounded-lg transition">Iniciar sesión</a>
          <a routerLink="/register" class="px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg shadow-sm transition">Crear cuenta</a>
        </div>
      </nav>
    </header>

    <!-- HEADER -->
    <section class="pt-32 pb-12">
      <div class="max-w-5xl mx-auto px-6 text-center">
        <span class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 text-primary-700 text-xs font-semibold mb-5 border border-primary-100">Planes y precios</span>
        <h1 class="text-4xl md:text-5xl font-extrabold text-gray-900 mb-4">
          Elige el plan que <span class="bg-gradient-to-r from-primary-600 to-primary-400 bg-clip-text text-transparent">crece con tu taller</span>
        </h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          Empieza gratis. Sube de plan cuando lo necesites. Sin contratos forzosos.
        </p>
        <div class="mt-6 inline-flex items-center gap-2 text-sm text-gray-500">
          <span class="text-green-500">●</span> 14 días de prueba con el plan BASIC al registrarte
          <span class="text-gray-300">·</span>
          <span class="text-blue-500">●</span> Cancela cuando quieras
        </div>
      </div>
    </section>

    <!-- PLANS GRID -->
    <section class="pb-24">
      <div class="max-w-6xl mx-auto px-6">
        <div *ngIf="loading" class="text-center text-gray-500 py-12">Cargando planes…</div>
        <div *ngIf="error" class="text-center text-red-600 py-12">{{ error }}</div>

        <div *ngIf="!loading && !error" class="grid md:grid-cols-3 gap-6">
          <div *ngFor="let plan of plans"
               class="card flex flex-col"
               [class.ring-2]="plan.code === 'BASIC'"
               [class.ring-primary-500]="plan.code === 'BASIC'"
               [class.shadow-xl]="plan.code === 'BASIC'"
               [class.relative]="plan.code === 'BASIC'">

            <span *ngIf="plan.code === 'BASIC'"
                  class="absolute -top-3 left-1/2 -translate-x-1/2 px-3 py-1 rounded-full bg-primary-600 text-white text-xs font-bold uppercase tracking-wider shadow-md">
              Más popular
            </span>

            <h3 class="text-xl font-bold text-gray-900">{{ plan.name }}</h3>
            <p class="text-sm text-gray-500 mt-1 mb-4">{{ plan.tagline }}</p>

            <div class="flex items-baseline gap-1 mb-6">
              <span class="text-4xl font-extrabold text-gray-900">
                {{ plan.monthlyPrice === 0 ? 'Gratis' : ('S/ ' + (plan.monthlyPrice | number:'1.0-0')) }}
              </span>
              <span *ngIf="plan.monthlyPrice > 0" class="text-sm text-gray-500">/ mes</span>
            </div>

            <ul class="space-y-3 text-sm text-gray-700 flex-1">
              <li *ngFor="let f of plan.features" class="flex items-start gap-2">
                <svg class="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                </svg>
                <span>{{ f }}</span>
              </li>
            </ul>

            <div class="mt-6 pt-6 border-t border-gray-200 text-xs text-gray-500 space-y-1">
              <div class="flex items-center gap-1.5">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/></svg>
                <span>Hasta <b class="text-gray-900">{{ plan.maxCustomersDisplay }}</b> clientes</span>
              </div>
              <div class="flex items-center gap-1.5">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/></svg>
                <span>Hasta <b class="text-gray-900">{{ plan.maxWorkOrdersDisplay }}</b> órdenes / mes</span>
              </div>
              <div class="flex items-center gap-1.5">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
                <span>Hasta <b class="text-gray-900">{{ plan.maxAdminUsers }}</b> usuarios admin</span>
              </div>
              <div *ngIf="plan.whatsappEnabled" class="flex items-center gap-1.5 text-green-700 font-medium">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448L.057 24zm6.597-3.807c1.676.995 3.276 1.591 4.939 1.592 5.362 0 9.732-4.37 9.734-9.732.002-2.601-1.01-5.046-2.851-6.886-1.84-1.84-4.286-2.851-6.886-2.851-5.362 0-9.732 4.37-9.732 9.732 0 1.738.5 3.451 1.587 4.939l-.999 3.648 3.748-.992zm11.387-5.464c-.074-.124-.272-.198-.57-.347-.297-.149-1.758-.868-2.031-.967-.272-.099-.47-.149-.669.149-.198.297-.768.967-.941 1.165-.173.198-.347.223-.644.074-.297-.149-1.255-.462-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.297-.347.446-.521.151-.172.2-.296.3-.495.099-.198.05-.372-.025-.521-.075-.148-.669-1.611-.916-2.206-.242-.579-.487-.501-.669-.51l-.57-.01c-.198 0-.52.074-.792.372s-1.04 1.016-1.04 2.479 1.065 2.876 1.213 3.074c.149.198 2.095 3.2 5.076 4.487.709.306 1.263.489 1.694.626.712.226 1.36.194 1.872.118.571-.085 1.758-.719 2.006-1.413.248-.695.248-1.29.173-1.414z"/></svg>
                <span>Notificaciones por WhatsApp</span>
              </div>
            </div>

            <a *ngIf="plan.code === 'FREE'"
               routerLink="/register"
               class="mt-6 w-full inline-flex items-center justify-center px-6 py-3 rounded-xl bg-gray-900 text-white font-semibold hover:bg-gray-800 transition">
              Empezar gratis
            </a>
            <a *ngIf="plan.code !== 'FREE'"
               routerLink="/register"
               [queryParams]="{plan: plan.code}"
               class="mt-6 w-full inline-flex items-center justify-center px-6 py-3 rounded-xl font-semibold transition"
               [class.bg-primary-600]="plan.code !== 'BASIC'"
               [class.text-white]="plan.code !== 'BASIC'"
               [class.hover:bg-primary-700]="plan.code !== 'BASIC'"
               [class.bg-white]="plan.code === 'BASIC'"
               [class.border]="plan.code === 'BASIC'"
               [class.border-primary-600]="plan.code === 'BASIC'"
               [class.text-primary-700]="plan.code === 'BASIC'"
               [class.hover:bg-primary-50]="plan.code === 'BASIC'">
              Elegir {{ plan.name }}
            </a>
          </div>
        </div>

        <!-- COMPARISON TABLE -->
        <div *ngIf="!loading && !error" class="mt-16">
          <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">Compara los planes en detalle</h2>
          <div class="card overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-gray-500 border-b border-gray-200">
                  <th class="py-3 px-2 font-medium">Característica</th>
                  <th *ngFor="let plan of plans" class="py-3 px-2 font-medium text-center"
                      [class.text-primary-700]="plan.code === 'BASIC'">
                    {{ plan.name }}
                  </th>
                </tr>
              </thead>
              <tbody class="text-gray-700">
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">Precio mensual</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">
                    {{ plan.monthlyPrice === 0 ? 'Gratis' : ('S/ ' + (plan.monthlyPrice | number:'1.0-0')) }}
                  </td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">Clientes</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">{{ plan.maxCustomersDisplay }}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">Órdenes / mes</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">{{ plan.maxWorkOrdersDisplay }}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">Usuarios admin</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">{{ plan.maxAdminUsers }}</td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">WhatsApp</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">
                    <span [class.text-green-500]="plan.whatsappEnabled"
                          [class.text-gray-300]="!plan.whatsappEnabled">
                      {{ plan.whatsappEnabled ? '✓' : '—' }}
                    </span>
                  </td>
                </tr>
                <tr class="border-b border-gray-100">
                  <td class="py-3 px-2 font-medium">Analítica</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">
                    <span [class.text-green-500]="plan.analyticsEnabled"
                          [class.text-gray-300]="!plan.analyticsEnabled">
                      {{ plan.analyticsEnabled ? '✓' : '—' }}
                    </span>
                  </td>
                </tr>
                <tr>
                  <td class="py-3 px-2 font-medium">Soporte prioritario</td>
                  <td *ngFor="let plan of plans" class="py-3 px-2 text-center">
                    <span [class.text-green-500]="plan.prioritySupport"
                          [class.text-gray-300]="!plan.prioritySupport">
                      {{ plan.prioritySupport ? '✓' : '—' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- FAQ -->
        <div *ngIf="!loading && !error" class="mt-20 max-w-3xl mx-auto">
          <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">Preguntas frecuentes</h2>
          <div class="space-y-4">
            <details class="card">
              <summary class="cursor-pointer font-semibold text-gray-900">¿Puedo cambiar de plan en cualquier momento?</summary>
              <p class="text-gray-600 text-sm mt-2">Sí. Subes o bajas de plan cuando quieras desde la sección <b>Mi cuenta → Mi plan</b>. El cobro se prorratea según los días restantes del mes.</p>
            </details>
            <details class="card">
              <summary class="cursor-pointer font-semibold text-gray-900">¿Qué métodos de pago aceptan?</summary>
              <p class="text-gray-600 text-sm mt-2">Tarjeta de crédito/débito (vía Stripe), Yape, Plin y transferencia bancaria BCP. Todos los pagos son procesados en Soles (PEN).</p>
            </details>
            <details class="card">
              <summary class="cursor-pointer font-semibold text-gray-900">¿Qué pasa si supero el límite de mi plan?</summary>
              <p class="text-gray-600 text-sm mt-2">El sistema te avisa con un warning, pero no bloquea la operación. Te damos 7 días para subir de plan o reducir el uso. Si no, los nuevos registros quedan en cola hasta resolver.</p>
            </details>
            <details class="card">
              <summary class="cursor-pointer font-semibold text-gray-900">¿Hay contrato de permanencia?</summary>
              <p class="text-gray-600 text-sm mt-2">No. Cancela cuando quieras. Si cancelas dentro de los 14 días de prueba, te devolvemos el 100%.</p>
            </details>
          </div>
        </div>
      </div>
    </section>
  `
})
export class PricingComponent implements OnInit {
  plans: PublicPlan[] = [];
  loading = true;
  error: string | null = null;

  constructor(private publicApi: PublicApiService) {}

  ngOnInit(): void {
    this.publicApi.getPlans().subscribe({
      next: (plans) => {
        this.plans = plans;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'No se pudieron cargar los planes. Intenta recargar la página.';
        this.loading = false;
        console.error('Error cargando planes', err);
      }
    });
  }
}
