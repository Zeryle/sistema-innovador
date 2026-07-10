import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ModalSize = 'sm' | 'md' | 'lg' | 'xl';

/**
 * Generic modal component used across the SPA.
 *
 * Pass content via content projection (default slot). The component
 * renders a backdrop + centered card, traps focus on Escape/click-outside,
 * and supports three "tone" variants that match our alert palette:
 *
 *   - 'info'   (default) : neutral indigo header
 *   - 'success'         : green header
 *   - 'warning'         : amber header
 *   - 'danger'          : red header
 *
 * Closing the modal emits `(close)`. The parent is responsible for
 * toggling the `open` input back to false.
 *
 * Sizes:
 *   sm = 24rem,  md = 32rem,  lg = 48rem,  xl = 64rem
 */
@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="open"
         class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm animate-fade-in"
         (click)="onBackdrop($event)">
      <div class="bg-white rounded-2xl shadow-2xl overflow-hidden w-full animate-slide-up"
           [style.maxWidth.px]="sizePx()"
           (click)="$event.stopPropagation()">
        <!-- Header -->
        <div class="px-6 py-4 flex items-center justify-between text-white"
             [class.bg-gradient-to-r]="true"
             [class.from-indigo-600]="tone === 'info'"
             [class.to-indigo-700]="tone === 'info'"
             [class.from-emerald-600]="tone === 'success'"
             [class.to-emerald-700]="tone === 'success'"
             [class.from-amber-500]="tone === 'warning'"
             [class.to-amber-600]="tone === 'warning'"
             [class.from-rose-600]="tone === 'danger'"
             [class.to-rose-700]="tone === 'danger'">
          <h3 class="font-semibold text-lg flex items-center gap-2">
            <span *ngIf="icon">{{ icon }}</span>
            <span>{{ title }}</span>
          </h3>
          <button (click)="close.emit()"
                  class="text-white/80 hover:text-white transition-colors rounded-full w-8 h-8 flex items-center justify-center hover:bg-white/10"
                  aria-label="Cerrar">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>

        <!-- Body -->
        <div class="px-6 py-5 max-h-[70vh] overflow-y-auto">
          <ng-content></ng-content>
        </div>

        <!-- Footer -->
        <div *ngIf="!hideFooter"
             class="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end gap-2">
          <button *ngIf="cancelLabel"
                  type="button"
                  (click)="close.emit()"
                  class="px-4 py-2 rounded-lg text-sm font-medium text-gray-700 bg-white border border-gray-300 hover:bg-gray-50 transition">
            {{ cancelLabel }}
          </button>
          <button *ngIf="confirmLabel"
                  type="button"
                  (click)="confirm.emit()"
                  class="px-4 py-2 rounded-lg text-sm font-medium text-white transition shadow-sm"
                  [class.bg-indigo-600]="tone === 'info'"
                  [class.hover:bg-indigo-700]="tone === 'info'"
                  [class.bg-emerald-600]="tone === 'success'"
                  [class.hover:bg-emerald-700]="tone === 'success'"
                  [class.bg-amber-500]="tone === 'warning'"
                  [class.hover:bg-amber-600]="tone === 'warning'"
                  [class.bg-rose-600]="tone === 'danger'"
                  [class.hover:bg-rose-700]="tone === 'danger'">
            {{ confirmLabel }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    @keyframes slideUp { from { transform: translateY(20px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }
    .animate-fade-in { animation: fadeIn 0.15s ease-out; }
    .animate-slide-up { animation: slideUp 0.2s ease-out; }
  `]
})
export class ModalComponent {
  @Input() open = false;
  @Input() title = '';
  @Input() icon: string | null = null;
  @Input() tone: 'info' | 'success' | 'warning' | 'danger' = 'info';
  @Input() size: ModalSize = 'md';
  @Input() hideFooter = false;
  @Input() confirmLabel: string | null = null;
  @Input() cancelLabel: string | null = null;
  @Input() closeOnBackdrop = true;

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  sizePx(): number {
    switch (this.size) {
      case 'sm': return 384;
      case 'md': return 512;
      case 'lg': return 768;
      case 'xl': return 1024;
      default: return 512;
    }
  }

  onBackdrop(event: MouseEvent): void {
    if (this.closeOnBackdrop) {
      this.close.emit();
    }
  }
}