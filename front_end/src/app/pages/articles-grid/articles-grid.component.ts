import { Component, OnInit } from '@angular/core';
import { Article } from 'src/app/models/Article';
import { ArticleResponse } from 'src/app/models/ArticleResponse';
import { Pagination } from 'src/app/models/Pagination';
import { ArticleService } from 'src/app/services/data/article.service';

@Component({
  selector: 'app-articles-grid',
  templateUrl: './articles-grid.component.html',
  styleUrls: ['./articles-grid.component.scss']
})
export class ArticlesGridComponent implements OnInit {

  public pagination$: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10
  public articles$: Article[] = [];
  public error: string = "";
  public filter: string = "";
  public totalPages: any[] = [];

  constructor(private articleService: ArticleService) { }

  ngOnInit(): void {
    this.articleService.getArticlesByDesc(this.filter, this.pagination$).subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    })
  }

  handleResponse = (response: ArticleResponse): void => {
    this.articles$ = response.itemList;
    this.pagination$ = response.pagination;

    this.totalPages = [];
    for (let i = 1; i <= this.pagination$.totalPages; i++) {
      this.totalPages.push(i);
    }
  }
  handleError = (error: Object): void => {
    this.error = error.toString();
    console.error(this.error);
  }


  handleEdit = (codart: string) => {
    let article = this.articles$.find(art => art.codArt === codart);
    console.log("The article have been modified " + article?.description);
  }
  handleDelete = (codart: string) => {
    this.articles$.splice(this.articles$.findIndex(article => article.codArt === codart), 1);
  }
}
