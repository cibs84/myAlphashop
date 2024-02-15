import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SalutiDataService {

  constructor(private http: HttpClient) { }

  getSaluti = (userId: string): Observable<Object> => {
    let url: string = "http://localhost:8080/api/saluti";
    if (userId !== "") {
      url += "/" + userId;
    }
    return this.http.get(url);
  };
}
