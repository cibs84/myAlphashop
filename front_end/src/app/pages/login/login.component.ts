import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { AuthappService } from '../../services/authapp.service';
import { Observable, map, of } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  title: string = "Login & Authentication";
  subtitle: string = "Login or Sign Up";

  authenticated: boolean = true;
  notlogged: boolean = false;
  nologgedQueryParam$: Observable<string | null> = of("");

  errMsgBadCred: string = 'Sorry, userid or password is incorrect!';
  errMsgLoginReq: string = "Login is required to access the selected page";

  constructor(private route: Router,
              private authapp: AuthappService,
              private activeRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.nologgedQueryParam$ = this.activeRoute.queryParamMap.pipe(
      map((params: ParamMap) => params.get('nologged')),
    );

    this.nologgedQueryParam$.subscribe(
      param => (param) ? this.notlogged = true : this.notlogged = false
    );
  }

  gestAuth(f: NgForm): void {
    this.authapp.authenticate(f.value.username, f.value.password).subscribe({
      next: (response) => {
        console.log(response);

        this.route.navigate(['welcome', f.value.username]);
        this.authenticated = true;
      },
      error: (error) => {
        console.log(error);

        this.authenticated = false;
      }
    });
  }
}
