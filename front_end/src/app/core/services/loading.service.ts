import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { LoggingService } from './logging.service';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  constructor(private logger: LoggingService) { }

  private loading = new BehaviorSubject<boolean>(false);
  loading$ = this.loading.asObservable();

  show = () => {
    this.logger.log('LoadingService: show()');
    this.loading.next(true);
  }
  hide = () => {
    this.logger.log('LoadingService: hide()');
    this.loading.next(false);
  }
  toggle = () => {
    this.loading.next(!this.loading.value);
  }
}
