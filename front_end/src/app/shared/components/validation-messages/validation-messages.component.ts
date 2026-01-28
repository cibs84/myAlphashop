import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';

@Component({
  selector: 'app-validation-messages',
  templateUrl: './validation-messages.component.html',
})
export class ValidationMessagesComponent {
  @Input() formCtrl?: AbstractControl | null;
  @Input() beErrorMsgs: string[] = [];
  @Input() feErrorMsgs?: Record<string, string>;

  readonly defaultErrorMsg: string = 'Validation error';

  readonly defaultErrorMsgs: Record<string, string> = {
    required: 'This field is required',
    pattern: 'Invalid format',
    minlength: 'Too short',
    maxlength: 'Too long',
    min: 'Value too small',
    max: 'Value too large',
  };

  readonly IS_VALID_MSG = 'Looks good!';

  showValidMsg() {
    return (
      !!this.formCtrl &&
      (this.formCtrl.dirty || this.formCtrl.touched) &&
      this.formCtrl.valid &&
      this.beErrorMsgs.length === 0
    );
  }

  isFrontendInvalid(): boolean {
    return (
      !!this.formCtrl &&
      (this.formCtrl.dirty || this.formCtrl.touched) &&
      this.formCtrl.invalid
    );
  }

  getFeErrorMsg() {
    const errorKey = Object.keys(this.formCtrl?.errors ?? {})[0];

    return (
      this.feErrorMsgs?.[errorKey] ||
      this.defaultErrorMsgs[errorKey] ||
      this.defaultErrorMsg
    );
  }
}
