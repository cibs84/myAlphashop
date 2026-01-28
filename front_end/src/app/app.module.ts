import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login/login.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleManagerComponent } from './pages/article-manager/article-manager.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { Page404Component } from './pages/page-404/page-404.component';
import { Page5xxComponent } from './pages/page-5xx/page-5xx.component';
import { CoreModule } from './core/core.module';
import { LogoutComponent } from './pages/logout/logout.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { ForbiddenComponent } from './pages/forbidden/forbidden.component';
import { LoadingInterceptor } from './core/interceptors/loading.interceptor';
import { AuthappService } from './core/services/authapp.service';
import { defaultIfEmpty, lastValueFrom, of } from 'rxjs';
import { GenericCardComponent } from './shared/components/generic-card/generic-card.component';
import { PaginationComponent } from './shared/components/pagination/pagination.component';
import { ArticlesTableComponent } from './shared/components/articles-table/articles-table.component';
import { LiteralItemStatusPipe } from './shared/pipes/literal-item-status.pipe';
import { ArticleDetailComponent } from './pages/article-detail/article-detail.component';
import { TranslatePipe } from './shared/pipes/translate.pipe';
import { GlobalErrorInterceptor } from './core/interceptors/global-error.interceptor';
import { StatusInfoComponent } from './shared/components/status-info/status-info.component';
import { AppModalComponent } from './shared/components/app-modal/app-modal.component';

export function initializeApp(authService: AuthappService) {
  return () => {
    return lastValueFrom(authService.initializeAuthStatus().pipe(defaultIfEmpty(null)));
  };
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    WelcomeComponent,
    Page404Component,
    Page5xxComponent,
    ArticlesComponent,
    GenericCardComponent,
    LogoutComponent,
    ArticleManagerComponent,
    ForbiddenComponent,
    PaginationComponent,
    ArticlesTableComponent,
    LiteralItemStatusPipe,
    ArticleDetailComponent,
    TranslatePipe,
    StatusInfoComponent,
    AppModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CoreModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [AuthappService],
      multi: true
    },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: GlobalErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
