import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';

/**
 * HTTP interceptor for global error handling
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ein unbekannter Fehler ist aufgetreten';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Fehler: ${error.error.message}`;
      } else {
        // Server-side error
        switch (error.status) {
          case 400:
            errorMessage = 'Ungültige Anfrage. Bitte überprüfen Sie Ihre Eingaben.';
            break;
          case 401:
            errorMessage = 'Sitzung abgelaufen. Bitte melden Sie sich erneut an.';
            localStorage.removeItem('token');
            router.navigate(['/login']);
            break;
          case 403:
            errorMessage = 'Zugriff verweigert. Sie haben keine Berechtigung für diese Aktion.';
            break;
          case 404:
            errorMessage = 'Die angeforderte Ressource wurde nicht gefunden.';
            break;
          case 409:
            errorMessage = 'Konflikt: Die Ressource existiert bereits oder wurde zwischenzeitlich geändert.';
            break;
          case 500:
            errorMessage = 'Serverfehler. Bitte versuchen Sie es später erneut.';
            break;
          case 503:
            errorMessage = 'Der Service ist vorübergehend nicht verfügbar.';
            break;
          default:
            errorMessage = error.error?.message || `Fehler ${error.status}: ${error.statusText}`;
        }
      }

      // Show error notification
      notificationService.error(errorMessage);

      // Re-throw error for component-level handling if needed
      return throwError(() => error);
    })
  );
};
