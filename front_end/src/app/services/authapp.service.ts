import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs';
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  apiUrl: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  authenticate = (username: string, password: string) => {

    let authToken: string = "Basic " + window.btoa(username + ":" + password);

    let headers = new HttpHeaders(
      {Authorization: authToken}
    );

    return this.httpClient.get<HttpResponse<Object>>(`${this.apiUrl}/articles/test`,
      {headers: headers, observe: "response"}).pipe(
        map(
          data => {
            sessionStorage.setItem("username", username);
            sessionStorage.setItem("authToken", authToken);
            return data;
          }
        )
      )
  }

  deleteArticleByCodart = (codArt: string) => {
      return this.httpClient.delete<HttpResponse<Object>>(`${this.apiUrl}/articles/delete/${codArt}`,
        {observe: "response"}
      );
    }

  usernameLogged = (): string|null => sessionStorage.getItem("username") || "";
  isLogged = (): boolean => sessionStorage.getItem("username") ? true : false;

  clearUsername = (): void => sessionStorage.removeItem("username");
  clearAll = (): void => sessionStorage.clear();
}
