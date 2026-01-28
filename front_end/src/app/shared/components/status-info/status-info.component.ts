import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-status-info',
  templateUrl: './status-info.component.html',
  styleUrls: ['./status-info.component.scss']
})
export class StatusInfoComponent {
  @Input() title: string = '';
  @Input() subtitle: string | null = null;
  @Input() icon: string = 'fa-circle-exclamation'; // Default icon
  @Input() iconClass: string = 'text-warning';      // Default color
  @Input() showHomeButton: boolean = false;
  @Input() showBackButton: boolean = false;

  @Output() back = new EventEmitter<void>();
}
