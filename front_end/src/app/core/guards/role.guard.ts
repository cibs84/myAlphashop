import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { LoggingService } from 'src/app/core/services/logging.service';
import { map, catchError, take, filter, switchMap } from 'rxjs/operators';
import { UserStateService } from '../services/user-state.service';
import { AuthappService } from '../services/authapp.service';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard  {
  constructor(
    private userStateService: UserStateService,
    private router: Router,
    private logger: LoggingService,
    private authService: AuthappService,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean> {
    const requiredRoles: string[] = route.data['roles'] || [];
    this.logger.log('👮 RoleGuard: required roles -> ', requiredRoles);

    // 1. Aspettiamo che l'app sia inizializzata (come in AuthGuard)
    return this.authService.isAppInitialized$.pipe(
      filter((initialized) => initialized === true),
      take(1),
      // 2. Una volta inizializzata, controlliamo i ruoli
      switchMap(() => {
        return this.userStateService.getRoles().pipe(
          filter(roles => roles !== null),
          take(1),
          map((userRoles) => {
            const hasRole = userRoles.some((role) =>
              requiredRoles.includes(role),
            );
            if (!hasRole) {
              this.logger.log(
                'Access denied: insufficient roles, redirect a /forbidden',
              );
              this.router.navigate(['/forbidden']);
              return false;
            }
            return true;
          }),
          catchError((err) => {
            this.logger.error('Error from getting roles', err);
            this.router.navigate(['/forbidden']);
            return of(false);
          }),
        );
      }),
    );
  }
}
