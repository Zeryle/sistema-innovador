import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [NgClass],
  template: `
    <span [ngClass]="badgeClass" class="badge">{{ status }}</span>
  `
})
export class StatusBadgeComponent {
  @Input() status = '';
  @Input() colorMap: Record<string, string> = {};

  get badgeClass(): string {
    const color = this.colorMap[this.status] || 'badge-info';
    return color;
  }
}
