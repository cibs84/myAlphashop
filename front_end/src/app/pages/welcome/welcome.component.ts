import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SalutiDataService } from 'src/app/services/data/saluti-data.service';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {

  titolo: string = "Benvenuti in Alphashop";
  sottotitolo: string = "Visualizza le offerte del giorno";
  saluti: string = "";
  errore: string = "";
  userId: string = '';

  constructor(private route: ActivatedRoute, private salutiService: SalutiDataService) { }

  ngOnInit(): void {
    this.userId = (this.route.snapshot.params['username'] !== undefined) ? this.route.snapshot.params['username'] : "";

    //*** Alternativa con le query param ***/
    // this.route.queryParams.subscribe(
    //   params => this.userId = params['username'] || 'Gianfranco'
    // )
  }

  getSaluti = (): void => {
    this.salutiService.getSaluti(this.userId).subscribe({
      next: this.responseHandler.bind(this),
      error: this.errorHandler.bind(this)
    });
  };

  responseHandler = (response: Object): void => {
    this.saluti = response.toString();
  }

  errorHandler = (error: any): void => {
    this.errore = error.error.message;
  }
}
