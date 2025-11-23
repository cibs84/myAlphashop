import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthappService } from '../../core/services/authapp.service';
import { LoggingService } from 'src/app/core/services/logging.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  title: string = "Login & Authentication";
  subtitle: string = "Login or Sign Up";

  private isLoading: boolean = false;

  errMsg: string = '';
  private errMsgBadCred: string = 'Sorry, userid or password is incorrect!';

  constructor(private route: Router, private authapp: AuthappService,
              private logger: LoggingService) { }

  ngOnInit(): void { }

  login(f: NgForm): void {
    if (this.isLoading) return;

    this.isLoading = true;
    this.errMsg = '';

    this.authapp.login(f.value.username, f.value.password).subscribe({
      next: (success) => {
        if (success) {
          this.logger.log("✅ Login riuscito!", "Navigate to Welcome page");
          this.route.navigate(['welcome']);
        } else {
          this.logger.log("❌ Login fallito durante il recupero delle info utente.");
          this.errMsg = "Errore durante il recupero delle informazioni utente.";
        }
        this.isLoading = false;
      },
      error: (error) => {
        this.logger.log("❌ Login fallito", error);
        this.errMsg = error.status === 401 ? this.errMsgBadCred : "Errore imprevisto.";
        this.isLoading = false;
      }
    });
  }
}
