import { Injectable } from '@angular/core';
import { Subject, Observable, take } from 'rxjs';

// Definiamo cosa può configurare l'utente
export interface ModalOptions {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'primary' | 'info';
}

@Injectable({
  providedIn: 'root'
})
export class ModalService {
  // Subject per comunicare al componente "Guscio" cosa mostrare
  private modalDataProperty = new Subject<ModalOptions & { id: string }>();
  modalData$ = this.modalDataProperty.asObservable();

  // Subject per restituire la risposta dell'utente (Sì/No)
  private modalResult = new Subject<boolean>();

  open(options: ModalOptions): Observable<boolean> {
    // Generiamo un ID univoco o usiamo uno fisso per il guscio
    this.modalDataProperty.next({ ...options, id: 'globalModal' });

    // Mostriamo la modale tramite Bootstrap API
    // Il ritardo serve a dare il tempo di renderizzare l'elemento html
    setTimeout(() => {
      const modalElem = document.getElementById('globalModal');
      if (modalElem) {
        const bsModal = new (window as any).bootstrap.Modal(modalElem);
        bsModal.show();
      }
    }, 10);

    return this.modalResult.asObservable().pipe(take(1));
  }

  confirm(value: boolean) {
    this.modalResult.next(value);
  }
}
