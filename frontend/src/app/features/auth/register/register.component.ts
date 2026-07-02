import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
  template: `
    <div class="card">
      <h2 class="text-xl font-semibold mb-6 text-center">Crear Cuenta</h2>
      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4">
        <div><label class="block text-sm font-medium text-gray-700 mb-1">Nombre del taller</label>
          <input type="text" formControlName="businessName" class="input-field" placeholder="Mi Taller S.A.C."></div>
        <div><label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input type="email" formControlName="email" class="input-field" placeholder="tu@email.com"></div>
        <div><label class="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
          <input type="text" formControlName="phone" class="input-field" placeholder="+51999888777"></div>
        <div><label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
          <input type="password" formControlName="password" class="input-field" placeholder="Mínimo 6 caracteres"></div>
        @if (errorMessage) {
          <div class="bg-red-50 text-red-600 text-sm p-3 rounded-lg">{{ errorMessage }}</div>
        }
        <button type="submit" [disabled]="form.invalid || loading" class="btn-primary w-full">
          {{ loading ? 'Creando...' : 'Registrarse' }}
        </button>
        <p class="text-center text-sm text-gray-500">
          ¿Ya tienes cuenta? <a routerLink="/login" class="text-primary-600 hover:underline">Ingresar</a>
        </p>
      </form>
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
      next: () => this.router.navigate(['/dashboard']),
      error: () => { this.errorMessage = 'Error al registrarse'; this.loading = false; }
    });
  }
}
