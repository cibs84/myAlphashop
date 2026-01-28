import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { delay, finalize, Observable } from 'rxjs';
import { LoggingService } from 'src/app/core/services/logging.service';
import { LoadingStateService } from '../services/loading-state.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {

  constructor(private loader: LoadingStateService, private logger: LoggingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    const isGlobalLoading = this.loader.isGlobalLoading;

    if (!isGlobalLoading) {
      this.loader.setLocal(true);
    }

    return next.handle(request).pipe(
      delay(800),
      finalize(() => {
        this.logger.log('LoadingInterceptor : finalize');

        if (!isGlobalLoading) {
          this.loader.setLocal(false);
        }
      })
    );
  }
}
