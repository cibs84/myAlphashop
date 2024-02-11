import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  constructor() { }

  autentica(username: string, password: string): boolean {
    return username === "dario" || password === "pass" ? true : false;
  }
}
