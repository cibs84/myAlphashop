import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  userId: string = "";
  password: string = "";

  autenticato: boolean = true;
  errMsg: string = "Spiacente, le credenziali inserite non sono corrette!";

  constructor(private route: Router) { }

  ngOnInit(): void {
  }

  gestAuth = (): void => {
    if (this.userId !== "Dario" || this.password !== "password") {
      this.autenticato = false;
    } else {
      this.route.navigate(['welcome', this.userId]);
      this.autenticato = true;
    }
  }

}
