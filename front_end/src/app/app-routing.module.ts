import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LogoutComponent } from './pages/logout/logout.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { Page404Component } from './pages/page-404/page-404.component';
import { Page50xComponent } from './pages/page-50x/page-50x.component';
import { RouteGuardService } from './services/route-guard.service';
import { ArticlesComponent } from './pages/articles/articles.component';
import { ArticleManagerComponent } from './pages/article-manager/article-manager.component';

const routes: Routes = [
  {path:'', component:LoginComponent},
  {path:'login', component:LoginComponent},
  {path:'logout', component:LogoutComponent},
  {path:'welcome', component:WelcomeComponent, canActivate:[RouteGuardService]},
  {path:'home', component:WelcomeComponent, canActivate:[RouteGuardService]},
  {path:'welcome/:username', component:WelcomeComponent, canActivate:[RouteGuardService]},
  {path:'articles', component:ArticlesComponent, canActivate:[RouteGuardService]},
  {path:'article-manager/:codArt', component:ArticleManagerComponent, canActivate:[RouteGuardService]},
  {path:'article-manager', component:ArticleManagerComponent, canActivate:[RouteGuardService]},
  // {path:'50x', component:Page50xComponent},
  // {path:'api/**', component:Page50xComponent},
  {path:'**', component:Page404Component}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
