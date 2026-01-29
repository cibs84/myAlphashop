import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LogoutComponent } from './pages/logout/logout.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { Page404Component } from './pages/page-404/page-404.component';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleManagerComponent } from './pages/article-manager/article-manager.component';
import { ForbiddenComponent } from './pages/forbidden/forbidden.component';
import { Roles } from './shared/enums/roles.enum';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { ArticleDetailComponent } from './pages/article-detail/article-detail.component';
import { Page5xxComponent } from './pages/page-5xx/page-5xx.component';
import { PendingChangesGuard } from './core/guards/pending-changes.guard';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  { path: 'logout', component: LogoutComponent },
  {
    path: 'welcome',
    component: WelcomeComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.User] },
  },
  {
    path: 'home',
    component: WelcomeComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.User] },
  },
  {
    path: 'welcome/:username',
    component: WelcomeComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.User] },
  },
  {
    path: 'articles',
    component: ArticlesComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.User] },
  },
  {
    path: 'articles/detail/:codart',
    component: ArticleDetailComponent,
    canActivate: [RoleGuard],
    data: { roles: [Roles.User] },
  },
  {
    path: 'articles/manage/new',
    component: ArticleManagerComponent,
    canActivate: [AuthGuard, RoleGuard],
    canDeactivate: [PendingChangesGuard],
    data: { roles: [Roles.Admin] },
  },
  {
    path: 'articles/manage/:codart',
    component: ArticleManagerComponent,
    canActivate: [AuthGuard, RoleGuard],
    canDeactivate: [PendingChangesGuard],
    data: { roles: [Roles.Admin] },
  },
  { path: '5xx', component: Page5xxComponent },
  { path: 'forbidden', component: ForbiddenComponent },
  { path: '**', component: Page404Component },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      initialNavigation: 'enabledBlocking'
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
