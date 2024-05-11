import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserAuthService } from './user-auth.service';
import { environment } from 'src/environments/environment.local';
import { Operation } from '../models/operation';
import { Payment } from '../models/payment';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private baseUrl = environment.apiURL + '/payments';
  private operationsUrl = environment.apiURL + '/operations';

  constructor(
    private userAuthService: UserAuthService,
    private httpClient: HttpClient
  ) {}

  getPayments(): Observable<Payment[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Payment[]>(this.baseUrl, { headers });
  }

  getPaymentByPaymentNumber(
    paymentNumber: string,
    needsHistory: boolean,
    needsAudit: boolean
  ): Observable<Payment> {
    const headers = this.userAuthService.getHeaders();
    const queryParams = new HttpParams()
      .set('needsHistory', needsHistory.toString())
      .set('needsAudit', needsAudit.toString());

    return this.httpClient.get<Payment>(`${this.baseUrl}/${paymentNumber}`, {
      headers,
      params: queryParams,
    });
  }

  createPayment(payment: Payment): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(this.baseUrl, payment, { headers });
  }

  repairPayment(paymentNumber: string, payment: Payment): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.put(
      `${this.baseUrl}/${paymentNumber}/checks`,
      payment,
      {
        headers,
      }
    );
  }

  approvePayment(paymentNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(
      `${this.baseUrl}/${paymentNumber}/checks/approve`,
      null,
      { headers }
    );
  }

  verifyPayment(paymentNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(
      `${this.baseUrl}/${paymentNumber}/checks/verify`,
      null,
      { headers }
    );
  }

  authorizePayment(paymentNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(
      `${this.baseUrl}/${paymentNumber}/checks/authorize`,
      null,
      { headers }
    );
  }

  reject(paymentNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${paymentNumber}/checks`, {
      headers,
    });
  }

  cancel(paymentNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${paymentNumber}`, {
      headers,
    });
  }

  getAvailableOperationsForProfileType(
    profileType: string
  ): Observable<Operation[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Operation[]>(
      `${this.operationsUrl}/profiles/${profileType}`,
      { headers }
    );
  }

  // TODO: implement REPAIR
}
