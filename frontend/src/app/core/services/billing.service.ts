import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export type PlanCode = 'FREE' | 'BASIC' | 'PREMIUM';

export interface SubscriptionStatus {
  tenantId: string;
  currentPlan: PlanCode;
  lastPaidPlan: PlanCode;
  currentPlanName: string;
  currentMonthlyPrice: number;
  currency: string;
  trialEndsAt: string | null;
  nextBillingAt: string | null;
  currentCustomers: number;
  currentWorkOrdersThisMonth: number;
  currentAdminUsers: number;
  maxCustomers: number;
  maxAdminUsers: number;
  maxWorkOrdersPerMonth: number;
  whatsappEnabled: boolean;
  analyticsEnabled: boolean;
  prioritySupport: boolean;
  overCustomerLimit: boolean;
  overWorkOrderLimit: boolean;
  overAdminLimit: boolean;
  availableUpgrades: SubscriptionStatusPlan[];
}

export interface SubscriptionStatusPlan {
  code: PlanCode;
  name: string;
  tagline: string;
  monthlyPrice: number;
  currency: string;
  maxCustomers: number;          // -1 means unlimited
  maxAdminUsers: number;
  maxWorkOrdersPerMonth: number; // -1 means unlimited
  maxCustomersDisplay: string;
  maxWorkOrdersDisplay: string;
  whatsappEnabled: boolean;
  analyticsEnabled: boolean;
  prioritySupport: boolean;
  features: string[];
}

/**
 * PaymentProvider object as returned by the gateway.
 * Note: in the real Stripe gateway, `provider` would be 'stripe'; in our
 * mock it's 'mock'. The SPA does not care — both flavours expose the same
 * fields.
 */
export interface GatewayCheckoutProvider {
  provider: string;
  intentId: string;
}

export interface GatewayCheckout {
  providerSessionId: string;
  provider: GatewayCheckoutProvider;
  url: string;
  expiresAtEpoch: number;
}

export interface CheckoutDetails {
  id: string;
  status: 'OPEN' | 'PROCESSING' | 'SUCCEEDED' | 'CANCELLED' | 'EXPIRED';
  targetPlan: PlanCode;
  targetPlanName: string;
  expectedAmount: number;
  currency: string;
  tenantId: string;
  providerIntentId: string;
  isFinal: boolean;
}

/**
 * Authenticated calls for the current tenant's subscription state.
 * The HTTP interceptor adds the Bearer token automatically, so we don't
 * need to know about tokens here.
 */
@Injectable({ providedIn: 'root' })
export class BillingService {
  private readonly baseUrl = `${environment.apiUrl}/billing`;

  constructor(private http: HttpClient) {}

  getSubscription(): Observable<SubscriptionStatus> {
    return this.http
      .get<{ success: boolean; message: string; data: SubscriptionStatus }>(`${this.baseUrl}/subscription`)
      .pipe(map(res => res.data));
  }

  startCheckout(targetPlan: PlanCode, successUrl?: string, cancelUrl?: string): Observable<GatewayCheckout> {
    return this.http
      .post<{ success: boolean; message: string; data: GatewayCheckout }>(
        `${this.baseUrl}/checkout`,
        { targetPlan, successUrl, cancelUrl }
      )
      .pipe(map(res => res.data));
  }

  getCheckout(sessionId: string): Observable<CheckoutDetails> {
    return this.http
      .get<{ success: boolean; message: string; data: CheckoutDetails }>(
        `${this.baseUrl}/checkout/${sessionId}`
      )
      .pipe(map(res => res.data));
  }
}