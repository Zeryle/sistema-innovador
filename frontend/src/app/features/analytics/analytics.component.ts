import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/auth/auth.service';
import { environment } from '../../../environments/environment';

interface TopCustomer {
  customerId: number;
  fullName: string;
  totalOrders: number;
  totalSpend: number;
}

interface TrendPoint {
  month: string;
  orders: number;
  revenue: number;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Analíticas</h1>
        <button (click)="downloadExcel()"
                [disabled]="downloading"
                class="px-5 py-2.5 rounded-lg bg-emerald-600 text-white font-semibold hover:bg-emerald-700 transition shadow-sm disabled:opacity-50 flex items-center gap-2">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
          {{ downloading ? 'Generando...' : 'Exportar a Excel' }}
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div class="card text-center">
          <p class="text-sm text-gray-500">Órdenes Totales</p>
          <p class="text-3xl font-bold text-primary-700">{{ dashboard.totalOrders || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Órdenes Activas</p>
          <p class="text-3xl font-bold text-yellow-700">{{ dashboard.activeOrders || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Clientes</p>
          <p class="text-3xl font-bold text-blue-700">{{ dashboard.totalCustomers || 0 }}</p>
        </div>
        <div class="card text-center">
          <p class="text-sm text-gray-500">Vehículos</p>
          <p class="text-3xl font-bold text-green-700">{{ dashboard.totalVehicles || 0 }}</p>
        </div>
      </div>

      <!-- Premium: comparativa vs mes anterior -->
      <div *ngIf="comparison" class="card mb-6">
        <div class="flex items-center gap-2 mb-4">
          <h3 class="font-semibold">Comparativa vs mes anterior</h3>
          <span class="text-xs px-2 py-0.5 rounded-full bg-purple-100 text-purple-700 font-medium">PREMIUM</span>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="border border-gray-200 rounded-lg p-4">
            <div class="text-xs text-gray-500 uppercase font-medium mb-1">Órdenes</div>
            <div class="flex items-baseline gap-2">
              <span class="text-2xl font-bold">{{ comparison.thisMonth.orders }}</span>
              <span class="text-sm text-gray-500">vs {{ comparison.lastMonth.orders }}</span>
            </div>
            <div class="text-sm font-semibold mt-1"
                 [class.text-emerald-600]="comparison.ordersDelta > 0"
                 [class.text-red-600]="comparison.ordersDelta < 0"
                 [class.text-gray-500]="comparison.ordersDelta === 0">
              {{ comparison.ordersDelta > 0 ? '+' : '' }}{{ comparison.ordersDelta }}%
            </div>
          </div>
          <div class="border border-gray-200 rounded-lg p-4">
            <div class="text-xs text-gray-500 uppercase font-medium mb-1">Ingresos (S/)</div>
            <div class="flex items-baseline gap-2">
              <span class="text-2xl font-bold">{{ comparison.thisMonth.revenue | number:'1.0-0' }}</span>
              <span class="text-sm text-gray-500">vs {{ comparison.lastMonth.revenue | number:'1.0-0' }}</span>
            </div>
            <div class="text-sm font-semibold mt-1"
                 [class.text-emerald-600]="comparison.revenueDelta > 0"
                 [class.text-red-600]="comparison.revenueDelta < 0"
                 [class.text-gray-500]="comparison.revenueDelta === 0">
              {{ comparison.revenueDelta > 0 ? '+' : '' }}{{ comparison.revenueDelta }}%
            </div>
          </div>
          <div class="border border-gray-200 rounded-lg p-4">
            <div class="text-xs text-gray-500 uppercase font-medium mb-1">Nuevos clientes</div>
            <div class="flex items-baseline gap-2">
              <span class="text-2xl font-bold">{{ comparison.thisMonth.newCustomers }}</span>
              <span class="text-sm text-gray-500">vs {{ comparison.lastMonth.newCustomers }}</span>
            </div>
            <div class="text-sm font-semibold mt-1"
                 [class.text-emerald-600]="comparison.customersDelta > 0"
                 [class.text-red-600]="comparison.customersDelta < 0"
                 [class.text-gray-500]="comparison.customersDelta === 0">
              {{ comparison.customersDelta > 0 ? '+' : '' }}{{ comparison.customersDelta }}%
            </div>
          </div>
        </div>
      </div>

      <!-- Premium: tendencia mensual + top customers -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div class="card">
          <div class="flex items-center gap-2 mb-4">
            <h3 class="font-semibold">Tendencia mensual (últimos 6 meses)</h3>
            <span class="text-xs px-2 py-0.5 rounded-full bg-purple-100 text-purple-700 font-medium">PREMIUM</span>
          </div>
          <div *ngIf="trend.length > 0" class="space-y-2">
            <div *ngFor="let p of trend" class="flex items-center gap-3">
              <span class="w-20 text-xs text-gray-500 font-mono">{{ p.month }}</span>
              <div class="flex-1 bg-gray-100 rounded-full h-6 relative">
                <div class="bg-primary-500 h-6 rounded-full flex items-center justify-end px-2"
                     [style.width.%]="(p.orders / maxOrders * 100) || 5">
                  <span class="text-xs text-white font-semibold">{{ p.orders }}</span>
                </div>
              </div>
              <span class="w-20 text-right text-sm font-semibold">S/ {{ p.revenue | number:'1.0-0' }}</span>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="flex items-center gap-2 mb-4">
            <h3 class="font-semibold">Top clientes por gasto</h3>
            <span class="text-xs px-2 py-0.5 rounded-full bg-purple-100 text-purple-700 font-medium">PREMIUM</span>
          </div>
          <div *ngIf="topCustomers.length > 0" class="space-y-2">
            <div *ngFor="let c of topCustomers; let i = index"
                 class="flex items-center gap-3 py-2 border-b border-gray-100 last:border-0">
              <span class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold"
                    [class.bg-yellow-100]="i === 0"
                    [class.text-yellow-700]="i === 0"
                    [class.bg-gray-200]="i > 0"
                    [class.text-gray-600]="i > 0">{{ i + 1 }}</span>
              <span class="flex-1 font-medium">{{ c.fullName }}</span>
              <span class="text-sm text-gray-500">{{ c.totalOrders }} ord.</span>
              <span class="text-sm font-semibold text-emerald-700">S/ {{ c.totalSpend | number:'1.0-0' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Original: órdenes por estado -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card">
          <h3 class="font-semibold mb-4">Órdenes por Estado</h3>
          @if (statusList.length > 0) {
            @for (s of statusList; track s.status) {
              <div class="flex justify-between py-2 border-b border-gray-100">
                <span class="text-sm">{{ s.status }}</span>
                <span class="text-sm font-semibold">{{ s.count }}</span>
              </div>
            }
          }
        </div>
        <div class="card">
          <h3 class="font-semibold mb-4">Desglose por tipo de servicio</h3>
          <span class="text-xs px-2 py-0.5 rounded-full bg-purple-100 text-purple-700 font-medium">PREMIUM</span>
          <div *ngIf="serviceBreakdown.length > 0" class="mt-3 space-y-2">
            <div *ngFor="let s of serviceBreakdown" class="flex justify-between py-2 border-b border-gray-100">
              <span class="text-sm">{{ s.category }}</span>
              <span class="text-sm font-semibold">{{ s.count }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .card { @apply bg-white rounded-xl border border-gray-200 p-5; }
  `]
})
export class AnalyticsComponent implements OnInit {
  dashboard: any = {};
  statusList: { status: string; count: number }[] = [];
  comparison: any = null;
  trend: TrendPoint[] = [];
  topCustomers: TopCustomer[] = [];
  serviceBreakdown: { category: string; count: number }[] = [];
  maxOrders = 0;
  downloading = false;

  constructor(private api: ApiService, public auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    // Basic dashboard
    this.api.get<any>('/analytics/dashboard').subscribe({
      next: (d) => {
        this.dashboard = d;
        const orderByStatus = d.ordersByStatus || {};
        this.statusList = Object.keys(orderByStatus).map(k => ({ status: k, count: orderByStatus[k] }));
      }
    });
    // Premium: period comparison
    this.api.get<any>('/analytics/advanced/period-comparison').subscribe({
      next: (c) => this.comparison = c
    });
    // Premium: monthly trend
    this.api.get<any>('/analytics/advanced/monthly-trend?months=6').subscribe({
      next: (t) => {
        this.trend = t.series || [];
        this.maxOrders = Math.max(1, ...this.trend.map(p => p.orders));
      }
    });
    // Premium: top customers
    this.api.get<TopCustomer[]>('/analytics/advanced/top-customers?limit=5').subscribe({
      next: (c) => this.topCustomers = c || []
    });
    // Premium: service breakdown
    this.api.get<any[]>('/analytics/advanced/service-breakdown').subscribe({
      next: (s) => this.serviceBreakdown = s || []
    });
  }

  downloadExcel(): void {
    if (this.downloading) return;
    this.downloading = true;
    const url = environment.apiUrl + '/analytics/export/xlsx';
    const token = this.auth.getToken();
    fetch(url, { headers: { 'Authorization': 'Bearer ' + token } })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.blob();
      })
      .then(blob => {
        const a = document.createElement('a');
        const objectUrl = URL.createObjectURL(blob);
        a.href = objectUrl;
        a.download = 'reporte-autotaller-' + new Date().toISOString().substring(0, 10) + '.xlsx';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(objectUrl);
        this.downloading = false;
      })
      .catch(err => {
        console.error('Excel download failed', err);
        this.downloading = false;
      });
  }
}