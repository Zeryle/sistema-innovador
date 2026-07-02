import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

interface NavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule],
  template: `
    <aside class="w-64 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-6 border-b border-gray-100">
        <h2 class="text-lg font-bold text-primary-700">🔧 AutoTaller</h2>
      </div>
      <nav class="flex-1 p-4 space-y-1">
        @for (item of navItems; track item.route) {
          <a [routerLink]="[item.route]"
             routerLinkActive="bg-primary-50 text-primary-700 font-medium"
             class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-gray-600 hover:bg-gray-50 transition-colors">
            <span>{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </a>
        }
      </nav>
      <div class="p-4 border-t border-gray-100">
        <button (click)="logout()" class="flex items-center gap-3 px-3 py-2.5 w-full rounded-lg text-red-600 hover:bg-red-50 transition-colors">
          <span>🚪</span>
          <span>Cerrar sesión</span>
        </button>
      </div>
    </aside>
  `
})
export class SidebarComponent {
  navItems: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: '📊' },
    { label: 'Clientes', route: '/customers', icon: '👥' },
    { label: 'Vehículos', route: '/vehicles', icon: '🚗' },
    { label: 'Órdenes', route: '/work-orders', icon: '🔧' },
    { label: 'Recordatorios', route: '/reminders', icon: '⏰' },
    { label: 'Catálogo', route: '/catalog', icon: '📋' },
    { label: 'Analíticas', route: '/analytics', icon: '📈' },
    { label: 'Configuración', route: '/settings', icon: '⚙️' }
  ];

  constructor(private authService: AuthService) {}

  logout(): void {
    this.authService.logout();
    window.location.href = '/login';
  }
}
