import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Profile } from '../models/profile';
import { UserAuthService } from './user-auth.service';
import { ProfileHistoryEntry } from '../models/profile-history-entry';
import { AuditEntry } from '../models/audit-entry';
import { environment } from 'src/environments/environment.local';
import { Operation } from '../models/operation';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private baseUrl = environment.apiURL + '/profiles';
  private operationsUrl = environment.apiURL + '/operations';

  constructor(
    private userAuthService: UserAuthService,
    private httpClient: HttpClient
  ) {}

  getProfiles(): Observable<Profile[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<Profile[]>(this.baseUrl, { headers });
  }

  createProfile(profile: Profile): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(this.baseUrl, profile, { headers });
  }

  getProfileByName(
    name: string,
    needsPending: boolean = false,
    needsHistory: boolean = false,
    needsAudit: boolean = false
  ): Observable<Profile> {
    const headers = this.userAuthService.getHeaders();
    const queryParams = new HttpParams()
      .set('pending', needsPending.toString())
      .set('history', needsHistory.toString())
      .set('audit', needsAudit.toString());

    return this.httpClient.get<Profile>(`${this.baseUrl}/${name}`, {
      headers,
      params: queryParams,
    });
  }

  getProfileNames(): Observable<string[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<string[]>(`${this.baseUrl}/profile-names`, {
      headers,
    });
  }

  modifyProfile(name: string, profile: Profile): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.put(`${this.baseUrl}/${name}`, profile, { headers });
  }

  removeProfile(name: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${name}`, { headers });
  }

  repairProfile(profile: Profile): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(this.baseUrl, profile, { headers });
  }

  approveChanges(name: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.post(`${this.baseUrl}/${name}/checks`, null, {
      headers,
    });
  }

  rejectChanges(name: string): Observable<Object> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.delete(`${this.baseUrl}/${name}/checks`, {
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
