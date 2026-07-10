import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';

@Component({
  selector: 'app-vehicles',
  standalone: true,
  imports: [DataTableComponent, FormsModule, CommonModule, ModalComponent],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Vehículos</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancelar' : '+ Nuevo Vehículo' }}</button>
      </div>

      @if (showForm) {
        <div class="card mb-6">
          <h3 class="font-semibold mb-4">Registrar Vehículo</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <input [(ngModel)]="form.plate" placeholder="Placa (ej: ABC-123)" class="input-field">
            <input [(ngModel)]="form.make" placeholder="Marca (ej: Toyota)" class="input-field">
            <input [(ngModel)]="form.model" placeholder="Modelo (ej: Corolla)" class="input-field">
            <input [(ngModel)]="form.year" type="number" placeholder="Año" class="input-field">
            <input [(ngModel)]="form.color" placeholder="Color" class="input-field">
            <input [(ngModel)]="form.mileage" type="number" placeholder="Kilometraje" class="input-field">
            <input [(ngModel)]="form.customerId" type="number" placeholder="ID del Cliente" class="input-field">
            <select [(ngModel)]="form.fuelType" class="input-field">
              <option value="GASOLINE">Gasolina</option>
              <option value="DIESEL">Diesel</option>
              <option value="GAS">Gas</option>
              <option value="ELECTRIC">Eléctrico</option>
              <option value="HYBRID">Híbrido</option>
            </select>
          </div>
          <button class="btn-primary mt-4" (click)="createVehicle()">Registrar Vehículo</button>
          @if (message) { <p class="mt-2 text-sm" [class.text-green-600]="ok" [class.text-red-600]="!ok">{{ message }}</p> }
        </div>
      }

      <app-data-table
        [columns]="columns"
        [data]="vehicles"
        [actions]="actions"
        [showSearch]="true"
        (search)="onSearch($event)"
        (actionClick)="onAction($event)">
      </app-data-table>
    </div>

    <app-modal [open]="!!viewing"
               title="Detalle del vehículo"
               tone="info"
               icon="🚗"
               size="md"
               cancelLabel="Cerrar"
               (close)="viewing = null">
      <div *ngIf="viewing" class="space-y-3">
        <div class="flex items-baseline gap-3">
          <div class="text-3xl font-extrabold text-gray-900 font-mono">{{ viewing.plate }}</div>
          <span class="px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 text-xs font-medium">{{ viewing.year }}</span>
        </div>
        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Marca / Modelo</div>
          <div class="text-lg font-semibold text-gray-900">{{ viewing.make }} {{ viewing.model }}</div>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Color</div>
            <div class="text-sm text-gray-900">{{ viewing.color || '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Combustible</div>
            <div class="text-sm text-gray-900">{{ viewing.fuelType || '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">Kilometraje</div>
            <div class="text-sm text-gray-900">{{ viewing.mileage ? viewing.mileage + ' km' : '—' }}</div>
          </div>
          <div>
            <div class="text-xs uppercase font-medium text-gray-500">VIN</div>
            <div class="text-sm text-gray-900 font-mono">{{ viewing.vin || '—' }}</div>
          </div>
        </div>
        <div>
          <div class="text-xs uppercase font-medium text-gray-500">Propietario</div>
          <div class="text-sm text-gray-900">{{ viewing.customerName || '—' }}</div>
        </div>
      </div>
    </app-modal>
  `
})
export class VehiclesComponent implements OnInit {
  columns = [
    { key: 'plate', label: 'Placa' },
    { key: 'make', label: 'Marca' },
    { key: 'model', label: 'Modelo' },
    { key: 'year', label: 'Año' },
    { key: 'customerName', label: 'Cliente' }
  ];
  actions = [{ label: 'Ver', class: 'btn-primary' }];
  vehicles: any[] = [];
  showForm = false;
  form = { plate: '', make: '', model: '', year: 2024, color: '', mileage: 0, customerId: 1, fuelType: 'GASOLINE' };
  message = '';
  ok = false;
  viewing: any = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.loadVehicles(); }

  loadVehicles(query?: string): void {
    this.api.get<any[]>('/vehicles', { query, page: 0, size: 50 }).subscribe({
      next: (data) => this.vehicles = data
    });
  }

  createVehicle(): void {
    this.api.post<any>('/vehicles', this.form).subscribe({
      next: () => {
        this.message = 'Vehículo registrado exitosamente';
        this.ok = true;
        this.showForm = false;
        this.form = { plate: '', make: '', model: '', year: 2024, color: '', mileage: 0, customerId: 1, fuelType: 'GASOLINE' };
        this.loadVehicles();
      },
      error: () => { this.message = 'Error al registrar vehículo'; this.ok = false; }
    });
  }

  onSearch(query: string): void { this.loadVehicles(query); }

  onAction(event: { action: string; row: any }): void {
    if (event.action === 'Ver') {
      this.viewing = event.row;
    }
  }
}