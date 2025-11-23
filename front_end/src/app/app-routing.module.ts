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
import { Page50xComponent } from './pages/page-50x/page-50x.component';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';
import { ArticlesGridComponent } from './pages/articles-grid/articles-grid.component';

const routes: Routes = [
  {path:'', component:LoginComponent},
  {path:'login', component:LoginComponent},
  {path:'logout', component:LogoutComponent},
  {path:'welcome', component:WelcomeComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.User]}},
  {path:'home', component:WelcomeComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.User]}},
  {path:'welcome/:username', component:WelcomeComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.User]}},
  {path:'articles', component:ArticlesComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.User]}},
  {path:'article-manager/:codArt', component:ArticleManagerComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.Admin]}},
  {path:'article-manager', component:ArticleManagerComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.Admin]}},
  {path:'articles/grid', component:ArticlesGridComponent, canActivate:[AuthGuard, RoleGuard], data:{roles:[Roles.User]}},
  {path:'50x', component:Page50xComponent},
  {path:'forbidden', component:ForbiddenComponent},
  {path:'**', component:Page404Component}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      initialNavigation: 'enabled'
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
