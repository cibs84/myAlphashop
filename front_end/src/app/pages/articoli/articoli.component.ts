import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';
import { Pagination } from 'src/app/models/Pagination';
import { ArticoliService } from 'src/app/services/data/articoli.service';

@Component({
  selector: 'app-articoli',
  templateUrl: './articoli.component.html',
  styleUrls: ['./articoli.component.scss']
})
export class ArticoliComponent implements OnInit {

  articoli$: Articolo[] = [];
  errore: string = "";

  filter: string = '';
  pagination$: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10

  totalPages: any[] = [];
  subscriptionType: number = 2;

  @Output()
  myClick: EventEmitter<any> = new EventEmitter();

  constructor(private articoliService: ArticoliService) { }

  ngOnInit(): void {
    this.getArticoli();
  }

  refresh = (): void => {
    this.errore = "";
    console.log("refresh(): valore di this.filter -> " + this.filter);
    this.getArticoli();
  }

  getArticoli = (): void => {
    if (this.subscriptionType === 1) {
      this.articoliService.getArticoloByCodart(this.filter, this.pagination$).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    } else if (this.subscriptionType === 2) {
      this.articoliService.getArticoliByDesc(this.filter, this.pagination$).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    } else {
      this.articoliService.getArticoloByBarcode(this.filter, this.pagination$).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    }
  }

  private handleResponse = (response: any): void => {
    this.articoli$ = response.itemList || [response];
    this.articoli$.map(art => {
      art.idStatoArt = this.getIdStatoArt(art.idStatoArt);
    });
    this.pagination$ = response.pagination || this.pagination$;

    this.subscriptionType = 1; // reset subscriptionType

    this.totalPages = [];
    for (let i = 1; i <= this.pagination$.totalPages; i++) {
      this.totalPages.push(i);
    }
  }

  private handleError = (error: any): void => {
    if (this.subscriptionType < 3) {
      this.subscriptionType++;
      this.getArticoli();
    } else {
      console.log(error);
      this.errore = error.error.message;
      console.error(this.errore);
    }
  }

  setPageNumber = (currentPage: string|number): void => {
    this.pagination$.currentPage = typeof(currentPage)==='string' ? parseInt(currentPage) : currentPage;
    this.getArticoli();
  }

  private getIdStatoArt = (idStatoArt: string): string => {
    if (idStatoArt === '1') {
      return "Attivo";
    } else if (idStatoArt === '2') {
      return "Sospeso";
    } else if (idStatoArt === '3') {
      return "Eliminato";
    } else {
      return "Errore";
    }
  }
}
