import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { LoadingStateService } from './core/services/loading-state.service';
import { ModalService } from './core/services/modal.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  globalLoading$: Observable<boolean> = this.loader.globalLoading$;
  modalData$ = this.modalService.modalData$;

  constructor(private loader: LoadingStateService,
              private modalService: ModalService
  ){}

  onClose(result: boolean) {
    this.modalService.confirm(result);
  }
}
