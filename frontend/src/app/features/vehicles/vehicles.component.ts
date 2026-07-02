import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DataTableComponent } from '../../shared/components/data-table/data-table.component';

@Component({
  selector: 'app-vehicles',
  standalone: true,
  imports: [DataTableComponent, FormsModule],
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
      alert(`Vehículo: ${event.row.plate}\n${event.row.make} ${event.row.model} (${event.row.year})\nCliente: ${event.row.customerName}`);
    }
  }
}
