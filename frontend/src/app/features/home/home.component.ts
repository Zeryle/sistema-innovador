import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PublicApiService, PublicCategory, PublicTenant } from '../../core/services/public-api.service';
import { AuthService } from '../../core/auth/auth.service';

interface ServiceCard {
  icon: string;
  title: string;
  desc: string;
}

interface ValueProp {
  icon: string;
  title: string;
  desc: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <!-- NAVBAR -->
    <header class="fixed top-0 inset-x-0 z-50 backdrop-blur-md bg-white/80 border-b border-gray-200/60">
      <nav class="max-w-7xl mx-auto px-6 py-3 flex items-center justify-between">
        <a routerLink="/" class="flex items-center gap-2">
          <div class="w-9 h-9 rounded-lg bg-gradient-to-br from-primary-600 to-primary-800 flex items-center justify-center text-white font-bold text-lg shadow-md">🔧</div>
          <span class="font-bold text-gray-900 text-lg">{{ tenant?.businessName || 'AutoTaller' }}</span>
        </a>
        <div class="hidden md:flex items-center gap-7 text-sm font-medium text-gray-700">
          <a href="#servicios" class="hover:text-primary-600 transition">Servicios</a>
          <a href="#tienda" class="hover:text-primary-600 transition">Tienda</a>
          <a href="#nosotros" class="hover:text-primary-600 transition">Nosotros</a>
          <a href="#contacto" class="hover:text-primary-600 transition">Contacto</a>
        </div>
        <div class="flex items-center gap-3">
          <ng-container *ngIf="!isLoggedIn; else navAuthed">
            <a routerLink="/login" class="px-4 py-2 text-sm font-medium text-primary-700 hover:bg-primary-50 rounded-lg transition">Iniciar sesión</a>
            <a routerLink="/register" class="px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg shadow-sm transition">Crear cuenta</a>
          </ng-container>
          <ng-template #navAuthed>
            <a routerLink="/app/dashboard" class="px-4 py-2 text-sm font-medium text-primary-700 hover:bg-primary-50 rounded-lg transition">Ir al dashboard</a>
            <a routerLink="/pricing" class="px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg shadow-sm transition">Ver planes</a>
          </ng-template>
        </div>
      </nav>
    </header>

    <!-- HERO -->
    <section class="relative pt-28 pb-24 overflow-hidden">
      <!-- decorative blobs -->
      <div class="absolute -top-32 -right-32 w-96 h-96 bg-primary-300/30 rounded-full blur-3xl"></div>
      <div class="absolute top-40 -left-32 w-80 h-80 bg-primary-400/20 rounded-full blur-3xl"></div>

      <div class="relative max-w-7xl mx-auto px-6 grid md:grid-cols-2 gap-12 items-center">
        <div>
          <span class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 text-primary-700 text-xs font-semibold mb-5 border border-primary-100">
            <span class="w-1.5 h-1.5 bg-primary-500 rounded-full animate-pulse"></span>
            {{ tenant?.businessName || 'AutoTaller' }} · {{ tenant?.plan || 'SaaS' }}
          </span>
          <h1 class="text-4xl md:text-6xl font-extrabold leading-tight text-gray-900 mb-5">
            Tu taller mecánico
            <span class="bg-gradient-to-r from-primary-600 to-primary-400 bg-clip-text text-transparent"> y repuestos</span>
            en un solo lugar.
          </h1>
          <p class="text-lg text-gray-600 mb-8 leading-relaxed">
            Agenda mantenimientos, consulta el estado de tu vehículo y compra repuestos originales
            sin salir de casa. Gestión profesional para talleres y sus clientes.
          </p>
          <div class="flex flex-wrap gap-3">
            <ng-container *ngIf="!isLoggedIn; else heroAuthed">
            <a routerLink="/login" class="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold shadow-lg shadow-primary-600/30 hover:bg-primary-700 hover:-translate-y-0.5 transition">
              Ingresar al taller
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/></svg>
            </a>
            <a routerLink="/register" class="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-white border border-gray-200 text-gray-700 font-semibold hover:border-primary-300 hover:text-primary-700 transition">
              Registrar mi taller
            </a>
          </ng-container>
          <ng-template #heroAuthed>
            <a routerLink="/app/dashboard" class="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-primary-600 text-white font-semibold shadow-lg shadow-primary-600/30 hover:bg-primary-700 hover:-translate-y-0.5 transition">
              Ir al dashboard
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3"/></svg>
            </a>
            <a routerLink="/pricing" class="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-white border border-gray-200 text-gray-700 font-semibold hover:border-primary-300 hover:text-primary-700 transition">
              Ver planes
            </a>
          </ng-template>
          </div>
          <div class="mt-10 flex items-center gap-6 text-sm text-gray-500">
            <div class="flex items-center gap-2"><span class="text-green-500">●</span> Servicio garantizado</div>
            <div class="flex items-center gap-2"><span class="text-blue-500">●</span> Repuestos originales</div>
            <div class="flex items-center gap-2"><span class="text-primary-500">●</span> Recordatorios automáticos</div>
          </div>
        </div>

        <!-- Hero visual: layered cards -->
        <div class="relative h-[420px] hidden md:block">
          <div class="absolute inset-0 bg-gradient-to-br from-primary-100 to-primary-200 rounded-3xl rotate-3"></div>
          <div class="absolute inset-4 bg-white rounded-2xl shadow-2xl p-6 flex flex-col gap-4">
            <div class="flex items-center justify-between border-b pb-3">
              <div>
                <p class="text-xs text-gray-500">Orden activa</p>
                <p class="font-bold text-gray-900">#WO-1042 · Toyota Corolla 2022</p>
              </div>
              <span class="badge-warning">EN TALLER</span>
            </div>
            <div class="space-y-3 text-sm">
              <div class="flex items-center justify-between">
                <span class="text-gray-600">🔧 Cambio de aceite y filtro</span>
                <span class="text-green-600 font-medium">✓ Listo</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-gray-600">🛞 Rotación de neumáticos</span>
                <span class="text-blue-600 font-medium">⟳ En proceso</span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-gray-600">🛑 Revisión de frenos</span>
                <span class="text-gray-400">⏳ Pendiente</span>
              </div>
            </div>
            <div class="mt-auto p-3 bg-primary-50 rounded-lg">
              <p class="text-xs text-primary-700 font-medium">📅 Próximo mantenimiento en 14 días</p>
            </div>
          </div>
          <!-- floating notification -->
          <div class="absolute -bottom-4 -left-6 bg-white rounded-xl shadow-xl p-4 flex items-center gap-3 border border-gray-100">
            <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center text-xl">📱</div>
            <div>
              <p class="text-xs text-gray-500">WhatsApp</p>
              <p class="text-sm font-medium text-gray-900">Tu orden está lista</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- SERVICES (taller) -->
    <section id="servicios" class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-6">
        <div class="text-center mb-14">
          <span class="text-sm font-semibold text-primary-600 tracking-wider uppercase">Reparación</span>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mt-2 mb-3">Servicios del taller</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">Todo lo que tu vehículo necesita en un solo lugar, atendido por mecánicos certificados.</p>
        </div>
        <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          @for (s of servicios; track s.title) {
            <div class="group p-6 bg-gray-50 hover:bg-white hover:shadow-xl border border-transparent hover:border-primary-100 rounded-2xl transition-all duration-300 cursor-pointer">
              <div class="w-12 h-12 bg-primary-100 group-hover:bg-primary-600 group-hover:text-white text-primary-600 rounded-xl flex items-center justify-center text-2xl mb-4 transition">
                {{ s.icon }}
              </div>
              <h3 class="font-bold text-gray-900 mb-2">{{ s.title }}</h3>
              <p class="text-sm text-gray-600 leading-relaxed">{{ s.desc }}</p>
            </div>
          }
        </div>
      </div>
    </section>

    <!-- TIENDA (categories from BD) -->
    <section id="tienda" class="py-20 bg-gradient-to-br from-gray-50 to-primary-50/30">
      <div class="max-w-7xl mx-auto px-6">
        <div class="text-center mb-14">
          <span class="text-sm font-semibold text-primary-600 tracking-wider uppercase">Tienda</span>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mt-2 mb-3">Repuestos por categoría</h2>
          <p class="text-gray-600 max-w-2xl mx-auto">
            @if (categories.length > 0) {
              Explora las {{ categories.length }} categorías de repuestos disponibles en nuestro catálogo.
            } @else {
              Cargando catálogo...
            }
          </p>
        </div>
        <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          @for (cat of categories; track cat.id) {
            <div class="bg-white p-6 rounded-2xl shadow-sm hover:shadow-xl transition border border-gray-100 group">
              <div class="aspect-square bg-gradient-to-br from-primary-100 to-primary-200 rounded-xl mb-4 flex items-center justify-center text-5xl group-hover:scale-105 transition">
                {{ iconForCategory(cat.name) }}
              </div>
              <h3 class="font-bold text-gray-900 text-lg mb-1">{{ cat.name }}</h3>
              @if (cat.description) {
                <p class="text-sm text-gray-600 mb-3">{{ cat.description }}</p>
              }
              @if (cat.subcategories.length > 0) {
                <div class="flex flex-wrap gap-1.5 mt-3">
                  @for (sub of cat.subcategories; track sub) {
                    <span class="text-xs px-2 py-1 bg-gray-100 text-gray-700 rounded-full">{{ sub }}</span>
                  }
                </div>
              }
              <a [routerLink]="isLoggedIn ? '/app/catalog' : '/login'" class="mt-4 inline-flex items-center gap-1 text-sm font-semibold text-primary-600 hover:text-primary-700">
                Ver catálogo
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
              </a>
            </div>
          }
        </div>
      </div>
    </section>

    <!-- VALUE PROPS -->
    <section id="nosotros" class="py-20 bg-white">
      <div class="max-w-7xl mx-auto px-6">
        <div class="text-center mb-14">
          <span class="text-sm font-semibold text-primary-600 tracking-wider uppercase">Por qué elegirnos</span>
          <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mt-2 mb-3">Diseñado para talleres modernos</h2>
        </div>
        <div class="grid md:grid-cols-3 gap-8">
          @for (v of valores; track v.title) {
            <div class="text-center p-6">
              <div class="w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 text-white rounded-2xl flex items-center justify-center text-3xl mx-auto mb-4 shadow-lg shadow-primary-500/30">
                {{ v.icon }}
              </div>
              <h3 class="font-bold text-gray-900 text-xl mb-2">{{ v.title }}</h3>
              <p class="text-gray-600">{{ v.desc }}</p>
            </div>
          }
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section id="contacto" class="py-20 bg-gradient-to-br from-primary-700 to-primary-900 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-96 h-96 bg-primary-400/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-0 left-0 w-80 h-80 bg-primary-300/20 rounded-full blur-3xl"></div>
      <div class="relative max-w-4xl mx-auto px-6 text-center">
        <h2 class="text-3xl md:text-5xl font-bold text-white mb-5">
          ¿Listo para llevar tu taller al siguiente nivel?
        </h2>
        <p class="text-primary-100 text-lg mb-8 max-w-2xl mx-auto">
          Crea tu cuenta gratis y empieza a gestionar clientes, vehículos y órdenes en minutos.
        </p>
        <div class="flex flex-wrap justify-center gap-3">
          <ng-container *ngIf="!isLoggedIn; else finalAuthed">
            <a routerLink="/register" class="px-8 py-3 bg-white text-primary-700 font-bold rounded-xl shadow-xl hover:shadow-2xl hover:-translate-y-0.5 transition">
              Crear cuenta gratis
            </a>
            <a routerLink="/login" class="px-8 py-3 bg-primary-600/30 backdrop-blur text-white font-bold rounded-xl border border-white/30 hover:bg-primary-600/50 transition">
              Ya tengo cuenta
            </a>
          </ng-container>
          <ng-template #finalAuthed>
            <a routerLink="/app/dashboard" class="px-8 py-3 bg-white text-primary-700 font-bold rounded-xl shadow-xl hover:shadow-2xl hover:-translate-y-0.5 transition">
              Ir al dashboard
            </a>
            <a routerLink="/pricing" class="px-8 py-3 bg-primary-600/30 backdrop-blur text-white font-bold rounded-xl border border-white/30 hover:bg-primary-600/50 transition">
              Ver planes
            </a>
          </ng-template>
        </div>
      </div>
    </section>

    <!-- FOOTER -->
    <footer class="bg-gray-900 text-gray-400 py-10">
      <div class="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-4">
        <div class="flex items-center gap-2">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-600 to-primary-800 flex items-center justify-center text-white font-bold">🔧</div>
          <span class="font-bold text-white">{{ tenant?.businessName || 'AutoTaller' }}</span>
        </div>
        <p class="text-sm">© {{ year }} · Sistema de gestión para talleres mecánicos</p>
        <div class="flex items-center gap-4 text-sm">
          @if (tenant?.phone) {
            <span>📞 {{ tenant!.phone }}</span>
          }
          @if (tenant?.ruc) {
            <span class="hidden md:inline">RUC {{ tenant!.ruc }}</span>
          }
        </div>
      </div>
    </footer>
  `
})
export class HomeComponent implements OnInit {
  get isLoggedIn(): boolean { return this.auth.isLoggedIn(); }
  year = new Date().getFullYear();
  tenant: PublicTenant | null = null;
  categories: PublicCategory[] = [];

  servicios: ServiceCard[] = [
    { icon: '🛢️', title: 'Cambio de aceite', desc: 'Aceite y filtro de motor con mantenimiento programado.' },
    { icon: '🛞', title: 'Alineación y balanceo', desc: 'Geometría computarizada para alargar la vida de tus neumáticos.' },
    { icon: '🛑', title: 'Sistema de frenos', desc: 'Pastillas, discos y líquido de freno con revisión completa.' },
    { icon: '⚡', title: 'Diagnóstico electrónico', desc: 'Escáner OBD-II para detectar fallas en segundos.' },
    { icon: '🔋', title: 'Sistema eléctrico', desc: 'Baterías, alternador y arranque con pruebas de carga.' },
    { icon: '🌡️', title: 'Aire acondicionado', desc: 'Recarga de gas, filtros y revisión del compresor.' },
    { icon: '⛓️', title: 'Transmisión y embrague', desc: 'Servicio de caja mecánica y automática.' },
    { icon: '🛠️', title: 'Suspensión y dirección', desc: 'Amortiguadores, rótulas y alineación de dirección.' }
  ];

  valores: ValueProp[] = [
    { icon: '⚡', title: 'Rápido', desc: 'Abre una orden de trabajo en menos de 30 segundos.' },
    { icon: '📱', title: 'Conectado', desc: 'Notificaciones automáticas por WhatsApp a tus clientes.' },
    { icon: '📊', title: 'Analítico', desc: 'Reportes de ingresos, fallas frecuentes y productividad.' }
  ];

  constructor(private publicApi: PublicApiService, public auth: AuthService) {}

  ngOnInit(): void {
    this.publicApi.getTenant().subscribe({
      next: t => this.tenant = t,
      error: () => {}
    });
    this.publicApi.getCategories().subscribe({
      next: cats => this.categories = cats,
      error: () => {}
    });
  }

  iconForCategory(name: string): string {
    const n = (name || '').toLowerCase();
    if (n.includes('motor')) return '⚙️';
    if (n.includes('fren')) return '🛑';
    if (n.includes('suspen')) return '🔩';
    if (n.includes('eléct') || n.includes('elect')) return '⚡';
    return '🔧';
  }
}