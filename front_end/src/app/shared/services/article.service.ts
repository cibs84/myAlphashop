import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Pagination } from 'src/app/shared/models/Pagination';
import { PaginatedResponseList } from 'src/app/shared/models/PaginatedResponseList';
import { Article, Vat, Category } from 'src/app/shared/models/Article';
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  apiUrl: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }

  private queryStringBuilder(pagination?: Pagination) {
    const currentPage = pagination?.currentPage ?? 1;
    const pageSize = pagination?.pageSize ?? 10;
    return `?currentPage=${currentPage}&pageSize=${pageSize}`;
  }

  getArticleByCodart = (/*codArt*/ filter: string) => {
    return this.httpClient.get<Article>(`${this.apiUrl}/articles/find/codart/${filter}`,
      { observe: 'response' }
    );
  };

  getArticlesByDesc = (/*description*/ filter: string, pagination: Pagination) => {
    return this.httpClient.get<PaginatedResponseList<Article>>(`${this.apiUrl}/articles/find/description/${filter}${this.queryStringBuilder(pagination)}`,
      {observe: "response"}
    );
  };

  getArticleByBarcode = (/*barcode(=ean)*/ filter: string) => {
    return this.httpClient.get<Article>(`${this.apiUrl}/articles/find/barcode/${filter}`,
      {observe: "response"}
    );
  };

  deleteArticleByCodart = (codArt: string) => {
    return this.httpClient.delete<Object>(`${this.apiUrl}/articles/delete/${codArt}`,
      {observe: "response"}
    );
  }

  createArt = (article: Article) => {
    return this.httpClient.post<Article>(`${this.apiUrl}/articles/create`, article,
      {observe: "response"}
    );
  }

  updateArt = (article: Article) => {
    return this.httpClient.put<Article>(`${this.apiUrl}/articles/update`, article,
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
