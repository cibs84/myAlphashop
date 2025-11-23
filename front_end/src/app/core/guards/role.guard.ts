import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { LoggingService } from 'src/app/core/services/logging.service';
import { map, catchError, take } from 'rxjs/operators';
import { UserStateService } from '../services/user-state.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private userStateService: UserStateService,
    private router: Router,
    private logger: LoggingService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const requiredRoles: string[] = route.data['roles'] || [];
    this.logger.log('👮 RoleGuard: required roles -> ', requiredRoles);

    return this.userStateService.getRoles().pipe(
      take(1),
      map(userRoles => {
        const hasRole = userRoles.some(role => requiredRoles.includes(role));
        if (!hasRole) {
          this.logger.log('Access denied: insufficient roles, redirect a /forbidden');
          this.router.navigate(['/forbidden']);
          return false;
        }
        return true;
      }),
      catchError(err => {
        this.logger.error('Error from getting roles', err);
        this.router.navigate(['/forbidden']);
        return of(false);
      })
    );
  }
}
