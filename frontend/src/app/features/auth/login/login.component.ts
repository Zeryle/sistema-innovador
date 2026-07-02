import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  template: `
      <div class="min-h-screen flex items-center justify-center relative overflow-hidden bg-gradient-to-br from-slate-900 via-primary-900 to-slate-800 p-4">
        <!-- Decorative animated background -->
        <div class="absolute inset-0 overflow-hidden">
          <div class="absolute -top-40 -right-40 w-[500px] h-[500px] bg-primary-500/20 rounded-full blur-3xl animate-pulse"></div>
          <div class="absolute -bottom-40 -left-40 w-[500px] h-[500px] bg-blue-500/15 rounded-full blur-3xl animate-pulse" style="animation-delay: 1.5s"></div>
          <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-primary-400/10 rounded-full blur-3xl"></div>
        </div>

        <div class="relative w-full max-w-md">
          <!-- Brand above card -->
          <div class="text-center mb-8">
            <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br from-primary-500 to-primary-700 text-white text-2xl shadow-lg shadow-primary-500/40 mb-3">
              🔧
            </div>
            <h1 class="text-3xl font-bold text-white tracking-tight">AutoTaller SaaS</h1>
            <p class="text-primary-200 text-sm mt-1">Gestión inteligente para tu taller</p>
          </div>

          <!-- Card -->
          <div class="bg-white/10 backdrop-blur-xl border border-white/20 rounded-3xl shadow-2xl p-8">
            <h2 class="text-xl font-semibold text-white mb-1 text-center">Iniciar sesión</h2>
            <p class="text-primary-200 text-sm text-center mb-6">Accedé a tu taller y controlá todo.</p>

            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
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
                <label class="block text-sm font-medium text-primary-100 mb-1.5">Contraseña</label>
                <div class="relative">
                  <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-primary-300">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
                  </span>
                  <input type="password" formControlName="password"
                         class="w-full bg-white/10 border border-white/20 rounded-xl pl-11 pr-3 py-2.5 text-white placeholder-primary-300 focus:outline-none focus:ring-2 focus:ring-primary-400 focus:border-transparent transition"
                         placeholder="••••••••">
                </div>
              </div>

              @if (errorMessage) {
                <div class="bg-red-500/15 border border-red-400/30 text-red-100 text-sm p-3 rounded-lg flex items-center gap-2">
                  <span>⚠️</span> {{ errorMessage }}
                </div>
              }

              <button type="submit" [disabled]="form.invalid || loading"
                      class="w-full bg-gradient-to-r from-primary-500 to-primary-700 hover:from-primary-600 hover:to-primary-800 text-white font-semibold py-2.5 rounded-xl transition shadow-lg shadow-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed hover:-translate-y-0.5">
                {{ loading ? 'Ingresando...' : 'Ingresar' }}
              </button>

              <div class="relative my-4">
                <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-white/10"></div></div>
                <div class="relative flex justify-center text-xs"><span class="bg-transparent px-3 text-primary-200">o</span></div>
              </div>

              <p class="text-center text-sm text-primary-100">
                ¿No tienes cuenta? <a routerLink="/register" class="text-white font-semibold hover:underline">Regístrate</a>
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
export class LoginComponent {
  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';
    this.authService.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/app/dashboard']),
      error: () => { this.errorMessage = 'Credenciales inválidas'; this.loading = false; }
    });
  }
}
