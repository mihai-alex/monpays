import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment.local';
import { UserAuthService } from './user-auth.service';
import { SidenavItem } from '../models/sidenav-item';
import { Observable } from 'rxjs';
import { map, filter } from 'rxjs/operators';
import { Operation } from '../models/operation';
import { EOperationType } from '../constants/enums/e-operation-type';

@Injectable({
  providedIn: 'root',
})
export class OperationService {
  private baseUrl = environment.apiURL + '/operations';

  constructor(
    private httpClient: HttpClient,
    private userAuthService: UserAuthService
  ) {}

  public getMenu(): Observable<SidenavItem[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient
      .get<String[]>(this.baseUrl, {
        headers: headers,
      })
      .pipe(
        map((groupNames) =>
          groupNames.map((groupName) => {
            // This map transforms the values
            if (groupName.toLowerCase() === 'profile') {
              let sidenavItem = new SidenavItem();
              sidenavItem.name = 'profile';
              sidenavItem.name_plural = 'Profiles';
              sidenavItem.route_link = 'profile-list';
              return sidenavItem;
            } else if (groupName.toLowerCase() === 'user') {
              let sidenavItem = new SidenavItem();
              sidenavItem.name = 'user';
              sidenavItem.name_plural = 'Users';
              sidenavItem.route_link = 'user-list';
              return sidenavItem;
            } else if (groupName.toLowerCase() === 'account') {
              let sidenavItem = new SidenavItem();
              sidenavItem.name = 'account';
              sidenavItem.name_plural = 'Accounts';
              sidenavItem.route_link = 'account-list';
              return sidenavItem;
            } else if (groupName.toLowerCase() === 'balance') {
              let sidenavItem = new SidenavItem();
              sidenavItem.name = 'balance';
              sidenavItem.name_plural = 'Balances';
              sidenavItem.route_link = 'balance-list';
              return sidenavItem;
            } else if (groupName.toLowerCase() === 'payment') {
              let sidenavItem = new SidenavItem();
              sidenavItem.name = 'payment';
              sidenavItem.name_plural = 'Payments';
              sidenavItem.route_link = 'payment-list';
              return sidenavItem;
            }

            return null;
          })
        ),
        map((items) => items.filter((item) => item !== null) as SidenavItem[]) // This map filters out null values
      );
  }

  public getOperations(profileName: string): Observable<EOperationType[]> {
    const headers = this.userAuthService.getHeaders();
    return this.httpClient.get<EOperationType[]>(
      this.baseUrl + '/' + profileName,
      {
        headers: headers,
      }
    );
  }
}
