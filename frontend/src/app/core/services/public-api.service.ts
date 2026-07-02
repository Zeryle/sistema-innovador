import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

export interface PublicTenant {
  id: string;
  businessName: string;
  phone: string;
  ruc: string;
  logoUrl: string | null;
  plan: string;
}

export interface PublicCategory {
  id: number;
  name: string;
  description: string;
  imageUrl: string | null;
  subcategories: string[];
}

/**
 * Unauthenticated API calls used by the public landing page.
 * Bypasses the auth interceptor logic and goes directly through HttpClient.
 */
@Injectable({ providedIn: 'root' })
export class PublicApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getTenant(): Observable<PublicTenant> {
    return this.http
      .get<ApiResponse<PublicTenant>>(`${this.baseUrl}/public/tenant`)
      .pipe(map(res => res.data || ({} as PublicTenant)));
  }

  getCategories(): Observable<PublicCategory[]> {
    return this.http
      .get<ApiResponse<PublicCategory[]>>(`${this.baseUrl}/public/categories`)
      .pipe(map(res => res.data || []));
  }
}