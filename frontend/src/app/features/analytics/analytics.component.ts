import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  template: `
    <div>
      <h1 class="text-2xl font-bold mb-6">Analíticas</h1>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div class="card text-center">
          <p class="text-sm text-gray-500">Órdenes Totales</p>
          <p class="text-3xl font-bold text-primary-700">{{ dashboard.totalOrders || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Órdenes Activas</p>
          <p class="text-3xl font-bold text-yellow-700">{{ dashboard.activeOrders || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Clientes</p>
          <p class="text-3xl font-bold text-blue-700">{{ dashboard.totalCustomers || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Vehículos</p>
          <p class="text-3xl font-bold text-green-700">{{ dashboard.totalVehicles || 0 }}</p>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card">
          <h3 class="font-semibold mb-4">📊 Órdenes por Estado</h3>
          @if (statusList.length > 0) {
            @for (s of statusList; track s.status) {
              <div class="flex justify-between py-2 border-b border-gray-100">
                <span class="font-medium">{{ translateStatus(s.status) }}</span>
                <span class="badge-info">{{ s.count }}</span>
              </div>
            }
          } @else {
            <p class="text-gray-400 text-center py-4">No hay datos de órdenes</p>
          }
        </div>

        <div class="card">
          <h3 class="font-semibold mb-4">⏰ Recordatorios Pendientes</h3>
          <p class="text-3xl font-bold text-yellow-600">{{ dashboard.pendingReminders || 0 }}</p>
          <p class="text-sm text-gray-500 mt-2">Recordatorios programados sin enviar</p>
        </div>
      </div>
    </div>
  `
})
export class AnalyticsComponent implements OnInit {
  dashboard: any = {};
  statusList: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<any>('/analytics/dashboard').subscribe({
      next: (data) => {
        this.dashboard = data;
        if (data.ordersByStatus) {
          this.statusList = Object.entries(data.ordersByStatus).map(([status, count]) => ({ status, count }));
        }
      }
    });
  }

  translateStatus(status: string): string {
    const map: Record<string, string> = {
      'RECEIVED': 'Recibido',
      'DIAGNOSING': 'Diagnosticando',
      'QUOTED': 'Cotizado',
      'APPROVED': 'Aprobado',
      'IN_PROGRESS': 'En Progreso',
      'WAITING_PARTS': 'Esperando Repuestos',
      'COMPLETED': 'Completado',
      'DELIVERED': 'Entregado',
      'CANCELLED': 'Cancelado'
    };
    return map[status] || status;
  }
}
