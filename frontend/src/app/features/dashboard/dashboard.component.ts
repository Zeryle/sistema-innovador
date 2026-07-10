import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartOptions } from 'chart.js';
import { ApiService } from '../../core/services/api.service';
import { BillingService, SubscriptionStatus } from '../../core/services/billing.service';

interface DashboardStats {
  activeOrders: number;
  totalOrders: number;
  totalCustomers: number;
  totalVehicles: number;
  pendingReminders: number;
  ordersByStatus: { [key: string]: number };
}

interface WorkOrderRow {
  id: number;
  vehiclePlate: string;
  customerName: string;
  status: string;
  description: string;
  priority: string;
  finalCost: number;
  estimatedCost: number;
  createdAt: string;
}

interface ReminderRow {
  id: number;
  customerName: string;
  vehiclePlate: string;
  title: string;
  type: string;
  channel: string;
  scheduledDate: string;
  status: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, BaseChartDirective],
  template: `
    <div class="space-y-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p class="text-gray-500 text-sm mt-1">Resumen general de tu taller</p>
      </div>

      <!-- STATS CARDS -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Órdenes activas -->
        <div class="card cursor-pointer hover:shadow-lg hover:-translate-y-0.5 transition-all"
             routerLink="/app/work-orders">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 font-medium">Órdenes Activas</p>
              <p class="text-3xl font-bold text-yellow-700 mt-1">{{ stats.activeOrders || 0 }}</p>
              <p class="text-xs text-gray-400 mt-1">de {{ stats.totalOrders || 0 }} totales</p>
            </div>
            <div class="w-11 h-11 rounded-xl bg-yellow-100 text-yellow-700 flex items-center justify-center text-xl">🔧</div>
          </div>
          <div class="mt-3 h-1 bg-yellow-100 rounded-full overflow-hidden">
            <div class="h-full bg-yellow-500"
                 [style.width.%]="activePercent"></div>
          </div>
        </div>

        <!-- Clientes -->
        <div class="card cursor-pointer hover:shadow-lg hover:-translate-y-0.5 transition-all"
             routerLink="/app/customers">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 font-medium">Clientes</p>
              <p class="text-3xl font-bold text-blue-700 mt-1">{{ stats.totalCustomers || 0 }}</p>
              <p class="text-xs text-gray-400 mt-1">registrados</p>
            </div>
            <div class="w-11 h-11 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center text-xl">👥</div>
          </div>
        </div>

        <!-- Vehículos -->
        <div class="card cursor-pointer hover:shadow-lg hover:-translate-y-0.5 transition-all"
             routerLink="/app/vehicles">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 font-medium">Vehículos</p>
              <p class="text-3xl font-bold text-green-700 mt-1">{{ stats.totalVehicles || 0 }}</p>
              <p class="text-xs text-gray-400 mt-1">en cartera</p>
            </div>
            <div class="w-11 h-11 rounded-xl bg-green-100 text-green-700 flex items-center justify-center text-xl">🚗</div>
          </div>
        </div>

        <!-- Recordatorios pendientes -->
        <div class="card cursor-pointer hover:shadow-lg hover:-translate-y-0.5 transition-all"
             routerLink="/app/reminders">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 font-medium">Recordatorios</p>
              <p class="text-3xl font-bold text-purple-700 mt-1">{{ stats.pendingReminders || 0 }}</p>
              <p class="text-xs text-gray-400 mt-1">pendientes</p>
            </div>
            <div class="w-11 h-11 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center text-xl">⏰</div>
          </div>
        </div>
      </div>

      <!-- PLAN ACTUAL -->
      <div class="card flex flex-col md:flex-row md:items-center gap-4">
        <div class="flex items-center gap-3 flex-1 min-w-0">
          <div class="w-12 h-12 rounded-xl flex items-center justify-center text-2xl flex-shrink-0"
               [class.bg-gray-100]="subscription?.currentPlan === 'FREE'"
               [class.bg-blue-100]="subscription?.currentPlan === 'BASIC'"
               [class.bg-purple-100]="subscription?.currentPlan === 'PREMIUM'">
            <span [class.text-gray-700]="subscription?.currentPlan === 'FREE'"
                  [class.text-blue-700]="subscription?.currentPlan === 'BASIC'"
                  [class.text-purple-700]="subscription?.currentPlan === 'PREMIUM'">
              {{ subscription?.currentPlan === 'FREE' ? '🆓' : (subscription?.currentPlan === 'PREMIUM' ? '💎' : '⚡') }}
            </span>
          </div>
          <div class="min-w-0">
            <div class="flex items-center gap-2 flex-wrap">
              <h2 class="text-lg font-semibold text-gray-900">Tu plan actual</h2>
              <span class="text-xs px-2 py-0.5 rounded-full font-bold"
                    [class.bg-gray-200]="subscription?.currentPlan === 'FREE'"
                    [class.text-gray-700]="subscription?.currentPlan === 'FREE'"
                    [class.bg-blue-100]="subscription?.currentPlan === 'BASIC'"
                    [class.text-blue-700]="subscription?.currentPlan === 'BASIC'"
                    [class.bg-purple-100]="subscription?.currentPlan === 'PREMIUM'"
                    [class.text-purple-700]="subscription?.currentPlan === 'PREMIUM'">
                {{ subscription?.currentPlanName || 'Cargando…' }}
              </span>
            </div>
            <p class="text-sm text-gray-600">
              <ng-container *ngIf="subscription?.currentPlan === 'FREE'">
                Plan gratuito · Hasta {{ subscription?.maxCustomers }} clientes
              </ng-container>
              <ng-container *ngIf="subscription?.currentPlan === 'BASIC'">
                S/ {{ subscription?.currentMonthlyPrice | number:'1.0-0' }} / mes ·
                {{ subscription?.whatsappEnabled ? 'WhatsApp habilitado' : 'Sin WhatsApp' }} ·
                {{ subscription?.analyticsEnabled ? 'Analítica activa' : 'Sin analítica' }}
              </ng-container>
              <ng-container *ngIf="subscription?.currentPlan === 'PREMIUM'">
                S/ {{ subscription?.currentMonthlyPrice | number:'1.0-0' }} / mes ·
                Soporte prioritario · WhatsApp + analítica
              </ng-container>
            </p>
          </div>
        </div>

        <!-- Feature usage / over-limit warnings -->
        <div class="flex flex-wrap items-center gap-2 text-xs">
          <span *ngIf="subscription?.overCustomerLimit"
                class="px-2 py-1 rounded-full bg-red-100 text-red-700 font-medium">
            ⚠ Excediste el límite de clientes
          </span>
          <span *ngIf="subscription?.overWorkOrderLimit"
                class="px-2 py-1 rounded-full bg-red-100 text-red-700 font-medium">
            ⚠ Excediste el límite de órdenes / mes
          </span>
        </div>

        <div class="flex items-center gap-2 flex-shrink-0">
          <a routerLink="/pricing"
             class="px-4 py-2 rounded-lg text-sm font-medium text-primary-700 hover:bg-primary-50 transition">
            Ver planes
          </a>
          <a *ngIf="subscription?.currentPlan !== 'PREMIUM' && (subscription?.availableUpgrades?.length || 0) > 0"
             routerLink="/pricing"
             [queryParams]="{from: 'dashboard'}"
             class="px-4 py-2 rounded-lg text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 transition shadow-sm">
            Subir de plan
          </a>
        </div>
      </div>

      <!-- CHARTS ROW -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <!-- Bar chart: órdenes por estado -->
        <div class="card lg:col-span-2">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">Órdenes por estado</h2>
              <p class="text-xs text-gray-500">Distribución actual del taller</p>
            </div>
            <span class="badge-info">{{ stats.totalOrders || 0 }} órdenes</span>
          </div>
          <div class="h-72">
            <canvas baseChart
                    [type]="'bar'"
                    [data]="statusBarData"
                    [options]="barOptions"></canvas>
          </div>
        </div>

        <!-- Doughnut chart: composición -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Composición</h2>
          <div class="h-72 flex items-center justify-center">
            <canvas baseChart
                    [type]="'doughnut'"
                    [data]="doughnutData"
                    [options]="doughnutOptions"></canvas>
          </div>
        </div>
      </div>

      <!-- BOTTOM ROW: recent orders + reminders -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <!-- Órdenes recientes -->
        <div class="card">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">Órdenes recientes</h2>
              <p class="text-xs text-gray-500">Últimas {{ recentOrders.length }} del taller</p>
            </div>
            <a routerLink="/app/work-orders" class="text-sm text-primary-600 hover:text-primary-700 font-medium">Ver todas →</a>
          </div>
          @if (recentOrders.length > 0) {
            <div class="space-y-2">
              @for (o of recentOrders; track o.id) {
                <div class="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition border border-gray-100">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 mb-1">
                      <span class="font-medium text-gray-900 text-sm truncate">{{ o.customerName }}</span>
                      <span class="text-xs text-gray-500">·</span>
                      <span class="text-xs text-gray-500 font-mono">{{ o.vehiclePlate }}</span>
                    </div>
                    <p class="text-xs text-gray-500 truncate">{{ o.description }}</p>
                  </div>
                  <div class="ml-3 flex flex-col items-end gap-1">
                    <span class="text-xs px-2 py-0.5 rounded-full font-medium" [class]="statusBadgeClass(o.status)">
                      {{ translateStatus(o.status) }}
                    </span>
                    <span class="text-xs text-gray-500">S/ {{ formatCost(o.estimatedCost) }}</span>
                  </div>
                </div>
              }
            </div>
          } @else {
            <p class="text-sm text-gray-400 text-center py-8">Sin órdenes registradas todavía.</p>
          }
        </div>

        <!-- Próximos recordatorios -->
        <div class="card">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">Próximos recordatorios</h2>
              <p class="text-xs text-gray-500">Programados para envío</p>
            </div>
            <a routerLink="/app/reminders" class="text-sm text-primary-600 hover:text-primary-700 font-medium">Ver todos →</a>
          </div>
          @if (upcomingReminders.length > 0) {
            <div class="space-y-2">
              @for (r of upcomingReminders; track r.id) {
                <div class="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 transition border border-gray-100">
                  <div class="w-9 h-9 rounded-full flex items-center justify-center text-base"
                       [class]="channelClass(r.channel)">
                    {{ channelIcon(r.channel) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="font-medium text-gray-900 text-sm truncate">{{ r.title }}</p>
                    <p class="text-xs text-gray-500 truncate">
                      {{ r.customerName }} · {{ r.vehiclePlate }}
                    </p>
                  </div>
                  <div class="text-right">
                    <p class="text-xs font-medium text-gray-700">{{ formatDate(r.scheduledDate) }}</p>
                    <p class="text-xs text-gray-400">{{ translateType(r.type) }}</p>
                  </div>
                </div>
              }
            </div>
          } @else {
            <p class="text-sm text-gray-400 text-center py-8">No hay recordatorios pendientes.</p>
          }
        </div>
      </div>

      <!-- Acciones rápidas -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <a routerLink="/app/customers" class="card hover:shadow-lg hover:-translate-y-0.5 transition text-center py-6">
          <div class="text-3xl mb-2">👥</div>
          <h3 class="font-semibold text-gray-900">Gestionar Clientes</h3>
          <p class="text-xs text-gray-500 mt-1">Administrar base de datos</p>
        </a>
        <a routerLink="/app/vehicles" class="card hover:shadow-lg hover:-translate-y-0.5 transition text-center py-6">
          <div class="text-3xl mb-2">🚗</div>
          <h3 class="font-semibold text-gray-900">Gestionar Vehículos</h3>
          <p class="text-xs text-gray-500 mt-1">Flota registrada</p>
        </a>
        <a routerLink="/app/work-orders" class="card hover:shadow-lg hover:-translate-y-0.5 transition text-center py-6">
          <div class="text-3xl mb-2">🔧</div>
          <h3 class="font-semibold text-gray-900">Nueva Orden de Trabajo</h3>
          <p class="text-xs text-gray-500 mt-1">Crear y asignar tareas</p>
        </a>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    activeOrders: 0,
    totalOrders: 0,
    totalCustomers: 0,
    totalVehicles: 0,
    pendingReminders: 0,
    ordersByStatus: {}
  };

  recentOrders: WorkOrderRow[] = [];
  upcomingReminders: ReminderRow[] = [];
  subscription: SubscriptionStatus | null = null;

  // % de órdenes activas vs totales (para barra de progreso)
  get activePercent(): number {
    if (!this.stats.totalOrders) return 0;
    return Math.round((this.stats.activeOrders / this.stats.totalOrders) * 100);
  }

  // Chart data
  statusBarData: ChartData<'bar'> = { labels: [], datasets: [] };
  doughnutData: ChartData<'doughnut'> = { labels: [], datasets: [] };

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (ctx) => ` ${ctx.parsed.y} órdenes`
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: { stepSize: 1, color: '#6b7280', font: { size: 11 } },
        grid: { color: '#f3f4f6' }
      },
      x: {
        ticks: { color: '#6b7280', font: { size: 11 } },
        grid: { display: false }
      }
    }
  };

  doughnutOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '65%',
    plugins: {
      legend: {
        position: 'bottom',
        labels: { font: { size: 11 }, color: '#374151', boxWidth: 12, padding: 12 }
      }
    }
  };

  constructor(private api: ApiService, private billing: BillingService) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadRecentOrders();
    this.loadUpcomingReminders();
    this.loadSubscription();
  }

  loadSubscription(): void {
    this.billing.getSubscription().subscribe({
      next: (s) => this.subscription = s,
      error: (err) => console.error('Error cargando suscripción', err)
    });
  }

  loadStats(): void {
    this.api.get<DashboardStats>('/analytics/dashboard').subscribe({
      next: (data) => {
        this.stats = data;
        this.buildCharts();
        // Force chart.js to recompute sizes after the DOM has fully rendered
        setTimeout(() => window.dispatchEvent(new Event('resize')), 0);
      },
      error: (err) => console.error('Error cargando stats', err)
    });
  }

  loadRecentOrders(): void {
    this.api.get<WorkOrderRow[]>('/work-orders').subscribe({
      next: (orders) => {
        this.recentOrders = [...(orders || [])]
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5);
      },
      error: () => this.recentOrders = []
    });
  }

  loadUpcomingReminders(): void {
    this.api.get<ReminderRow[]>('/reminders').subscribe({
      next: (reminders) => {
        this.upcomingReminders = [...(reminders || [])]
          .filter(r => r.status === 'SCHEDULED')
          .sort((a, b) => new Date(a.scheduledDate).getTime() - new Date(b.scheduledDate).getTime())
          .slice(0, 5);
      },
      error: () => this.upcomingReminders = []
    });
  }

  buildCharts(): void {
    // Bar: status -> count (translated labels)
    const entries = Object.entries(this.stats.ordersByStatus || {});
    const labels = entries.map(([s]) => this.translateStatus(s));
    const counts = entries.map(([, n]) => n);
    const colorMap: { [k: string]: string } = {
      RECEIVED: 'rgba(59, 130, 246, 0.85)',
      IN_PROGRESS: 'rgba(245, 158, 11, 0.85)',
      COMPLETED: 'rgba(16, 185, 129, 0.85)',
      CANCELLED: 'rgba(239, 68, 68, 0.85)',
      DELIVERED: 'rgba(99, 102, 241, 0.85)'
    };
    const colors = entries.map(([s]) => colorMap[s] || 'rgba(107, 114, 128, 0.85)');
    const borders = colors.map(c => c.replace(/0\\.85/, '1'));

    this.statusBarData = {
      labels,
      datasets: [{
        data: counts,
        backgroundColor: colors,
        borderColor: borders,
        borderWidth: 1,
        borderRadius: 6,
        maxBarThickness: 50
      }]
    };

    // Doughnut: misma data, diferente visualización
    this.doughnutData = {
      labels,
      datasets: [{
        data: counts,
        backgroundColor: colors,
        borderColor: '#ffffff',
        borderWidth: 2
      }]
    };
  }

  // Helpers
  translateStatus(s: string): string {
    const m: { [k: string]: string } = {
      RECEIVED: 'Recibido',
      IN_PROGRESS: 'En progreso',
      COMPLETED: 'Completado',
      CANCELLED: 'Cancelado',
      DELIVERED: 'Entregado'
    };
    return m[s] || s;
  }

  translateType(t: string): string {
    const m: { [k: string]: string } = {
      OIL_CHANGE: 'Cambio de aceite',
      MAINTENANCE_DUE: 'Mantenimiento',
      TIRE_ROTATION: 'Rotación neumáticos',
      INSPECTION: 'Inspección',
      FOLLOW_UP: 'Seguimiento',
      PAYMENT_DUE: 'Pago pendiente',
      CUSTOM: 'Personalizado'
    };
    return m[t] || t;
  }

  statusBadgeClass(s: string): string {
    const m: { [k: string]: string } = {
      RECEIVED: 'bg-blue-100 text-blue-800',
      IN_PROGRESS: 'bg-yellow-100 text-yellow-800',
      COMPLETED: 'bg-green-100 text-green-800',
      CANCELLED: 'bg-red-100 text-red-800',
      DELIVERED: 'bg-indigo-100 text-indigo-800'
    };
    return m[s] || 'bg-gray-100 text-gray-800';
  }

  channelIcon(c: string): string {
    return c === 'WHATSAPP' ? '💬' : c === 'EMAIL' ? '📧' : '📨';
  }

  channelClass(c: string): string {
    return c === 'WHATSAPP' ? 'bg-green-100 text-green-600' : 'bg-blue-100 text-blue-600';
  }

  formatCost(n: number | null | undefined): string {
    if (n === null || n === undefined) return '0.00';
    return Number(n).toFixed(2);
  }

  formatDate(d: string | null | undefined): string {
    if (!d) return '';
    const date = new Date(d);
    return date.toLocaleDateString('es-PE', { day: '2-digit', month: 'short' });
  }
}
