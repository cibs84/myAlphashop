import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthappService } from './authapp.service';

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService implements CanActivate {

  constructor(private basicAuth: AuthappService, private route: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    if (!this.basicAuth.isLogged()) {
      console.log("Not logged in -> " + this.basicAuth.isLogged());
      this.route.navigate(['login']);
      return false;
    } else {
      console.log("Logged in -> " + this.basicAuth.isLogged());
      return true;
    }
  }
}
