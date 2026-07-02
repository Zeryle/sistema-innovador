import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Catálogo de Partes</h1>
        <button class="btn-primary" (click)="showForm = !showForm">{{ showForm ? 'Cancelar' : '+ Nueva Categoría' }}</button>
      </div>

      @if (showForm) {
        <div class="card mb-6">
          <h3 class="font-semibold mb-4">Nueva Categoría de Parte</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input [(ngModel)]="form.name" placeholder="Nombre (ej: Filtro de aire)" class="input-field">
            <input [(ngModel)]="form.description" placeholder="Descripción" class="input-field">
            <input [(ngModel)]="form.parentCategoryId" type="number" placeholder="ID Categoría Padre (opcional)" class="input-field">
          </div>
          <button class="btn-primary mt-4" (click)="createCategory()">Crear Categoría</button>
          @if (message) { <p class="mt-2 text-sm" [class.text-green-600]="ok" [class.text-red-600]="!ok">{{ message }}</p> }
        </div>
      }

      <div class="card">
        <input type="text" class="input-field mb-4" placeholder="Buscar parte... (ej: pastillas, filtro, batería)"
               #searchInput (input)="onSearch(searchInput.value)">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          @for (part of parts; track part.id) {
            <div class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition cursor-pointer">
              <h4 class="font-semibold">{{ part.name }}</h4>
              @if (part.description) {
                <p class="text-sm text-gray-500 mt-1">{{ part.description }}</p>
              }
              @if (part.parentCategoryId) {
                <span class="badge-info mt-2">Subcategoría</span>
              }
            </div>
          }
          @if (parts.length === 0) {
            <p class="text-gray-400 col-span-full text-center py-8">No se encontraron partes</p>
          }
        </div>
      </div>
    </div>
  `
})
export class CatalogComponent implements OnInit {
  parts: any[] = [];
  allParts: any[] = [];
  showForm = false;
  form = { name: '', description: '', parentCategoryId: null as number | null };
  message = '';
  ok = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadParts();
  }

  loadParts(): void {
    this.api.get<any[]>('/catalog/parts').subscribe({
      next: (data) => { this.allParts = data; this.parts = data; }
    });
  }

  createCategory(): void {
    this.api.post<any>('/catalog/parts', this.form).subscribe({
      next: () => {
        this.message = 'Categoría creada exitosamente';
        this.ok = true;
        this.showForm = false;
        this.form = { name: '', description: '', parentCategoryId: null };
        this.loadParts();
      },
      error: () => { this.message = 'Error al crear categoría'; this.ok = false; }
    });
  }

  onSearch(query: string): void {
    if (!query) { this.parts = this.allParts; return; }
    this.api.get<any[]>('/catalog/parts/search', { query }).subscribe({
      next: (data) => this.parts = data
    });
  }
}
