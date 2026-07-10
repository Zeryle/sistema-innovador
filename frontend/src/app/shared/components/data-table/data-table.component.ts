import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [NgClass],
  template: `
    <div class="card overflow-hidden">
      @if (showSearch) {
        <div class="p-4 border-b border-gray-100">
          <input type="text" class="input-field max-w-sm" placeholder="Buscar..."
                 [value]="searchQuery" (input)="onSearch($event)">
        </div>
      }
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            <tr>
              @for (col of columns; track col.key) {
                <th class="px-4 py-3">{{ col.label }}</th>
              }
              @if (actions.length > 0) {
                <th class="px-4 py-3 text-right">Acciones</th>
              }
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            @for (row of data; track row.id) {
              <tr class="hover:bg-gray-50 transition-colors">
                @for (col of columns; track col.key) {
                  <td class="px-4 py-3 text-sm text-gray-700">{{ row[col.key] }}</td>
                }
                @if (actions.length > 0) {
                  <td class="px-4 py-3 text-right whitespace-nowrap">
                    @for (action of actions; track action.label) {
                      <button (click)="onAction(action.label, row)"
                              class="ml-2 text-xs px-3 py-1 rounded transition"
                              [ngClass]="action.class || 'btn-primary'">
                        {{ action.label }}
                      </button>
                    }
                  </td>
                }
              </tr>
            }
          </tbody>
        </table>
      </div>
      @if (!data || data.length === 0) {
        <div class="p-12 text-center text-gray-400">
          No se encontraron registros
        </div>
      }
    </div>
  `
})
export class DataTableComponent {
  @Input() columns: { key: string; label: string }[] = [];
  @Input() data: any[] = [];
  @Input() actions: { label: string; class?: string }[] = [];
  @Input() showSearch = false;

  @Output() search = new EventEmitter<string>();
  @Output() actionClick = new EventEmitter<{ action: string; row: any }>();

  searchQuery = '';

  onSearch(event: any): void {
    this.searchQuery = event.target.value;
    this.search.emit(this.searchQuery);
  }

  onAction(action: string, row: any): void {
    this.actionClick.emit({ action, row });
  }
}
