import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, filter, map, Observable, ReplaySubject, shareReplay, take, tap, throwError } from 'rxjs';
import { UserInfoResponse } from 'src/app/shared/models/UserInfoResponse';
import { LoggingService } from './logging.service';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserStateService {

  private apiUrl: string = environment.apiUrl;

  private userInfoRequestSubject = new ReplaySubject<UserInfoResponse | null>(1);
  private userInfo$ = this.userInfoRequestSubject.asObservable();

  private userInfoRequest$: Observable<UserInfoResponse | null> | null = null;

  constructor(private logger: LoggingService, private httpClient: HttpClient) { }

  getUsernameLogged = (): Observable<string | null> => this.userInfo$.pipe(
    map(userInfo => userInfo ? userInfo.username : null)
  );

  getRoles(): Observable<string[]> {
    return this.userInfo$.pipe(
        // Il Predicate `userInfo is UserInfoResponse` forza TypeScript a riconoscere
        // 'userInfo' come NON-null dopo il filtro per evitare l'errore TS2531 nel 'map'.
        filter((userInfo): userInfo is UserInfoResponse => userInfo != null),
        take(1),
        map(userInfo => {
            this.logger.log("getRoles() userInfo -> ", userInfo);
            return userInfo.roles ?? [];
        }),
        catchError((error) => {
          this.logger.error("Error in getRoles():", error);
          return throwError(() => error);
        })
    );
  }

  getUserInfo(): Observable<UserInfoResponse | null> {
      // 1. Logica di Caching: se c'è una richiesta in corso o completata (cached)
      if (this.userInfoRequest$) {
          // Ritorna il risultato HTTP presente in cache.
          return this.userInfoRequest$.pipe(take(1));
      }
      // 2. Avvia la nuova richiesta solo se la cache è vuota (this.userInfoRequest$ == null)
      // e quindi non c'è una richiesta in corso o non è presente un risultato HTTP in cache
      this.logger.log('getUserInfo: Creazione e avvio nuova richiesta ME.');
      this.userInfoRequest$ = this.httpClient.get<UserInfoResponse>(`${this.apiUrl}/authentication/me`).pipe(
          tap(userInfo => {
            this.logger.log("*** USER INFO RICEVUTO e Subject Aggiornato ***", userInfo);
              this.userInfoRequestSubject.next(userInfo);
          }),
          catchError(err => {
              this.logger.error('getUserInfo: error received', err);
              this.clearUserState();
              return throwError(() => err);
          }),
          // 4. shareReplay(1) rende questa Observable CACHATA
          shareReplay({ bufferSize: 1, refCount: true })
      );
      return this.userInfoRequest$.pipe(take(1));
  }

  public clearUserState(){
    this.userInfoRequestSubject.next(null);
    this.userInfoRequest$ = null;
  }
}
