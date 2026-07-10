import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [DataTableComponent, FormsModule, CommonModule, ModalComponent],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Clientes</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancelar' : '+ Nuevo Cliente' }}</button>
      </div>

      @if (showForm) {
        <div class="card mb-6">
          <h3 class="font-semibold mb-4">Nuevo Cliente</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input [(ngModel)]="form.name" placeholder="Nombre" class="input-field">
            <input [(ngModel)]="form.lastName" placeholder="Apellido" class="input-field">
            <input [(ngModel)]="form.dni" placeholder="DNI" class="input-field">
            <input [(ngModel)]="form.email" placeholder="Email" class="input-field">
            <input [(ngModel)]="form.phone" placeholder="Teléfono" class="input-field">
            <input [(ngModel)]="form.notes" placeholder="Notas" class="input-field">
          </div>
          <button class="btn-primary mt-4" (click)="createCustomer()">Guardar Cliente</button>
          @if (message) { <p class="mt-2 text-sm" [class.text-green-600]="ok" [class.text-red-600]="!ok">{{ message }}</p> }
        </div>
      }

      <app-data-table
        [columns]="columns"
        [data]="customers"
        [actions]="actions"
        [showSearch]="true"
        (search)="onSearch($event)"
        (actionClick)="onAction($event)">
      </app-data-table>
    </div>

    <app-modal [open]="!!viewing"
               title="Detalle del cliente"
               tone="info"
               icon="👤"
               size="md"
               cancelLabel="Cerrar"
               (close)="viewing = null">
      <div *ngIf="viewing" class="space-y-3">
        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Nombre completo</div>
          <div class="text-lg font-semibold text-gray-900">{{ viewing.fullName }}</div>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">DNI</div>
            <div class="text-sm text-gray-900">{{ viewing.dni || '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Teléfono</div>
            <div class="text-sm text-gray-900">{{ viewing.phone || '—' }}</div>
          </div>
          <div class="col-span-2">
            <div class="text-xs uppercase font-medium text-gray-500">Email</div>
            <div class="text-sm text-gray-900">{{ viewing.email || '—' }}</div>
          </div>
        </div>
        <div *ngIf="viewing.notes">
          <div class="text-xs uppercase font-medium text-gray-500">Notas</div>
          <div class="text-sm text-gray-900 bg-gray-50 p-3 rounded-lg">{{ viewing.notes }}</div>
        </div>
        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Vehículos</div>
          <div class="text-sm text-gray-900">{{ viewing.vehicleCount || 0 }} registrado(s)</div>
        </div>
      </div>
    </app-modal>
  `
})
export class CustomersComponent implements OnInit {
  columns = [
    { key: 'fullName', label: 'Nombre' },
    { key: 'dni', label: 'DNI' },
    { key: 'phone', label: 'Teléfono' },
    { key: 'email', label: 'Email' }
  ];
  actions = [{ label: 'Ver', class: 'btn-primary' }];
  customers: any[] = [];
  showForm = false;
  form = { name: '', lastName: '', dni: '', email: '', phone: '', notes: '' };
  message = '';
  ok = false;
  viewing: any = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.loadCustomers(); }

  loadCustomers(query?: string): void {
    this.api.get<any[]>('/customers', { query, page: 0, size: 50 }).subscribe({
      next: (data) => this.customers = data
    });
  }

  createCustomer(): void {
    this.api.post<any>('/customers', this.form).subscribe({
      next: () => {
        this.message = 'Cliente creado exitosamente';
        this.ok = true;
        this.showForm = false;
        this.form = { name: '', lastName: '', dni: '', email: '', phone: '', notes: '' };
        this.loadCustomers();
      },
      error: () => { this.message = 'Error al crear cliente'; this.ok = false; }
    });
  }

  onSearch(query: string): void { this.loadCustomers(query); }

  onAction(event: { action: string; row: any }): void {
    if (event.action === 'Ver') {
      this.viewing = event.row;
    }
  }
}