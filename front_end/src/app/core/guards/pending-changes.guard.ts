import { Injectable } from '@angular/core';
import { CanDeactivate } from '@angular/router';
import { Observable } from 'rxjs';

export interface ComponentCanDeactivate {
  canDeactivate: () => boolean | Observable<boolean>;
  openConfirmModal?: () => Observable<boolean>;
}

@Injectable({
  providedIn: 'root'
})
export class PendingChangesGuard implements CanDeactivate<ComponentCanDeactivate> {
  canDeactivate(component: ComponentCanDeactivate): boolean | Observable<boolean> {
    // If the form is not dirty, exit without modal.
    if (component.canDeactivate()) {
      return true;
    }
    // Otherwise, open the modal and return the Observable (true = exit, false = stay)
    return component.openConfirmModal ? component.openConfirmModal() : true;
  }
}
