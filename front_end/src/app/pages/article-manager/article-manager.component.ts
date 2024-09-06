import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { log } from 'console';
import { Article, Category, Vat } from 'src/app/models/Article';
import { ArticleService } from 'src/app/services/data/article.service';
import { ErrorMessages } from 'src/app/shared/Enums';
import { ErrorValidationMap } from 'src/app/shared/Types';
import { scrollToErrorAlert, scrollToSuccessAlert } from 'src/app/shared/scroll-helpers';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  title: string = "";
  codArt: string = "";
  isEditMode: boolean = true;

  errorMessage: string = "";
  successMessage: string = "";
  respStatusCode: number = -1;

  errorValidationMap!: ErrorValidationMap;

  readonly REQ_FIELD_MSG = "Required field";
  readonly MIN_MAX_NR_FIELD_MSG = "Min 0 - Max 100";
  readonly NO_NEG_NR_FIELD_MSG = "No negative numbers";
  readonly NO_NEG_NR_OR_ZERO_FIELD_MSG = "No negative numbers or 0";
  operationType: string = this.isEditMode ? "edit" : "creation";
  succOperationMsg = "Article " + this.operationType + " successfully executed!";

  article: Article = {
    codArt: "",
    description: "",
    barcodes: [],
    creationDate: new Date()
  }

  categories: Category[] = [];
  vatList: Vat[] = [];


  constructor(private route: ActivatedRoute,
              private articleService: ArticleService,
              private scroller: ViewportScroller) {

  }

  ngOnInit(): void {
    this.errorValidationMap = this.createErrorValidationMap<Article>(this.article);

    if (this.route.snapshot.params['codArt']) {
      this.title = "Edit Article";
      this.isEditMode = true;

      this.codArt = this.route.snapshot.params['codArt'];
      console.log("Selected article " + this.codArt);

      this.articleService.getArticleByCodart(this.codArt).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    } else {
      this.title = "Create Article";
      this.isEditMode = false;
    }

    this.articleService.getCategories().subscribe(
      response => this.categories = response.body as Category[]
    );

    this.articleService.getVatList().subscribe(
      response => this.vatList = response.body as Vat[]
    );

    console.log(this.errorValidationMap);
    console.log("isEditMode: " + this.isEditMode);

  }

  handleResponse(response: any){
    console.log("handleResponse()");
    console.log("isEditMode : " + this.isEditMode);
    console.log("this.succOperationMsg : " + this.succOperationMsg);

    console.log(response);

    this.respStatusCode = response.status;
    this.article = response.body;

    //  Scroll down the page to the alert element with the response message
    scrollToSuccessAlert(this.scroller);
  };

  handleError(error: any){
    console.log("handleError()");
    console.log(error);

    this.respStatusCode = error.status;
    this.errorMessage = error.error.message || ErrorMessages.UnavailableServer;
    this.errorValidationMap = error.error.errorValidationMap;

    console.log(this.respStatusCode);
    console.log(this.errorValidationMap);

    //  Scroll down the page to the alert element with the error message
    scrollToErrorAlert(this.scroller);
  }

  saveArt = () => {
    console.log("saveArt()");

    this.errorMessage = "";
    this.successMessage = "";
    this.errorValidationMap = this.createErrorValidationMap<Article>(this.article);

    console.log(this.article);

    if (this.article.barcodes.length == 0) {
      this.article.barcodes = [];
    }

    if (this.isEditMode) {
      this.articleService.updateArt(this.article).subscribe({
        next: response => {
          this.handleResponse(response);
          console.log(this.succOperationMsg);

          this.successMessage = this.succOperationMsg;
        },
        error: error => this.handleError(error)
      });
    } else {
      this.articleService.createArt(this.article).subscribe({
        next: response => {
          this.handleResponse(response);
          this.successMessage = this.succOperationMsg;
        },
        error: error => this.handleError(error)
      });
    }
  }

  // Creates a map object to initialize error messages for each item (=article) property
  // Used to set an empty initial value for each possible validation error
  createErrorValidationMap<T>(item: T): ErrorValidationMap {
    const map: ErrorValidationMap = {};
    for (const key in item) {
      if (Object.prototype.hasOwnProperty.call(item, key)) {
        map[key] = [''];
      }
    }
    return map;
  }
}
