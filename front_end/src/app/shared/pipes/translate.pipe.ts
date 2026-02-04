import { Pipe, PipeTransform } from '@angular/core';
import { MsgKey } from 'src/app/core/i18n/msg-key.type';
import { TranslationService } from 'src/app/core/services/translation.service';

@Pipe({
    name: 'translate',
    pure: false,
    standalone: false
})
export class TranslatePipe implements PipeTransform {

  constructor(private translator: TranslationService){}

  /**
   * * 1. Simple usage (no placeholders):
   * Template: {{ 'SAVE_BTN' | translate }}
   * Output:   "Salva" (or "Save")
   *
   * * 2. Usage with placeholders:
   * Translation file: "WELCOME": "Welcome back, {user}!"
   * Template:         {{ 'WELCOME' | translate:{ user: 'Mario' } }}
   * Output:           "Welcome back, Mario!"
   */
  transform(msgKey: MsgKey, params?: Record<string, string | number>): string {
    return this.translator.translate(msgKey, params);
  }

}
