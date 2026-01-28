import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  catchError,
  EMPTY,
  finalize,
  Observable,
  of,
  take,
  tap,
  throwError,
} from 'rxjs';
import { LoggingService } from './logging.service';
import { environment } from 'src/environments/environment';
import { UserStateService } from './user-state.service';
import { AuthappService } from './authapp.service';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private apiUrl: string = environment.apiUrl;
  private isRefreshing = false;
  private pendingRequests: Array<(ok: boolean) => void> = [];

  constructor(
    private logger: LoggingService,
    private httpClient: HttpClient,
    private userStateService: UserStateService,
    private authappService: AuthappService
  ) {}

  refreshToken = (): Observable<any> => {
    this.logger.log('[TokenService] refreshToken()');

    if (this.isRefreshing) {
      this.logger.log(
        '🔄 Skip refresh: già in esecuzione — metto la chiamata in pending'
      );
      return new Observable<boolean>((subscriber) => {
        // aggiungi alla coda una funzione che verrà chiamata da releasePendingRequests
        this.pendingRequests.push((ok: boolean) => {
          if (ok) {
            // next() serve semplicemente a sbloccare la pipe;
            // il valore è opzionale, ma se lo metti rende il flusso
            // più leggibile e sicuro per futuri sviluppi.
            subscriber.next(true);
            subscriber.complete();
          } else {
            // se 'ok' === false notifichiamo l'errore (simulate 401)
            subscriber.error({ status: 401, message: 'refresh failed' });
          }
        });
      });
    }

    this.isRefreshing = true;

    return this.httpClient
      .post(`${this.apiUrl}/authentication/refresh`, null, {
        observe: 'response',
        withCredentials: true,
      })
      .pipe(
        take(1),
        tap(() => {
          this.logger.log('✅ [TokenService] Refresh token eseguito con successo');
          this.authappService.setAuthenticated(true);

          // risveglia eventuali richieste in pending (true = successo)
          this.pendingRequests.forEach((cb) => {
            try {
              cb(true);
            } catch (e) {
              /* ignore */
            }
          });
          this.pendingRequests = [];
        }),
        catchError((error) => {
          this.logger.error('❌ [TokenService] Errore nel refresh token:', error);

          // risveglia la coda con errore (ok = false)
          this.pendingRequests.forEach((cb) => {
            try {
              cb(false);
            } catch (e) {
              /* ignore */
            }
          });
          this.pendingRequests = [];

          this.logger.log('🔴 [TokenService] Refresh fallito. Effettuo pulizia stato.');
          this.authappService.setAuthenticated(false);
          this.userStateService.clearUserState();

          // Per gli errori che NON sono 401, rilascia l'errore (sarà gestito da ErrorHandlingService che lo convertirà in EMPTY)
          return throwError(() => error);
        }),
        finalize(() => {
          this.isRefreshing = false;
        })
      );
  };
}
