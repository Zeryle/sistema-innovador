import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

/**
 * Result of a mock payment attempt. Mirrors the structure of a Stripe
 * PaymentIntent: a success/cancel outcome plus a (mock) error code on decline.
 */
export interface MockPaymentResult {
  outcome: 'succeeded' | 'failed';
  errorCode?: 'card_declined' | 'expired_card' | 'processing_error' | 'incorrect_cvc';
  message?: string;
}

/**
 * Talks to the mock Stripe gateway:
 *   - {@link triggerPaymentSuccess} sends the webhook as if Stripe had fired
 *     checkout.session.completed.
 *   - {@link triggerPaymentCancel} sends the equivalent of session.expired.
 *
 * In production the SPA would not call these — Stripe's hosted page would POST
 * directly to our /api/webhook/stripe endpoint. We keep them here so the demo
 * can show the same flow with a button.
 *
 * The endpoint is permitAll (no Bearer token), so we use HttpClient directly
 * and bypass the auth interceptor.
 */
@Injectable({ providedIn: 'root' })
export class MockStripeClient {
  constructor(private http: HttpClient) {}

  triggerPaymentSuccess(sessionId: string, providerIntentId: string): Observable<{ success: boolean }> {
    return this.http.post<{ success: boolean }>(`${environment.apiUrl.replace('/api', '')}/api/webhook/stripe`, {
      type: 'succeeded',
      sessionId,
      providerIntentId
    });
  }

  triggerPaymentCancel(sessionId: string): Observable<{ success: boolean }> {
    return this.http.post<{ success: boolean }>(`${environment.apiUrl.replace('/api', '')}/api/webhook/stripe`, {
      type: 'expired',
      sessionId
    });
  }
}

/**
 * Validates a credit-card number client-side before "submitting" the mock payment.
 * Returns a {@link MockPaymentResult} with the simulated outcome.
 *
 * The decisions mirror Stripe's public test cards so the demo behaviour
 * matches what real cards would do:
 *   - 4242 4242 4242 4242  → succeeded
 *   - 4000 0000 0000 0002  → card_declined
 *   - 4000 0000 0000 0069  → expired_card
 *   - 4000 0000 0000 0127  → incorrect_cvc
 *   - anything else         → succeeded (for demo simplicity)
 */
export function simulateCardPayment(cardNumber: string, cvc: string): MockPaymentResult {
  const num = cardNumber.replace(/\s+/g, '');
  if (num === '4000000000000002') {
    return { outcome: 'failed', errorCode: 'card_declined', message: 'Tu tarjeta fue rechazada por el banco.' };
  }
  if (num === '4000000000000069') {
    return { outcome: 'failed', errorCode: 'expired_card', message: 'La tarjeta está vencida.' };
  }
  if (num === '4000000000000127' && cvc !== '123') {
    return { outcome: 'failed', errorCode: 'incorrect_cvc', message: 'El código de seguridad es incorrecto.' };
  }
  return { outcome: 'succeeded' };
}