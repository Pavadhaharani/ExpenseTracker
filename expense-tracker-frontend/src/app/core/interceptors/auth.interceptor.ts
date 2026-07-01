import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const notifications = inject(NotificationService);
  const router = inject(Router);
  const token = authService.getToken();

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/login')) {
        localStorage.removeItem('expense_tracker_token');
        localStorage.removeItem('expense_tracker_user');
        router.navigateByUrl('/login');
      }

      const message =
        error.error?.message ||
        error.error?.fieldErrors?.[Object.keys(error.error?.fieldErrors ?? {})[0]] ||
        'Request failed. Please try again.';
      notifications.error(message);
      return throwError(() => error);
    })
  );
};
