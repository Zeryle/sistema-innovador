import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  // Public marketing landing
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  // Public pricing
  {
    path: 'pricing',
    loadComponent: () => import('./features/pricing/pricing.component').then(m => m.PricingComponent)
  },
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
    path: 'app',
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
  },

  // Billing flow (checkout success/cancel are public so the user gets redirected
  // to them by the SPA without a session in some paths; the checkout/pay page
  // also lives outside /app because it must be reachable before the user can
  // log into the app on a different device).
  {
    path: 'billing/checkout/:id',
    loadComponent: () => import('./features/checkout/pay/checkout-pay.component').then(m => m.CheckoutPayComponent)
  },
  {
    path: 'billing/success',
    loadComponent: () => import('./features/checkout/success/checkout-success.component').then(m => m.CheckoutSuccessComponent)
  },
  {
    path: 'billing/cancel',
    loadComponent: () => import('./features/checkout/cancel/checkout-cancel.component').then(m => m.CheckoutCancelComponent)
  },
  // Wildcard: any unknown URL -> landing
  { path: '**', redirectTo: '' }
];
