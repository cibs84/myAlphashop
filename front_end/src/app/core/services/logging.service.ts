import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoggingService {

  constructor() { }

  log(message: any, ...optionalParams: any[]): void {
    if (!environment.production) {
      const caller = this.getCallerDetails();
      console.log(`[LOG] ${caller}:`, message, ...optionalParams);
    }
  }

  error(message: any, ...optionalParams: any[]): void {
    const caller = this.getCallerDetails();
    console.error(`[ERROR] ${caller}:`, message, ...optionalParams);
  }

  private getCallerDetails(): string {
    const err = new Error();
    if (err.stack) {
      const stackLines = err.stack.split('\n');
      if (stackLines.length >= 4) {
        // ATTENZIONE: dipende dal browser, in Chrome la 4ª riga è quella giusta
        return stackLines[3].trim();
      }
    }
    return 'caller unknown';
  }
}
