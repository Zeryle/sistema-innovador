import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';

@Component({
  selector: 'app-work-orders',
  standalone: true,
  imports: [DataTableComponent, FormsModule, CommonModule, ModalComponent],
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

    <app-modal [open]="!!viewing"
               [title]="viewing ? 'Orden #' + viewing.id : ''"
               tone="info"
               icon="🔧"
               size="lg"
               cancelLabel="Cerrar"
               (close)="viewing = null">
      <div *ngIf="viewing" class="space-y-4">
        <div class="flex items-center gap-2 flex-wrap">
          <span class="inline-flex px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide"
                [class.bg-yellow-100]="viewing.status === 'RECEIVED'"
                [class.text-yellow-700]="viewing.status === 'RECEIVED'"
                [class.bg-blue-100]="viewing.status === 'IN_PROGRESS'"
                [class.text-blue-700]="viewing.status === 'IN_PROGRESS'"
                [class.bg-purple-100]="viewing.status === 'WAITING_PARTS'"
                [class.text-purple-700]="viewing.status === 'WAITING_PARTS'"
                [class.bg-green-100]="viewing.status === 'COMPLETED'"
                [class.text-green-700]="viewing.status === 'COMPLETED'">
            {{ viewing.status }}
          </span>
          <span class="text-sm text-gray-500">
            {{ viewing.vehiclePlate }} · {{ viewing.customerName }}
          </span>
        </div>

        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Descripción</div>
          <div class="text-sm text-gray-900">{{ viewing.description || '—' }}</div>
        </div>

        <div class="grid grid-cols-3 gap-3">
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Costo estimado</div>
            <div class="text-lg font-bold text-gray-900">S/ {{ viewing.estimatedCost || 0 }}</div>
          </div>
          <div *ngIf="viewing.finalCost">
            <div class="text-xs uppercase font-medium text-gray-500">Costo final</div>
            <div class="text-lg font-bold text-green-700">S/ {{ viewing.finalCost }}</div>
          </div>
          <div *ngIf="viewing.startDate">
            <div class="text-xs uppercase font-medium text-gray-500">Inicio</div>
            <div class="text-sm text-gray-900">{{ viewing.startDate | date:'short' }}</div>
          </div>
        </div>

        <div *ngIf="viewing.diagnosticNotes">
          <div class="text-xs uppercase font-medium text-gray-500">Notas de diagnóstico</div>
          <div class="text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">{{ viewing.diagnosticNotes }}</div>
        </div>

        <div>
          <div class="flex items-center justify-between mb-2">
            <div class="text-xs uppercase font-medium text-gray-500">
              Historial del vehículo ({{ viewing.vehiclePlate }})
              <span class="ml-2 text-gray-400 normal-case">— {{ viewing.history?.length || 0 }} órdenes previas</span>
            </div>
            <span *ngIf="historyLoading" class="text-xs text-gray-400 italic">cargando…</span>
          </div>
          <div *ngIf="viewing.history && viewing.history.length > 0" class="space-y-1 max-h-48 overflow-y-auto">
            <div *ngFor="let h of viewing.history; let i = index"
                 class="flex items-center justify-between text-sm py-2 px-3 bg-gray-50 rounded-lg">
              <div class="flex items-center gap-2">
                <span class="text-xs font-mono text-gray-400">#{{ h.id }}</span>
                <span class="px-2 py-0.5 rounded-full text-xs font-medium"
                      [class.bg-yellow-100]="h.status === 'RECEIVED'"
                      [class.text-yellow-700]="h.status === 'RECEIVED'"
                      [class.bg-blue-100]="h.status === 'IN_PROGRESS'"
                      [class.text-blue-700]="h.status === 'IN_PROGRESS'"
                      [class.bg-purple-100]="h.status === 'WAITING_PARTS'"
                      [class.text-purple-700]="h.status === 'WAITING_PARTS'"
                      [class.bg-green-100]="h.status === 'COMPLETED'"
                      [class.text-green-700]="h.status === 'COMPLETED'">
                  {{ h.status }}
                </span>
                <span class="text-gray-900 truncate max-w-xs">{{ h.description }}</span>
              </div>
              <div class="text-xs text-gray-500 whitespace-nowrap">
                S/ {{ h.estimatedCost || 0 }} · {{ h.createdAt?.substring(0, 10) || 'N/A' }}
              </div>
            </div>
          </div>
          <div *ngIf="viewing.history && viewing.history.length === 0"
               class="text-sm text-gray-500 italic">No hay órdenes previas para este vehículo.</div>
        </div>
      </div>
    </app-modal>
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
  viewing: any = null;
  historyLoading = false;

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
      // Open the modal immediately with the row, then fetch history async.
      this.viewing = { ...event.row, history: [] };
      this.historyLoading = true;
      this.api.get<any[]>(`/work-orders/vehicle/${event.row.vehicleId}`).subscribe({
        next: (history) => {
          if (this.viewing && this.viewing.id === event.row.id) {
            this.viewing = { ...this.viewing, history };
          }
          this.historyLoading = false;
        },
        error: () => { this.historyLoading = false; }
      });
    }
  }
}