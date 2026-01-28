import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { LoggingService } from './logging.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PublicRoutesService {

  private routes: string[] = [];
  private apiUrl: string = environment.apiUrl;

  constructor(private httpClient: HttpClient, private logger: LoggingService) { }

  public loadPublicRoutes(): Observable<void> {
    return this.httpClient.get<string[]>(`${this.apiUrl}/authentication/public-routes`).pipe(
      tap(routes => {
        this.routes = routes;
        this.logger.log("[PublicRoutesService] ✅ Public routes loaded:", routes);
      }),
      map(() => void 0),
      catchError(error => {
        this.logger.error('[PublicRoutesService] ⚠️ Error loading public routes', error);
        this.routes = [];
        return of(void 0);
      })
    );
  }

  public isPublicRoute(url: string): boolean {
    return this.routes.some(route => url.includes(route));
  }
}
