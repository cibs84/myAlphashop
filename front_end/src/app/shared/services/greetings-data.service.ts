import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GreetingsDataService {

  constructor(private http: HttpClient) { }

  getGreetings = (userId: string): Observable<Object> => {
    let url: string = "http://localhost:8080/api/greetings";
    if (userId !== "") {
      url += "/" + userId;
    }
    return this.http.get(url);
  };
}
