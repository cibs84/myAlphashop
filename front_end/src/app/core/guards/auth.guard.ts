import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthappService } from 'src/app/core/services/authapp.service';
import { map, catchError, take, switchMap } from 'rxjs/operators';
import { LoggingService } from 'src/app/core/services/logging.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthappService,
    private router: Router,
    private logger: LoggingService
  ) {}

  canActivate(): Observable<boolean> {
    this.logger.log('🔐 AuthGuard: controllo login (attendendo inizializzazione)');

    return this.authService.isAppInitialized$.pipe(
      switchMap(() => {
        return this.authService.isLogged().pipe(
          take(1),
          map(isLogged => {
            if (!isLogged) {
              this.logger.log('Utente non loggato: redirect a /login');
              this.router.navigate(['/login']);
              return false;
            }
            return true;
          }),
          catchError(err => {
            this.logger.error('Errore in AuthGuard:', err);
            this.router.navigate(['/login']);
            return of(false);
          })
        );
      })
    )
  }
}
