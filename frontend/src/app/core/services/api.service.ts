import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  get<T>(endpoint: string, params?: Record<string, any>): Observable<T> {
    let httpParams = new HttpParams();
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }
    return this.http.get<ApiResponse<T>>(`${environment.apiUrl}${endpoint}`, { params: httpParams })
      .pipe(map(res => res.data));
  }

  post<T>(endpoint: string, body: any): Observable<T> {
    return this.http.post<ApiResponse<T>>(`${environment.apiUrl}${endpoint}`, body)
      .pipe(map(res => res.data));
  }

  put<T>(endpoint: string, body: any): Observable<T> {
    return this.http.put<ApiResponse<T>>(`${environment.apiUrl}${endpoint}`, body)
      .pipe(map(res => res.data));
  }

  delete(endpoint: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiUrl}${endpoint}`)
      .pipe(map(() => undefined));
  }
}
