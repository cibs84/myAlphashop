import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse} from '@angular/common/http';
import { Pagination } from 'src/app/models/Pagination';
import { ArticleResponse } from 'src/app/models/ArticleResponse';
import { Article, Category, Vat } from 'src/app/models/Article';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  // articoli: Articolo[] = [
  //   {codart : '014600301', descrizione : 'BARILLA FARINA 1 KG', um : 'PZ', pcscart : 24, peso : 1, prezzo : 1.09, active : true, data : new Date(), urlImage : "assets/img/articles/farina_barilla_1kg.png"},
  //   {codart : "013500121", descrizione : "BARILLA PASTA GR.500 N.70 1/2 PENNE", um : "PZ", pcscart : 30, peso : 0.5, prezzo : 1.3, active : true, data : new Date(), urlImage : "assets/img/articles/barilla_mezze_penne_1kg.jpeg"},
  //   {codart : "007686402", descrizione : "FINDUS FIOR DI NASELLO 300 GR", um : "PZ", pcscart : 8, peso : 0.3, prezzo : 6.46, active : true, data : new Date(), urlImage : "assets/img/articles/findus_fior_di_nasello_300gr.jpeg"},
  //   {codart : "057549001", descrizione : "FINDUS CROCCOLE 400 GR", um : "PZ", pcscart : 12, peso : 0.4, prezzo : 5.97, active : true, data : new Date(), urlImage : "assets/img/articles/findus_croccole_400gr.jpeg"}
  // ];

  pagination: Pagination = new Pagination();
  server: string = 'localhost';
  port: string = '5051';

  constructor(private httpClient: HttpClient) { }


  getArticleByCodart = (/*codArt*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter ? filter : ' ';

    return this.httpClient.get(`http://${this.server}:${this.port}/api/article/findByCodart/${filter}`,
      {observe: "response"}
    );
  };

  getArticlesByDesc = (/*description*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter.trim() || ' ';

    return this.httpClient.get(`http://${this.server}:${this.port}/api/articles/findByDescription/${filter}?currentPage=${this.pagination.currentPage}&pageSize=${this.pagination.pageSize}`,
      {observe: "response"}
    );
  };

  getArticleByBarcode = (/*barcode(=ean)*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter || ' ';

    return this.httpClient.get(`http://${this.server}:${this.port}/api/article/findByBarcode/${filter}`,
      {observe: "response"}
    );
  };

  deleteArticleByCodart = (codArt: string) => {
    return this.httpClient.delete(`http://${this.server}:${this.port}/api/article/delete/${codArt}`,
      {observe: "response"}
    );
  }

  private setPagination = (pagination: Pagination|undefined) => {
    if (typeof(pagination)!=='undefined') {
      this.pagination = pagination;
    };
  }

  artUpdate = (article: Article) => {
    return this.httpClient.put<HttpResponse<any>>(`http://${this.server}:${this.port}/api/article/update`, article,
      {observe: "response"}
    );
  }

  getCategories = () => {
    return this.httpClient.get<Category[]>(`http://${this.server}:${this.port}/api/categories`,
    {observe: "response"}
    );
  }

  getVatList = () => {
    return this.httpClient.get<Vat[]>(`http://${this.server}:${this.port}/api/vat-list`,
    {observe: "response"}
    );
  }
}
