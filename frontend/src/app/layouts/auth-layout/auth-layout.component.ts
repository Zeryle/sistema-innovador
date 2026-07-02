import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-800 to-primary-600 p-4">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-white">AutoTaller SaaS</h1>
          <p class="text-primary-200 mt-2">Gestión inteligente para tu taller</p>
        </div>
        <router-outlet></router-outlet>
      </div>
    </div>
  `
})
export class AuthLayoutComponent {}
