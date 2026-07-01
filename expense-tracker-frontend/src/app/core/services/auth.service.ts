import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.models';
import { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '../models/auth.models';

const TOKEN_KEY = 'expense_tracker_token';
const USER_KEY = 'expense_tracker_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;
  private readonly userSubject = new BehaviorSubject<AuthResponse | null>(this.readStoredUser());

  readonly currentUser$ = this.userSubject.asObservable();

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/login`, payload).pipe(
      map((response) => response.data),
      tap((auth) => this.storeSession(auth))
    );
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/auth/register`, payload).pipe(
      map((response) => response.data),
      tap((auth) => this.storeSession(auth))
    );
  }

  me(): Observable<UserResponse> {
    return this.http
      .get<ApiResponse<UserResponse>>(`${this.apiUrl}/auth/me`)
      .pipe(map((response) => response.data));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSubject.next(null);
    this.router.navigateByUrl('/login');
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): AuthResponse | null {
    return this.userSubject.value;
  }

  private storeSession(auth: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, auth.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(auth));
    this.userSubject.next(auth);
  }

  private readStoredUser(): AuthResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as AuthResponse;
    } catch {
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }
}
