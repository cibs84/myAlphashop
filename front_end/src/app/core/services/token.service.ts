import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, EMPTY, finalize, Observable, ReplaySubject, take, tap, throwError } from 'rxjs';
import { LoggingService } from './logging.service';
import { environment } from 'src/environments/environment';
import { UserStateService } from './user-state.service';
import { AuthappService } from './authapp.service';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private apiUrl: string = environment.apiUrl;
  private isRefreshing = false;
  private refreshTokenSubject = new ReplaySubject<boolean>(1);

  constructor(private logger: LoggingService,
              private httpClient: HttpClient,
              private userStateService: UserStateService,
              private authappService: AuthappService) { }

  retryRequest = (request: HttpRequest<any>): Observable<HttpEvent<unknown>> => {
      this.logger.log('AuthappService : retryRequest', request);
      const newRequest = request.clone({ withCredentials: true });
      console.log("request.withCredentials >>> ", request.withCredentials);

      return this.httpClient.request(newRequest).pipe(
        catchError(error => {
          this.logger.error("❌ Errore nel retry della richiesta:", error);
          return throwError(() => error);
        })
      );
    }

    refreshToken = (): Observable<any> => {
      this.logger.log('AuthappService : refreshToken');

      if (this.isRefreshing) {
        this.logger.log("🔄 Skip refresh: già in esecuzione");
        return this.refreshTokenSubject.asObservable().pipe(take(1));
      }

      this.isRefreshing = true;

      return this.httpClient.post(`${this.apiUrl}/authentication/refresh`, null, {observe: "response"}).pipe(
        take(1),
        tap(() => {
          this.logger.log("✅ Refresh token eseguito con successo");
          this.authappService.setAuthenticated(true);
          this.refreshTokenSubject.next(true);
        }),
        catchError((error) => {
          this.logger.error("❌ Errore nel refresh token:", error);
          this.refreshTokenSubject.error(error); // Indica l'errore a chi era in attesa

          this.logger.log("🔴 Refresh fallito. Effettuo pulizia stato.");
          this.authappService.setAuthenticated(false);
          this.userStateService.clearUserState();


          if (error.status === 401) {
            // L'AuthappService ritorna EMPTY solo se l'errore è 401
            return EMPTY;
          }

          // Per tutti gli altri errori, rilascia l'errore (sarà gestito da ErrorHandlingService che lo convertirà in EMPTY)
          return throwError(() => error);
        }),
        finalize(() => {
          this.isRefreshing = false;
        })
      );
    }
}
