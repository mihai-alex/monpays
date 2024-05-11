import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.local';
import { UserAuthService } from './user-auth.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Account } from '../models/account';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private baseUrl = environment.apiURL + '/accounts';

  constructor(
    private userAuthService: UserAuthService,
    private httpClient: HttpClient
  ) {}

  getAccounts(): Observable<Account[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Account[]>(this.baseUrl, { headers });
  }

  createAccount(account: Account): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(this.baseUrl, account, { headers });
  }

  getAccountByAccountNumber(
    accountNumber: string,
    needsPending: boolean = false,
    needsHistory: boolean = false,
    needsAudit: boolean = false
  ): Observable<Account> {
    const headers = this.userAuthService.getHeaders();
    const params = new HttpParams()
      .set('pending', needsPending.toString())
      .set('history', needsHistory.toString())
      .set('audit', needsAudit.toString());

    return this.httpClient.get<Account>(`${this.baseUrl}/${accountNumber}`, {
      headers,
      params,
    });
  }

  modifyAccount(accountNumber: string, account: Account): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.put(`${this.baseUrl}/${accountNumber}`, account, {
      headers,
    });
  }

  removeAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${accountNumber}`, {
      headers,
    });
  }

  // TODO: fix the following patch methods: ---------------------------------------------------------------

  closeAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'close'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  blockAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'block'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  unblockAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'unblock'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  blockCreditAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'block_credit'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  blockDebitAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'block_debit'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  unblockCreditAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'unblock_credit'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  unblockDebitAccount(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'unblock_debit'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${accountNumber}`,
      operation,
      {
        headers,
      }
    );
  }

  // ------------------------------------------------------------------------------------------

  approveChanges(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(
      `${this.baseUrl}/${accountNumber}/checks`,
      null,
      {
        headers,
      }
    );
  }

  rejectChanges(accountNumber: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${accountNumber}/checks`, {
      headers,
    });
  }

  // TODO: implement REPAIR
}
