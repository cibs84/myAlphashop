import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingStateService {
  // Stato per il loader globale (es. per Delete, Save)
  private globalLoading = new BehaviorSubject<boolean>(false);
  globalLoading$ = this.globalLoading.asObservable();

  // Stato per il loader locale (es. per tabelle, ricerche)
  private localLoading = new BehaviorSubject<boolean>(false);
  localLoading$ = this.localLoading.asObservable();

  private activeLocalLoadings = 0;

  // Metodi espliciti
  setGlobal(isLoading: boolean) {
    this.globalLoading.next(isLoading);
  }
  setLocal(isLoading: boolean) {
    if (isLoading) {
      this.activeLocalLoadings++;
    } else if (this.activeLocalLoadings > 0) {
      this.activeLocalLoadings--;
    }

    this.localLoading.next(this.activeLocalLoadings > 0);
  }

  get isGlobalLoading(){
    return this.globalLoading.value;
  }

  // Metodo per spegnere tutto
  clearAll() {
    this.globalLoading.next(false);
    this.localLoading.next(false);
  }
}
