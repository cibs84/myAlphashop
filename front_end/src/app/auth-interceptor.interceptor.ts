import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthappService } from './services/authapp.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authSvc : AuthappService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    let authHeader = sessionStorage.getItem("authToken") ?? "";

    if (this.authSvc.isLogged()) {
      request = request.clone({
        setHeaders : {Authorization : authHeader}
      });
    }

    return next.handle(request);
  }
}
