import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Notification } from 'src/app/shared/models/Notification';

@Component({
  selector: 'app-notification-area',
  templateUrl: './notification-area.component.html',
  styleUrls: ['./notification-area.component.scss']
})
export class NotificationAreaComponent {

  readonly notification$: Observable<Notification | null> = this.notificationService.notification$;

  constructor(
    private notificationService: NotificationService,
  ) {}
}
