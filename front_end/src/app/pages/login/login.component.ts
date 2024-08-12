import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthappService } from '../../services/authapp.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  title: string = "Login & Authentication";
  subtitle: string = "Login or Sign Up";

  userId: string = "Dario";

  authenticated: boolean = true;
  errMsg: string = "Sorry, the credentials entered are incorrect!";

  constructor(private route: Router, private authapp: AuthappService) { }

  ngOnInit(): void {
  }

  gestAuth(f: NgForm): void {
    if (this.authapp.authenticate(f.value.username, f.value.password)) {
      sessionStorage.setItem("username", f.value.username);
      this.route.navigate(['welcome', this.userId]);
      this.authenticated = true;
    } else {
      this.authenticated = false;
    }
  }
}
