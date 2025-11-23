import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GreetingsDataService } from 'src/app/shared/services/greetings-data.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {

  title: string = "Welcome to Alphashop";
  subtitle: string = "View deals of the day";
  greetings: string = "";
  error: string = "";
  userId: string = '';

  constructor(private route: ActivatedRoute, private greetingsDataService: GreetingsDataService) { }

  ngOnInit(): void {
    this.userId = (this.route.snapshot.params['username'] !== undefined) ? this.route.snapshot.params['username'] : "";

    //*** Alternativa con le query param ***/
    // this.route.queryParams.subscribe(
    //   params => this.userId = params['username'] || 'Gianfranco'
    // )
  }

  getGreetings = (): void => {
    this.greetingsDataService.getGreetings(this.userId).subscribe({
      next: this.responseHandler.bind(this),
      error: this.errorHandler.bind(this)
    });
  };

  responseHandler = (response: Object): void => {
    this.greetings = response.toString();
  }

  errorHandler = (error: any): void => {
    this.error = error.error.message;
  }
}
