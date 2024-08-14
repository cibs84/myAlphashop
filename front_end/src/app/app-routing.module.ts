import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { LogoutComponent } from './pages/logout/logout.component';
import { WelcomeComponent } from './pages/welcome/welcome.component';
import { ErrorComponent } from './pages/error/error.component';
import { RouteGuardService } from './services/route-guard.service';
import { ArticlesGridComponent } from './pages/articles-grid/articles-grid.component';
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
  {path:'articles/grid', component:ArticlesGridComponent, canActivate:[RouteGuardService]},
  {path:'article-manager/:codart', component:ArticleManagerComponent, canActivate:[RouteGuardService]},
  {path:'**', component:ErrorComponent, canActivate:[RouteGuardService]},
  {path:'**', component:ErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
