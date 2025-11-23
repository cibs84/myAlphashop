import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { tap, throwError, catchError, Observable, EMPTY, map, switchMap, of, BehaviorSubject, ReplaySubject, finalize, delay, filter, take, shareReplay, defaultIfEmpty } from "rxjs";
import { environment } from "src/environments/environment";
import { LoggingService } from "./logging.service";
import { PublicRoutesService } from "./public-routes.service";
import { UserStateService } from "./user-state.service";

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  private apiUrl: string = environment.apiUrl;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  public isAppInitializedSubject = new ReplaySubject<boolean>(1);
  public isAppInitialized$ = this.isAppInitializedSubject.asObservable().pipe(take(1));

  constructor(private httpClient: HttpClient,
              private logger: LoggingService,
              private publicRoutesService: PublicRoutesService,
              private userStateService: UserStateService) {}

  login = (username: string, password: string): Observable<boolean> => {
    this.logger.log('AuthappService : login');

    return this.httpClient.post(`${this.apiUrl}/authentication/login`,
      { username, password }, { observe: "response" }).pipe(
      switchMap(() => {
        return this.userStateService.getUserInfo().pipe(
          take(1),
          map(() => {
            this.setAuthenticated(true);
            return true;
          }),
          catchError((error) => {
            this.logger.error("❌ Errore nel recupero delle informazioni utente:", error);
            this.setAuthenticated(false);
            return of(false);
          }),
        );
      }),
      catchError((error) => {
        this.logger.error("❌ Errore nel login:", error);
        this.setAuthenticated(false);
        return throwError(() => error);
      })
    );
  }

  logout = () => {
    this.logger.log('AuthappService : logout');

    return this.httpClient.post(`${this.apiUrl}/authentication/logout`, null, {observe: "response"})
      .pipe(
        tap(() => {
          this.logger.log('Logout eseguito con successo');
          this.setAuthenticated(false);
          this.userStateService.clearUserState();
        }),
        catchError(error => {
          this.logger.error("❌ Errore durante il logout:", error);
          this.setAuthenticated(false);
          this.userStateService.clearUserState();
          return throwError(() => error);
        })
      );
  }

  isLogged = (): Observable<boolean> => {
    return this.isAuthenticated$;
  }

  setAuthenticated(status: boolean){
    this.isAuthenticatedSubject.next(status);
  }

  initializeAuthStatus(): Observable<any> {
    this.logger.log('App Initializer: Loading public routes, user info and initializing authenticated status...');
    return this.publicRoutesService.loadPublicRoutes().pipe(
      switchMap(() => {
        this.logger.log('✅ Rotte pubbliche caricate. Ora inizializzo user info...');
        return this.userStateService.getUserInfo().pipe(
          take(1),
          tap(userInfo => {
            if (userInfo && userInfo.username) {
              this.logger.log('👤 Utente autenticato durante init:', userInfo.username);
              this.setAuthenticated(true);
            } else {
              this.logger.log('🚫 Nessun utente autenticato durante init');
              this.setAuthenticated(false);
            }
          }),
          catchError(error => {
            this.logger.log('⚠️ getUserInfo() fallita durante init:', error.status);
            this.setAuthenticated(false);
            return of(null);
          }),
          finalize(() => {
            this.logger.log('✅ App initialization completed');
            this.isAppInitializedSubject.next(true);
          }),
        );
      })
    )
  }
}
