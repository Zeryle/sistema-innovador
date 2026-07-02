import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  // Auth (public)
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  // App (authenticated)
  {
    path: '',
    canActivate: [AuthGuard],
    loadComponent: () => import('./layouts/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'customers',
        loadComponent: () => import('./features/customers/customers.component').then(m => m.CustomersComponent)
      },
      {
        path: 'vehicles',
        loadComponent: () => import('./features/vehicles/vehicles.component').then(m => m.VehiclesComponent)
      },
      {
        path: 'work-orders',
        loadComponent: () => import('./features/work-orders/work-orders.component').then(m => m.WorkOrdersComponent)
      },
      {
        path: 'reminders',
        loadComponent: () => import('./features/reminders/reminders.component').then(m => m.RemindersComponent)
      },
      {
        path: 'analytics',
        loadComponent: () => import('./features/analytics/analytics.component').then(m => m.AnalyticsComponent)
      },
      {
        path: 'catalog',
        loadComponent: () => import('./features/catalog/catalog.component').then(m => m.CatalogComponent)
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent)
      }
    ]
  }
];
