import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  template: `
    <div>
      <h1 class="text-2xl font-bold mb-6">Configuración</h1>
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card">
          <h3 class="font-semibold mb-4">🏢 Perfil del Taller</h3>
          <p><strong>Negocio:</strong> {{ tenant?.businessName }}</p>
          <p><strong>Plan:</strong> {{ tenant?.plan }}</p>
          <p><strong>Teléfono:</strong> {{ tenant?.phone }}</p>
        </div>
        <div class="card">
          <h3 class="font-semibold mb-4">💬 WhatsApp Config</h3>
          <p class="text-sm text-gray-500">La integración con WhatsApp está en modo mock.</p>
          <p class="text-sm text-gray-500">Configure sus credenciales de Meta Cloud API cuando estén disponibles.</p>
        </div>
      </div>
    </div>
  `
})
export class SettingsComponent implements OnInit {
  tenant: any = {};

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<any>('/tenant').subscribe({
      next: (data) => this.tenant = data
    });
  }
}
