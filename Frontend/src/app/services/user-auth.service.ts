import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserAuthService {
  private isAuthenticatedSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);

  constructor() {
    // When the service initializes, check if the user is already authenticated based on the token.
    this.isAuthenticatedSubject.next(this.isAuthenticated());
  }

  public setToken(jwtToken: string) {
    localStorage.setItem('jwtToken', jwtToken);

    // Decode the token and get the username from it
    const decodedToken: any = jwt_decode(jwtToken);
    const username = decodedToken?.sub || '';

    // Set the username in the localStorage
    localStorage.setItem('username', username);

    localStorage.setItem('isAuthenticated', 'true');

    // After setting the token, update the authentication status
    this.isAuthenticatedSubject.next(true);
  }

  public getToken(): string {
    return localStorage.getItem('jwtToken') || '';
  }

  public clear() {
    localStorage.clear();

    // After clearing the token, update the authentication status
    this.isAuthenticatedSubject.next(false);
  }

  public getHeaders(): HttpHeaders {
    const jwtToken = this.getToken();
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      token: jwtToken,
      //Authorization: 'Bearer ' + jwtToken, // Sending JWT as a Bearer token
      'X-Frame-Options': 'DENY', // Prevent clickjacking attacks - malicious website or attacker tricks a user into clicking on something different from what the user perceives
      'X-XSS-Protection': '1; mode=block', // Enable XSS protection in modern browsers (Cross-Site Scripting) - when an attacker injects malicious scripts into web pages that are then viewed by other users
      'Strict-Transport-Security': 'max-age=31536000; includeSubDomains', // Enable HSTS - helps protect websites against various types of attacks by enforcing the use of secure connections over HTTPS
    });
    return headers;
  }

  public isAuthenticated(): boolean {
    const isAuthenticated = localStorage.getItem('isAuthenticated');

    const jwtToken = this.getToken();
    if (jwtToken) {
      const decodedToken: any = jwt_decode(jwtToken);
      const expirationDate = new Date(0); // Initialize with epoch date (January 1, 1970)
      expirationDate.setUTCSeconds(decodedToken.exp); // Set the expiration date from the token

      // Check if the token is expired
      if (expirationDate < new Date()) {
        // Token is expired, clear the authentication and log out the user
        this.clear();
        return false;
      }
    }

    return isAuthenticated === 'true';
  }

  public setAuthenticatedStatus(isAuthenticated: boolean) {
    this.isAuthenticatedSubject.next(isAuthenticated);
  }

  public getAuthenticatedStatus(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  public getUsername(): string {
    return localStorage.getItem('username') || '';
  }

  // TODO: (maybe) use cookies/session storage instead of local storage
  public setRoles(roles: string[]) {
    localStorage.setItem('roles', JSON.stringify(roles));
  }

  public getRoles(): string[] {
    const rolesString = localStorage.getItem('roles');
    if (rolesString === null) {
      // Item not found in localStorage, return an empty array or throw an error if appropriate
      return []; // Or you can throw an error to indicate that the item is not found
    }

    try {
      const roles = JSON.parse(rolesString) as string[];
      return roles;
    } catch (error) {
      console.error('Error parsing roles from localStorage:', error);
      return []; // Return an empty array or handle the error as needed
    }
  }
}
