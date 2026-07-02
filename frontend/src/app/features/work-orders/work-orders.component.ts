import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';

@Component({
  selector: 'app-work-orders',
  standalone: true,
  imports: [DataTableComponent, FormsModule],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Órdenes de Trabajo</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancelar' : '+ Nueva Orden' }}</button>
      </div>

      @if (showForm) {
        <div class="card mb-6">
          <h3 class="font-semibold mb-4">Nueva Orden de Trabajo</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <input [(ngModel)]="form.customerId" type="number" placeholder="ID del Cliente" class="input-field">
            <input [(ngModel)]="form.vehicleId" type="number" placeholder="ID del Vehículo" class="input-field">
            <input [(ngModel)]="form.estimatedCost" type="number" placeholder="Costo estimado (S/)" class="input-field">
            <textarea [(ngModel)]="form.description" placeholder="Descripción del trabajo" class="input-field col-span-full" rows="3"></textarea>
          </div>
          <button class="btn-primary mt-4" (click)="createWorkOrder()">Crear Orden</button>
          @if (message) { <p class="mt-2 text-sm" [class.text-green-600]="ok" [class.text-red-600]="!ok">{{ message }}</p> }
        </div>
      }

      <div class="grid grid-cols-4 gap-4 mb-6">
        @for (status of statuses; track status.key) {
          <div class="card text-center cursor-pointer hover:shadow-md transition" (click)="filterByStatus(status.key)">
            <p class="text-sm text-gray-500">{{ status.label }}</p>
            <p class="text-2xl font-bold">{{ status.count }}</p>
          </div>
        }
      </div>
      <app-data-table [columns]="columns" [data]="workOrders" [actions]="actions" [showSearch]="true"
        (actionClick)="onAction($event)"></app-data-table>
    </div>
  `
})
export class WorkOrdersComponent implements OnInit {
  columns = [
    { key: 'id', label: '#' },
    { key: 'vehiclePlate', label: 'Vehículo' },
    { key: 'customerName', label: 'Cliente' },
    { key: 'status', label: 'Estado' },
    { key: 'description', label: 'Descripción' },
    { key: 'estimatedCost', label: 'Costo S/' }
  ];
  actions = [{ label: 'Ver', class: 'btn-primary' }];
  statuses = [
    { key: 'RECEIVED', label: 'Recibidos', count: 0 },
    { key: 'IN_PROGRESS', label: 'En Progreso', count: 0 },
    { key: 'WAITING_PARTS', label: 'Esperando', count: 0 },
    { key: 'COMPLETED', label: 'Completados', count: 0 }
  ];
  workOrders: any[] = [];
  showForm = false;
  form = { customerId: 1, vehicleId: 1, description: '', estimatedCost: 0 };
  message = '';
  ok = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.loadOrders(); }

  loadOrders(): void {
    this.api.get<any[]>('/work-orders').subscribe({
      next: (data) => {
        this.workOrders = data;
        this.updateStatusCounts(data);
      }
    });
  }

  updateStatusCounts(orders: any[]): void {
    const counts: Record<string, number> = {};
    orders.forEach(o => {
      const s = o.status || 'UNKNOWN';
      counts[s] = (counts[s] || 0) + 1;
    });
    this.statuses.forEach(st => st.count = counts[st.key] || 0);
  }

  createWorkOrder(): void {
    this.api.post<any>('/work-orders', this.form).subscribe({
      next: () => {
        this.message = 'Orden creada exitosamente'; this.ok = true; this.showForm = false;
        this.form = { customerId: 1, vehicleId: 1, description: '', estimatedCost: 0 };
        this.loadOrders();
      },
      error: () => { this.message = 'Error al crear orden'; this.ok = false; }
    });
  }

  filterByStatus(status: string): void {
    this.api.get<any[]>('/work-orders', { status }).subscribe({
      next: (data) => {
        this.workOrders = data;
        this.updateStatusCounts(data);
      }
    });
  }

  onAction(event: { action: string; row: any }): void {
    if (event.action === 'Ver') {
      this.api.get<any[]>(`/work-orders/vehicle/${event.row.vehicleId}`).subscribe({
        next: (history) => {
          let hist = `Historial del Vehículo #${event.row.vehicleId}\n\n`;
          hist += `Orden #${event.row.id}: ${event.row.status} — ${event.row.description}\n`;
          hist += `Costo est.: S/ ${event.row.estimatedCost || 0}\n\n`;
          hist += `--- Historial Completo (${history.length} órdenes) ---\n`;
          history.forEach((h: any, i: number) => {
            hist += `${i+1}. [${h.status}] ${h.description} — S/ ${h.estimatedCost || 0} (${h.createdAt?.substring(0,10) || 'N/A'})\n`;
          });
          alert(hist);
        },
        error: () => alert(`Orden #${event.row.id}\n${event.row.status}\n${event.row.description}\nCosto: S/ ${event.row.estimatedCost || 0}`)
      });
    }
  }
}
