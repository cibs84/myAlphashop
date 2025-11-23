import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { LoadingService } from 'src/app/core/services/loading.service';
import { LoggingService } from 'src/app/core/services/logging.service';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent implements OnInit {

  loading$!: Observable<boolean>;

  constructor(private loader: LoadingService, private logger: LoggingService) {
    this.logger.log('SpinnerComponent constructor');
  }

  ngOnInit(): void {
    this.loading$ = this.loader.loading$;
    this.loading$.subscribe(value => this.logger.log('SpinnerComponent loading$ -> ', value));
  }
}
