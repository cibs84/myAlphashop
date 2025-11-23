import { Injectable } from '@angular/core';
import { catchError, EMPTY, map, of, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpRequest } from '@angular/common/http';
import { isNetworkOrServerError } from '../../shared/utils/http-status.utils';
import { LoggingService } from './logging.service';
import { PublicRoutesService } from './public-routes.service';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlingService {

  constructor(private tokenService: TokenService,
              private router: Router,
              private logger: LoggingService,
              private publicRoutesService: PublicRoutesService) {}

  handleError(error: any, request: HttpRequest<any>) {
    this.logger.log('ErrorHandlingService : handleError');
    this.logger.log('Errore ricevuto:', error);

    const isPublicRoute: boolean = this.publicRoutesService.isPublicRoute(request.url);

    if (error.status === 401 && !isPublicRoute) {
      this.logger.error('Unauthorized: Token non valido o scaduto. Sto tentando di rinnovarlo...');
      return this.tokenService.refreshToken().pipe(
        switchMap(() => {
          this.logger.log("Dopo il refresh del token, ritenta la richiesta originale");
          return this.tokenService.retryRequest(request);
        }),
        catchError((refreshOrRetryError) => {
          if (refreshOrRetryError.status === 401) {
            this.logger.error('❌ Autenticazione fallita: Token di Refresh non valido o nuovo Access Token non accettato (401). Reindirizzamento al login.');
            this.router.navigate(['/login']);
            return EMPTY;
          }
          // Errore diverso da 401 (es. 403, 500, network) nella richiesta ritentata.
          // Dobbiamo PROPAGARLO per farlo gestire dai blocchi 'else if' di seguito (403, 50x).
          return throwError(() => refreshOrRetryError);
        })
      );

    } else if (error.status === 403) {
      this.logger.error('Forbidden: insufficient roles');
      this.router.navigate(['/forbidden']);
      return throwError(() => error);

    } else if (isNetworkOrServerError(error.status)) {
      this.logger.error('Errore Server 50x oppure 0');
      this.router.navigate(['/50x']);
      return throwError(() => error);
    }

    // Propagazione errori non gestiti
    return throwError(() => error);
  }
}
