import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoggingService } from 'src/app/core/services/logging.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private logger: LoggingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.logger.log('AuthInterceptor -> Request URL:', request.url);

    request = request.clone({ withCredentials: true });

    return next.handle(request);
  }
}
