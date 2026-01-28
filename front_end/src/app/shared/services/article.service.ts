import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Pagination } from 'src/app/shared/models/Pagination';
import { PaginatedResponseList } from 'src/app/shared/models/PaginatedResponseList';
import { environment } from "src/environments/environment";
import { ArticleResponse } from '../models/ArticleResponse';
import { ArticleCreateRequest } from '../models/ArticleCreateRequest';
import { ArticleUpdateRequest } from '../models/ArticleUpdateRequest';
import { Category } from '../models/Category';
import { Vat } from '../models/Vat';

@Injectable({
  providedIn: 'root',
})
export class ArticleService {
  apiUrl: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) {}

  getArticleByCodart = (codart: string) => {
    return this.httpClient.get<ArticleResponse>(
      `${this.apiUrl}/articles/${codart}`
    );
  };

  getArticlesByDesc = (description: string, pagination: Pagination) => {
    return this.httpClient.get<PaginatedResponseList<ArticleResponse>>(
      `${this.apiUrl}/articles${this.queryStringBuilder(
        { description: description },
        pagination
      )}`
    );
  };

  getArticleByBarcode = (barcode: string) => {
    return this.httpClient.get<ArticleResponse>(
      `${this.apiUrl}/articles/by-barcode/${barcode}`
    );
  };

  deleteArticleByCodart = (codart: string) => {
    return this.httpClient.delete<void>(`${this.apiUrl}/articles/${codart}`);
  };

  createArt = (article: ArticleCreateRequest) => {
    return this.httpClient.post<ArticleResponse>(
      `${this.apiUrl}/articles`,
      article
    );
  };

  updateArt = (article: ArticleUpdateRequest, codart: string) => {
    return this.httpClient.put<ArticleResponse>(
      `${this.apiUrl}/articles/${codart}`,
      article
    );
  };

  getCategories = () => {
    return this.httpClient.get<Category[]>(
      `${this.apiUrl}/categories/find/all`
    );
  };

  getVatList = () => {
    return this.httpClient.get<Vat[]>(`${this.apiUrl}/vat/find/all`);
  };

  private queryStringBuilder(
    params: {
      description?: string;
    },
    pagination?: Pagination
  ) {
    let queryString = '?';
    Object.entries(params).forEach(
      ([key, value]) => (queryString += `${key}=${value}&`)
    );

    queryString = queryString.slice(0, -1); // Remove '&' from tail

    if (pagination != null) {
      const currentPage = pagination?.currentPage ?? 1;
      const pageSize = pagination?.pageSize ?? 10;
      queryString += `&currentPage=${currentPage}&pageSize=${pageSize}`;
    }

    return queryString;
  }
}
