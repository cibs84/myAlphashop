import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  constructor() { }

  authenticate(username: string, password: string): boolean {
    return username === "dario" && password === "pass" ? true : false;
  }

  usernameLogged = (): string|null => sessionStorage.getItem("username") || "";
  isLogged = (): boolean => sessionStorage.getItem("username") ? true : false;

  clearUsername = (): void => sessionStorage.removeItem("username");
  clearAll = (): void => sessionStorage.clear();
}
