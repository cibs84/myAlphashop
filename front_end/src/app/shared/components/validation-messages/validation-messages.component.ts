import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';
import { log } from 'console';

@Component({
  selector: 'app-validation-messages',
  templateUrl: './validation-messages.component.html'
})
export class ValidationMessagesComponent {
  @Input() errors: string[] | null = null;
  @Input() ngModel!: NgModel;
  @Input() defaultMsg: string = 'Required field**';
  @Input() customMsgs?: { [key: string]: string };

  hasNgValidation(): boolean {


    var res = Boolean(
      this.ngModel &&
      this.ngModel.invalid &&
      (this.ngModel.dirty || this.ngModel.touched)
    )
    console.log('hasNgValidation', this.ngModel, res, this.errors);

    return res;
  }
}
