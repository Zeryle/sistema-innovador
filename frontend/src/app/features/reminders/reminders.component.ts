import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';

@Component({
  selector: 'app-reminders',
  standalone: true,
  imports: [DataTableComponent, FormsModule, CommonModule, ModalComponent],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Recordatorios</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancelar' : '+ Nuevo Recordatorio' }}</button>
      </div>

      @if (showForm) {
        <div class="card mb-6">
          <h3 class="font-semibold mb-4">Nuevo Recordatorio</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input [(ngModel)]="form.title" placeholder="Título" class="input-field">
            <input [(ngModel)]="form.customerId" type="number" placeholder="ID del Cliente" class="input-field">
            <input [(ngModel)]="form.vehicleId" type="number" placeholder="ID del Vehículo (opcional)" class="input-field">
            <select [(ngModel)]="form.type" class="input-field">
              <option value="MAINTENANCE_DUE">Mantenimiento</option>
              <option value="OIL_CHANGE">Cambio de Aceite</option>
              <option value="FOLLOW_UP">Seguimiento</option>
              <option value="INSPECTION">Inspección</option>
              <option value="TIRE_ROTATION">Rotación de Llantas</option>
              <option value="CUSTOM">Personalizado</option>
            </select>
            <input [(ngModel)]="form.scheduledDate" type="datetime-local" class="input-field">
            <select [(ngModel)]="form.channel" class="input-field">
              <option value="WHATSAPP">WhatsApp</option>
              <option value="EMAIL">Email</option>
              <option value="BOTH">Ambos</option>
            </select>
          </div>
          <button class="btn-primary mt-4" (click)="createReminder()">Crear Recordatorio</button>
          @if (message) { <p class="mt-2 text-sm" [class.text-green-600]="ok" [class.text-red-600]="!ok">{{ message }}</p> }
        </div>
      }

      <app-data-table
        [columns]="columns"
        [data]="reminders"
        [actions]="actions"
        (actionClick)="onAction($event)">
      </app-data-table>
    </div>

    <app-modal [open]="!!viewing"
               title="Detalle del recordatorio"
               tone="info"
               icon="🔔"
               size="md"
               cancelLabel="Cerrar"
               (close)="viewing = null">
      <div *ngIf="viewing" class="space-y-3">
        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Título</div>
          <div class="text-base font-semibold text-gray-900">{{ viewing.title }}</div>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Cliente</div>
            <div class="text-sm text-gray-900">{{ viewing.customerName || '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Vehículo</div>
            <div class="text-sm text-gray-900">{{ viewing.vehiclePlate || '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Tipo</div>
            <div class="text-sm text-gray-900">{{ viewing.type }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Canal</div>
            <div class="text-sm text-gray-900">{{ viewing.channel }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Programado</div>
            <div class="text-sm text-gray-900">{{ viewing.scheduledDate | date:'medium' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Estado</div>
            <span class="inline-flex px-2 py-0.5 rounded-full text-xs font-medium"
                  [class.bg-blue-100]="viewing.status === 'SCHEDULED'"
                  [class.text-blue-700]="viewing.status === 'SCHEDULED'"
                  [class.bg-yellow-100]="viewing.status === 'SENT'"
                  [class.text-yellow-700]="viewing.status === 'SENT'"
                  [class.bg-green-100]="viewing.status === 'DELIVERED'"
                  [class.text-green-700]="viewing.status === 'DELIVERED'"
                  [class.bg-gray-100]="viewing.status === 'CANCELLED'"
                  [class.text-gray-700]="viewing.status === 'CANCELLED'">
              {{ viewing.status }}
            </span>
          </div>
        </div>
        <div *ngIf="viewing.message">
          <div class="text-xs uppercase font-medium text-gray-500">Mensaje</div>
          <div class="text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">{{ viewing.message }}</div>
        </div>
      </div>
    </app-modal>

    <app-modal [open]="!!confirmingCancel"
               title="¿Cancelar este recordatorio?"
               tone="warning"
               icon="⚠️"
               size="sm"
               cancelLabel="No, mantener"
               confirmLabel="Sí, cancelar"
               (close)="confirmingCancel = null"
               (confirm)="doCancel()">
      <p class="text-sm text-gray-600">
        Vas a cancelar el recordatorio <strong>{{ confirmingCancel?.title }}</strong>.
        El cliente ya no recibirá este mensaje programado.
      </p>
      <p class="text-xs text-gray-500 mt-2">
        Puedes volver a crearlo si te equivocas.
      </p>
    </app-modal>
  `
})
export class RemindersComponent implements OnInit {
  columns = [
    { key: 'title', label: 'Título' },
    { key: 'customerName', label: 'Cliente' },
    { key: 'type', label: 'Tipo' },
    { key: 'scheduledDate', label: 'Programado' },
    { key: 'status', label: 'Estado' },
    { key: 'channel', label: 'Canal' }
  ];
  actions = [
    { label: 'Ver', class: 'btn-primary' },
    { label: 'Cancelar', class: 'btn-danger' }
  ];
  reminders: any[] = [];
  showForm = false;
  form = { title: '', customerId: 1, vehicleId: 1, type: 'MAINTENANCE_DUE', scheduledDate: '', channel: 'WHATSAPP' };
  message = '';
  ok = false;

  viewing: any = null;
  confirmingCancel: any = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadReminders();
  }

  loadReminders(): void {
    this.api.get<any[]>('/reminders').subscribe({
      next: (data) => this.reminders = data
    });
  }

  createReminder(): void {
    this.api.post<any>('/reminders', this.form).subscribe({
      next: () => {
        this.message = 'Recordatorio creado exitosamente';
        this.ok = true;
        this.showForm = false;
        this.form = { title: '', customerId: 1, vehicleId: 1, type: 'MAINTENANCE_DUE', scheduledDate: '', channel: 'WHATSAPP' };
        this.loadReminders();
      },
      error: () => { this.message = 'Error al crear recordatorio'; this.ok = false; }
    });
  }

  onAction(event: { action: string; row: any }): void {
    if (event.action === 'Ver') {
      this.viewing = event.row;
    } else if (event.action === 'Cancelar') {
      this.confirmingCancel = event.row;
    }
  }

  doCancel(): void {
    const r = this.confirmingCancel;
    if (!r) return;
    // The backend exposes DELETE /api/reminders/{id} which flips status to CANCELLED.
    this.api.delete(`/reminders/${r.id}`).subscribe({
      next: () => {
        this.confirmingCancel = null;
        this.loadReminders();
      },
      error: (err: any) => {
        // Fall back to optimistic UI update if the DELETE endpoint fails.
        const idx = this.reminders.findIndex((x: any) => x.id === r.id);
        if (idx >= 0) this.reminders[idx] = { ...this.reminders[idx], status: 'CANCELLED' };
        this.confirmingCancel = null;
        console.error('Cancel failed; updated locally:', err);
      }
    });
  }
}
