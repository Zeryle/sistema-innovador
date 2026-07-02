import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  template: `
    <div class="card">
      <h2 class="text-xl font-semibold mb-6 text-center">Iniciar Sesión</h2>
      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input type="email" formControlName="email" class="input-field" placeholder="tu@email.com">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
          <input type="password" formControlName="password" class="input-field" placeholder="******">
        </div>
        @if (errorMessage) {
          <div class="bg-red-50 text-red-600 text-sm p-3 rounded-lg">{{ errorMessage }}</div>
        }
        <button type="submit" [disabled]="form.invalid || loading" class="btn-primary w-full">
          {{ loading ? 'Ingresando...' : 'Ingresar' }}
        </button>
        <p class="text-center text-sm text-gray-500">
          ¿No tienes cuenta? <a routerLink="/register" class="text-primary-600 hover:underline">Regístrate</a>
        </p>
      </form>
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
      next: () => this.router.navigate(['/dashboard']),
      error: () => { this.errorMessage = 'Credenciales inválidas'; this.loading = false; }
    });
  }
}
