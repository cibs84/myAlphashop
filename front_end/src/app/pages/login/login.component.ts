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

  titolo: string = "Accesso & Autenticazione";
  sottotitolo: string = "Accedi oppure registrati";

  userId: string = "Dario";

  autenticato: boolean = true;
  errMsg: string = "Spiacente, le credenziali inserite non sono corrette!";

  constructor(private route: Router, private authapp: AuthappService) { }

  ngOnInit(): void {
  }

  gestAuth(f: NgForm): void {
    if (this.authapp.authenticate(f.value.username, f.value.password)) {
      sessionStorage.setItem("username", f.value.username);
      this.route.navigate(['welcome', this.userId]);
      this.autenticato = true;
    } else {
      this.autenticato = false;
    }
  }
}
