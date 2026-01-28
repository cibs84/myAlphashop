import { Injectable } from '@angular/core';
import { map, Observable, of, shareReplay, startWith, Subject, switchMap, timer } from 'rxjs';
import { Notification } from 'src/app/shared/models/Notification';
import { TranslationService } from './translation.service';
import { MsgKey } from '../i18n/msg-key.type';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private notificationSubject = new Subject<Notification|null>();

  readonly notification$: Observable<Notification|null> = this.notificationSubject.asObservable().pipe(
    switchMap((notification: Notification|null) => {
      if (!notification) return of(null);
      return timer(3000).pipe(
        map(() => null),
        startWith(notification)
      );
    }),
    shareReplay(1)
  )

  constructor(private translator: TranslationService) { }

  setNotificationSuccess(message: MsgKey){
    const msg = this.translator.translate(message);
    this.notificationSubject.next({message: msg, type: 'success'});
  }

  setNotificationError(message: MsgKey){
    const msg = this.translator.translate(message);
    this.notificationSubject.next({message: msg, type: 'error'});
  }

  setNotificationInfo(message: MsgKey){
    const msg = this.translator.translate(message);
    this.notificationSubject.next({message: msg, type: 'info'});
  }

  setNotificationWarning(message: MsgKey){
    const msg = this.translator.translate(message);
    this.notificationSubject.next({message: msg, type: 'warning'});
  }

  clearNotification(){
    this.notificationSubject.next(null);
  }
}
