import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ErrorHandlingService } from 'src/app/core/services/error-handling.service';
import { LoggingService } from 'src/app/core/services/logging.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private errorHandling: ErrorHandlingService, private logger: LoggingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.logger.log('AuthInterceptor : intercept');
    this.logger.log('AuthInterceptor : Request URL', request.url);

    request = request.clone({ withCredentials: true });

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        this.logger.error('AuthInterceptor : catchError', error);
        return this.errorHandling.handleError(error, request);
      })
    )
  }
}
