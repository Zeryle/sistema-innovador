import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/auth/auth.service';
import { UserDto } from '../../../core/models/user.model';

@Component({
  selector: 'app-header',
  standalone: true,
  template: `
    <header class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <h3 class="text-lg font-semibold text-gray-800">Bienvenido, {{ user?.email }}</h3>
      </div>
      <div class="flex items-center gap-4">
        <span class="badge-info">{{ user?.role }}</span>
      </div>
    </header>
  `
})
export class HeaderComponent implements OnInit {
  user: UserDto | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
  }
}
