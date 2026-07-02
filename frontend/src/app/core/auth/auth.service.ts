import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: any;
  tenant: any;
}

export interface LoginCommand {
  email: string;
  password: string;
}

export interface RegisterCommand {
  email: string;
  password: string;
  businessName: string;
  phone: string;
  role?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const stored = localStorage.getItem('currentUser');
    if (stored && stored !== 'undefined' && stored !== 'null') {
      try {
        this.currentUserSubject.next(JSON.parse(stored));
      } catch (e) {
        localStorage.removeItem('currentUser');
      }
    }
  }

  login(command: LoginCommand): Observable<LoginResponse> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.baseUrl}/login`, command).pipe(
      map(res => res.data),
      tap(res => this.handleAuth(res))
    );
  }

  register(command: RegisterCommand): Observable<LoginResponse> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.baseUrl}/register`, command).pipe(
      map(res => res.data),
      tap(res => this.handleAuth(res))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  private handleAuth(res: LoginResponse): void {
    localStorage.setItem('token', res.token);
    localStorage.setItem('refreshToken', res.refreshToken);
    localStorage.setItem('currentUser', JSON.stringify(res.user));
    this.currentUserSubject.next(res.user);
  }
}
