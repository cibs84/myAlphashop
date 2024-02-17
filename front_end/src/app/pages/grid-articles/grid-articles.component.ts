import { Component, OnInit } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';
import { ArticoliService } from 'src/app/services/articoli.service';

@Component({
  selector: 'app-grid-articles',
  templateUrl: './grid-articles.component.html',
  styleUrls: ['./grid-articles.component.scss']
})
export class GridArticlesComponent implements OnInit {

  public articoli$: Articolo[] = [];

  constructor(private articoliService: ArticoliService) { }

  ngOnInit(): void {
    this.articoli$ = this.articoliService.getArticoli();
    console.log(this.articoli$);
  }

  handleEdit = (codart: string) => {
    console.log("E' stato modificato l'articolo " + this.articoliService.getArticoloByCodart(codart).descrizione);
  }

  handleDelete = (codart: string) => {
    this.articoli$.splice(this.articoli$.findIndex(articolo => articolo.codart === codart), 1);
  }
}
