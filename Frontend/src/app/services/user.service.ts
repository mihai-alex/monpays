import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserAuthService } from './user-auth.service';
import { environment } from 'src/environments/environment.local';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { UserHistoryEntry } from '../models/user-history-entry';
import { AuditEntry } from '../models/audit-entry';
import { UserVerificationRequest } from '../models/user-verification-request';
import { UserAuthenticationResponse } from '../models/user-authentication-response';

// TODO: refactor this service to separate the authentication logic from the user data logic

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = environment.apiURL + '/users';

  requestHeader = new HttpHeaders({
    'No-Auth': 'True',
  });

  constructor(
    private httpClient: HttpClient,
    private userAuthService: UserAuthService
  ) {}

  login(loginData: any): Observable<any> {
    return this.httpClient.post(
      environment.apiURL + '/auth/sign_in', // Replace with the actual URL
      loginData,
      {
        headers: this.requestHeader,
        responseType: 'json', // Change responseType to 'json'
      }
    );
  }

  public changePassword(data: any, username: string): Observable<string> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.put(
      `http://localhost:8080/api/auth/${username}/password`,
      data,
      {
        headers: headers,
        responseType: 'text',
      }
    );
  }

  verifyTfaCode(verificationRequest: UserVerificationRequest) {
    return this.httpClient.post<UserAuthenticationResponse>(
      environment.apiURL + '/auth/verify',
      verificationRequest
    );
  }

  public roleMatch(allowedRoles: string[]): boolean {
    // TODO: get the current user's roles from backend and compare them with the allowed roles
    return true;
  }

  // TODO: pass the auth token in the header
  // Users data logic below:
  getUsers(): Observable<User[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<User[]>(this.baseUrl, { headers });
  }

  createUser(user: User): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(`${this.baseUrl}`, user, { headers });
  }

  getUserByUserName(
    userName: string,
    needsPending: boolean = false,
    needsHistory: boolean = false,
    needsAudit: boolean = false
  ): Observable<User> {
    const headers = this.userAuthService.getHeaders();
    const queryParams = new HttpParams()
      .set('pending', needsPending.toString())
      .set('history', needsHistory.toString())
      .set('audit', needsAudit.toString());
    return this.httpClient.get<User>(`${this.baseUrl}/${userName}`, {
      headers,
      params: queryParams,
    });
  }

  modifyUser(userName: string, user: User): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.put(`${this.baseUrl}/${userName}`, user, {
      headers,
    });
  }

  removeUser(userName: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${userName}`, { headers });
  }

  // History and audit logic below:
  // TODO: get the user history and audit from backend

  getUserHistory(userName: string): Observable<UserHistoryEntry[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<UserHistoryEntry[]>(
      `${this.baseUrl}/${userName}/history`,
      { headers }
    );
  }

  getUserAudit(userName: string): Observable<AuditEntry[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<AuditEntry[]>(
      `${this.baseUrl}/${userName}/audit`,
      { headers }
    );
  }

  blockUser(userName: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'block'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${userName}/block`,
      operation,
      {
        headers,
      }
    );
  }

  unblockUser(userName: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    const operation = 'unblock'; // Hardcoded operation value
    return this.httpClient.patch(
      `${this.baseUrl}/${userName}/unblock`,
      operation,
      {
        headers,
      }
    );
  }

  approveChanges(userName: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(`${this.baseUrl}/${userName}/checks`, null, {
      headers,
    });
  }

  rejectChanges(userName: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${userName}/checks`, {
      headers,
    });
  }

  // TODO: implement REPAIR
}
