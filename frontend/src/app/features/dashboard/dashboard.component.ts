import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule],
  template: `
    <div>
      <h1 class="text-2xl font-bold mb-6">Dashboard</h1>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="card cursor-pointer hover:shadow-md transition" routerLink="/work-orders">
          <p class="text-sm text-gray-500">Órdenes Activas</p>
          <p class="text-3xl font-bold text-yellow-700">{{ stats.activeOrders || 0 }}</p>
        </div>
        <div class="card cursor-pointer hover:shadow-md transition" routerLink="/work-orders">
          <p class="text-sm text-gray-500">Órdenes Totales</p>
          <p class="text-3xl font-bold text-primary-700">{{ stats.totalOrders || 0 }}</p>
        </div>
        <div class="card cursor-pointer hover:shadow-md transition" routerLink="/customers">
          <p class="text-sm text-gray-500">Clientes</p>
          <p class="text-3xl font-bold text-blue-700">{{ stats.totalCustomers || 0 }}</p>
        </div>
        <div class="card cursor-pointer hover:shadow-md transition" routerLink="/vehicles">
          <p class="text-sm text-gray-500">Vehículos</p>
          <p class="text-3xl font-bold text-green-700">{{ stats.totalVehicles || 0 }}</p>
        </div>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <a routerLink="/customers" class="btn-primary text-center">👥 Gestionar Clientes</a>
        <a routerLink="/vehicles" class="btn-primary text-center">🚗 Gestionar Vehículos</a>
        <a routerLink="/work-orders" class="btn-primary text-center">🔧 Nueva Orden de Trabajo</a>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  stats: any = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<any>('/analytics/dashboard').subscribe({
      next: (data) => this.stats = data,
      error: () => {}
    });
  }
}
