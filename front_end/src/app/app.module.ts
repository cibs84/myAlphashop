import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleManagerComponent } from './pages/article-manager/article-manager.component';
import { FormsModule } from '@angular/forms';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { Page404Component } from './pages/page-404/page-404.component';
import { Page50xComponent } from './pages/page-50x/page-50x.component';
import { CoreModule } from './core/core.module';
import { LogoutComponent } from './pages/logout/logout.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { ForbiddenComponent } from './pages/forbidden/forbidden.component';
import { LoadingInterceptor } from './core/interceptors/loading.interceptor';
import { AuthappService } from './core/services/authapp.service';
import { LoggingService } from './core/services/logging.service';
import { PublicRoutesService } from './core/services/public-routes.service';
import { catchError, defaultIfEmpty, lastValueFrom, of } from 'rxjs';
import { ArticleCardComponent } from './shared/components/article-card/article-card.component';
import { ArticlesGridComponent } from './pages/articles-grid/articles-grid.component';

export function initializeApp(authService: AuthappService) {
  return () => {
    // Chiama la logica complessa del Service e la converte in Promise
    return lastValueFrom(authService.initializeAuthStatus().pipe(defaultIfEmpty(null)));
  };
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    WelcomeComponent,
    Page404Component,
    Page50xComponent,
    ArticlesComponent,
    ArticleCardComponent,
    ArticlesGridComponent,
    LogoutComponent,
    ArticleManagerComponent,
    ForbiddenComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CoreModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthappService, LoggingService, PublicRoutesService],
      multi: true
    },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
