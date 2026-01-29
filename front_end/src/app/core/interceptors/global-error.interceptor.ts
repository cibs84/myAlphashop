import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { ErrorHandlingService } from '../services/error-handling.service';
import { LoggingService } from '../services/logging.service';

@Injectable()
export class GlobalErrorInterceptor implements HttpInterceptor {

  constructor(private errorHandling: ErrorHandlingService, private logger: LoggingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.logger.log('GlobalErrorInterceptor -> Request URL:', request.url);

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        this.logger.error('GlobalErrorInterceptor -> catchError', error);
        return this.errorHandling.handleError(error, request);
      })
    )
  }
}
