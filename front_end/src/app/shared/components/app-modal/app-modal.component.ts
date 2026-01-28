import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal',
  templateUrl: './app-modal.component.html'
})
export class AppModalComponent {
  @Input() modalId: string = 'genericModal';
  @Input() title: string = 'Confirm';
  @Input() message: string = 'Are you sure?';
  @Input() confirmText: string = 'Confirm';
  @Input() cancelText: string = 'Cancel';
  @Input() btnClass: string = 'btn-primary'; // btn-danger per delete, btn-warning per pending
  @Input() headerClass: string = 'bg-primary'; // bg-danger per delete

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onConfirm() { this.confirm.emit(); }
  onCancel() { this.cancel.emit(); }
}
