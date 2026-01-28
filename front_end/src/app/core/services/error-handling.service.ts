import { Injectable } from '@angular/core';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { isNetworkOrServerError } from '../../shared/utils/http-status.utils';
import { LoggingService } from './logging.service';
import { PublicRoutesService } from './public-routes.service';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class ErrorHandlingService {
  constructor(
    private tokenService: TokenService,
    private router: Router,
    private logger: LoggingService,
    private httpClient: HttpClient,
    private publicRoutesService: PublicRoutesService
  ) {}

  handleError(error: any, request: HttpRequest<any>) {
    this.logger.log('[ErrorHandlingService] handleError() -> Errore ricevuto:', error);

    const isPublicRoute: boolean = this.publicRoutesService.isPublicRoute(
      request.url
    );

    if (error.status === 401 && !isPublicRoute) {
      this.logger.error(
        '❌ [ErrorHandlingService] Unauthorized (401): Token non valido o scaduto. Sto tentando di rinnovarlo...'
      );
      return this.tokenService.refreshToken().pipe(
        switchMap(() => {
          this.logger.log(
            '[ErrorHandlingService] Dopo il refresh del token, ritenta la richiesta originale'
          );
          return this.retryRequest(request);
        }),
        catchError((refreshOrRetryError) => {
          if (refreshOrRetryError.status === 401) {
            this.logger.error(
              '❌ [ErrorHandlingService] Autenticazione fallita: Token di Refresh non valido o nuovo Access Token non accettato (401). Reindirizzamento al login.'
            );
            this.router.navigate(['/login']);
          }
          return throwError(() => refreshOrRetryError);
        })
      );
    } else if (error.status === 403) {
      this.logger.error('[ErrorHandlingService] Forbidden: insufficient roles');
      this.router.navigate(['/forbidden']);
    } else if (isNetworkOrServerError(error.status) && request.method == 'GET') {
      this.logger.error('[ErrorHandlingService] Errore Server 5xx or 0, request GET -> redirection /5xx');

      this.router.navigate(['/5xx']);
    }

    return throwError(() => error);
  }

  private retryRequest = (
    request: HttpRequest<any>
  ): Observable<HttpEvent<unknown>> => {
    this.logger.log('[ErrorHandlingService] retryRequest()', request);

    return this.httpClient.request(request).pipe(
      catchError((error) => {
        this.logger.error('❌ [ErrorHandlingService] Errore nel retry della richiesta:', error);
        return throwError(() => error);
      })
    );
  };
}
