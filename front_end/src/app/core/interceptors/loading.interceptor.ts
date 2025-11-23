import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { finalize, Observable } from 'rxjs';
import { LoadingService } from 'src/app/core/services/loading.service';
import { LoggingService } from 'src/app/core/services/logging.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {

  constructor(private loader: LoadingService, private logger: LoggingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    this.loader.show();

    return next.handle(request).pipe(
      finalize(() => {
        this.logger.log('LoadingInterceptor : finalize');
        this.loader.hide();
      })
    );
  }
}
