import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Profile } from '../models/profile';
import { UserAuthService } from './user-auth.service';
import { ProfileHistoryEntry } from '../models/profile-history-entry';
import { AuditEntry } from '../models/audit-entry';
import { environment } from 'src/environments/environment.local';
import { Operation } from '../models/operation';
import { Balance } from '../models/balance';

@Injectable({
  providedIn: 'root',
})
export class BalanceService {
  private baseUrl = environment.apiURL + '/balances';

  constructor(
    private userAuthService: UserAuthService,
    private httpClient: HttpClient
  ) {}

  getBalances(): Observable<Balance[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Balance[]>(this.baseUrl, { headers });
  }

  getBalancesByAccountNumber(accountNumber: string): Observable<Balance> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Balance>(`${this.baseUrl}/${accountNumber}`, {
      headers,
    });
  }
}
