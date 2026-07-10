import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center relative overflow-hidden bg-gradient-to-br from-slate-900 via-primary-900 to-slate-800 p-4">
      <!-- Decorative animated background -->
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute -top-40 -right-40 w-[500px] h-[500px] bg-primary-500/20 rounded-full blur-3xl animate-pulse"></div>
        <div class="absolute -bottom-40 -left-40 w-[500px] h-[500px] bg-purple-500/15 rounded-full blur-3xl animate-pulse" style="animation-delay: 1.5s"></div>
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-primary-400/10 rounded-full blur-3xl"></div>
      </div>

      <div class="relative w-full max-w-md">
        <!-- Brand above card -->
        <div class="text-center mb-8">
          <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-700 text-white text-2xl shadow-lg shadow-primary-500/40 mb-3">
            🔧
          </div>
          <h1 class="text-3xl font-bold text-white tracking-tight">AutoTaller SaaS</h1>
          <p class="text-primary-200 text-sm mt-1">Crea tu cuenta y empezá hoy</p>
        </div>

        <!-- Card -->
        <div class="bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-2xl p-8">
          <h2 class="text-xl font-semibold text-white mb-1 text-center">Registrar mi taller</h2>
          <p class="text-primary-200 text-sm text-center mb-6">Completá los datos para empezar.</p>

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-primary-100 mb-1.5">Nombre del taller</label>
              <div class="relative">
                <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-primary-300">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/></svg>
                </span>
                <input type="text" formControlName="businessName"
                       class="w-full bg-white/10 border border-white/20 rounded-xl pl-11 pr-3 py-2.5 text-white placeholder-primary-300 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent transition"
                       placeholder="Mi Taller S.A.C.">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-primary-100 mb-1.5">Email</label>
              <div class="relative">
                <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-primary-300">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
                </span>
                <input type="email" formControlName="email"
                       class="w-full bg-white/10 border border-white/20 rounded-xl pl-11 pr-3 py-2.5 text-white placeholder-primary-300 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent transition"
                       placeholder="tu@email.com">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-primary-100 mb-1.5">Teléfono</label>
              <div class="relative">
                <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-primary-300">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/></svg>
                </span>
                <input type="text" formControlName="phone"
                       class="w-full bg-white/10 border border-white/20 rounded-xl pl-11 pr-3 py-2.5 text-white placeholder-primary-300 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent transition"
                       placeholder="+519****8777">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-primary-100 mb-1.5">Contraseña</label>
              <div class="relative">
                <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-primary-300">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
                </span>
                <input type="password" formControlName="password"
                       class="w-full bg-white/10 border border-white/20 rounded-xl pl-11 pr-3 py-2.5 text-white placeholder-primary-300 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent transition"
                       placeholder="Mínimo 6 caracteres">
              </div>
            </div>

            @if (errorMessage) {
              <div class="bg-red-500/15 border border-red-400/30 text-red-100 text-sm p-3 rounded-lg flex items-center gap-2">
                <span>⚠️</span> {{ errorMessage }}
              </div>
            }

            <button type="submit" [disabled]="form.invalid || loading"
                    class="w-full bg-gradient-to-r from-primary-500 to-primary-700 hover:from-primary-600 hover:to-primary-800 text-white font-semibold py-2.5 rounded-xl transition shadow-lg shadow-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed hover:-translate-y-0.5">
              {{ loading ? 'Creando...' : 'Crear cuenta' }}
            </button>

            <p class="text-center text-sm text-primary-100">
              ¿Ya tienes cuenta? <a routerLink="/login" class="text-white font-semibold hover:underline">Ingresar</a>
            </p>
          </form>
        </div>

        <p class="text-center text-xs text-primary-200/60 mt-6">
          AutoTaller SaaS · Sistema de gestión para talleres mecánicos
        </p>
      </div>
    </div>
  `
})
export class RegisterComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.form = this.fb.group({
      businessName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';
    this.authService.register(this.form.value).subscribe({
      next: () => this.router.navigate(['/app/dashboard']),
      error: () => { this.errorMessage = 'Error al registrarse'; this.loading = false; }
    });
  }
}