import { Component, OnInit } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';
import { ArticoloResponse } from 'src/app/models/ArticoloResponse';
import { Pagination } from 'src/app/models/Pagination';
import { ArticoliService } from 'src/app/services/data/articoli.service';

@Component({
  selector: 'app-grid-articles',
  templateUrl: './grid-articles.component.html',
  styleUrls: ['./grid-articles.component.scss']
})
export class GridArticlesComponent implements OnInit {

  public pagination$: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10
  public articoli$: Articolo[] = [];
  public errore: string = "";
  public filter: string = "";
  public totalPages: any[] = [];

  constructor(private articoliService: ArticoliService) { }

  ngOnInit(): void {
    this.articoliService.getArticlesByDesc(this.filter, this.pagination$).subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    })
  }

  handleResponse = (response: ArticoloResponse): void => {
    this.articoli$ = response.itemList;
    this.pagination$ = response.pagination;

    this.totalPages = [];
    for (let i = 1; i <= this.pagination$.totalPages; i++) {
      this.totalPages.push(i);
    }
  }
  handleError = (error: Object): void => {
    this.errore = error.toString();
    console.error(this.errore);
  }


  handleEdit = (codart: string) => {
    let articolo = this.articoli$.find(art => art.codArt === codart);
    console.log("E' stato modificato l'articolo " + articolo?.descrizione);
  }
  handleDelete = (codart: string) => {
    this.articoli$.splice(this.articoli$.findIndex(articolo => articolo.codArt === codart), 1);
  }
}
