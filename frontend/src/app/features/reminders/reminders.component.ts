import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';

@Component({
  selector: 'app-reminders',
  standalone: true,
  imports: [DataTableComponent, FormsModule],
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
        [actions]="actions">
      </app-data-table>
    </div>
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
  actions = [{ label: 'Cancelar', class: 'btn-danger' }];
  reminders: any[] = [];
  showForm = false;
  form = { title: '', customerId: 1, vehicleId: 1, type: 'MAINTENANCE_DUE', scheduledDate: '', channel: 'WHATSAPP' };
  message = '';
  ok = false;

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
}
