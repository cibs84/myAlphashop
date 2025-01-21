import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse} from '@angular/common/http';
import { Pagination } from 'src/app/models/Pagination';
import { Article, Category, Vat } from 'src/app/models/Article';
import { PaginatedResponseList } from 'src/app/models/PaginatedResponseList';
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  pagination: Pagination = new Pagination();
  apiUrl: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }


  getArticleByCodart = (/*codArt*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter ? filter : ' ';

    return this.httpClient.get<HttpResponse<Article>>(`${this.apiUrl}/articles/find/codart/${filter}`,
      {observe: "response"}
    );
  };

  getArticlesByDesc = (/*description*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter.trim() || ' ';

    return this.httpClient.get<HttpResponse<PaginatedResponseList<Article>>>(`${this.apiUrl}/articles/find/description/${filter}?currentPage=${this.pagination.currentPage}&pageSize=${this.pagination.pageSize}`,
      {observe: "response"}
    );
  };

  getArticleByBarcode = (/*barcode(=ean)*/ filter: string, pagination?: Pagination) => {
    this.setPagination(pagination);
    filter = filter || ' ';

    return this.httpClient.get<HttpResponse<Article>>(`${this.apiUrl}/articles/find/barcode/${filter}`,
      {observe: "response"}
    );
  };

  deleteArticleByCodart = (codArt: string) => {
    return this.httpClient.delete<HttpResponse<Object>>(`${this.apiUrl}/articles/delete/${codArt}`,
      {observe: "response"}
    );
  }

  private setPagination = (pagination: Pagination|undefined) => {
    if (typeof(pagination)!=='undefined') {
      this.pagination = pagination;
    };
  }

  createArt = (article: Article) => {
    return this.httpClient.post<HttpResponse<Article>>(`${this.apiUrl}/articles/create`, article,
      {observe: "response"}
    );
  }

  updateArt = (article: Article) => {
    return this.httpClient.put<HttpResponse<Article>>(`${this.apiUrl}/articles/update`, article,
      {observe: "response"}
    );
  }

  getCategories = () => {
    return this.httpClient.get<Category[]>(`${this.apiUrl}/categories/find/all`,
      {observe: "response"}
    );
  }

  getVatList = () => {
    return this.httpClient.get<Vat[]>(`${this.apiUrl}/vat/find/all`,
      {observe: "response"}
    );
  }
}
