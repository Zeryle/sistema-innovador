import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-checkout-cancel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6">
      <div class="card max-w-md w-full text-center py-10">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-yellow-100 mb-4">
          <svg class="w-10 h-10 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M5.071 19h13.858c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
          </svg>
        </div>
        <h1 class="text-3xl font-extrabold text-gray-900 mb-2">Pago cancelado</h1>
        <p class="text-gray-600 mb-6">
          No se realizó ningún cobro. Puedes volver a intentarlo cuando quieras desde la página de planes.
        </p>

        <div class="space-y-2">
          <a routerLink="/pricing"
             class="block w-full px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold hover:bg-primary-700 transition shadow-sm">
            Volver a planes
          </a>
          <a routerLink="/app/dashboard"
             class="block w-full px-6 py-3 rounded-xl border border-gray-300 text-gray-700 font-medium hover:bg-gray-50 transition">
            Ir al dashboard
          </a>
        </div>
      </div>
    </div>
  `
})
export class CheckoutCancelComponent {}