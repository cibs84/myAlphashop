import { Injectable } from '@angular/core';
import { EN } from '../i18n/en';
import { IT } from '../i18n/it';
import { SupportedLanguage } from '../i18n/supported-language.type';
import { getByPath } from '../../shared/utils/get-by-path.util';
import { MsgKey } from '../i18n/msg-key.type';

@Injectable({
  providedIn: 'root'
})

export class TranslationService {

  private language: SupportedLanguage = 'it';

  private messageMaps = {
    en: EN,
    it: IT
  }

  constructor() { }

  translate(msgKey: MsgKey, params?: Record<string, string | number>): string {
    let msg = getByPath(this.messageMaps[this.language], msgKey) ?? msgKey;

    if (params) {
      Object.keys(params).forEach(key => {
        msg = msg.split(`{${key}}`).join(String(params[key]));
      })
    }

    return msg;
  }

  setLanguage(lang: SupportedLanguage){
    this.language = lang;
  }

}
