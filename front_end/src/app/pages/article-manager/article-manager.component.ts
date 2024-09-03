import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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

  title: string = "Edit Article";

  codArt: string = "";

  errorMessage: string = "";
  successMessage: string = "";
  respStatusCode: number = -1;

  errorValidationMap!: ErrorValidationMap;

  readonly REQUIRED_FIELD_MSG = "Required field";

  article: Article = {
    codArt: "",
    description: "",
    um: "",
    codStat: "",
    pcsCart: 0,
    netWeight: 0,
    idArtStatus: "",
    price: 0,
    active: true,
    creationDate: new Date(),
    urlImage: "",
    category: {id: 0, description: ""},
    vat: {idVat: 0, description: "", taxRate: 0},
    barcodes: [{barcode: "", type: ""}],
  }

  categories: Category[] = [];
  vatList: Vat[] = [];

  constructor(private route: ActivatedRoute,
              private articleService: ArticleService,
              private scroller: ViewportScroller) {

  }

  ngOnInit(): void {
    this.codArt = this.route.snapshot.params['codArt'];
    console.log("Selected article " + this.codArt);

    this.errorValidationMap = this.createErrorValidationMap<Article>(this.article);

    this.articleService.getArticleByCodart(this.codArt).subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    });

    this.articleService.getCategories().subscribe(
      response => this.categories = response.body as Category[]
    );

    this.articleService.getVatList().subscribe(
      response => this.vatList = response.body as Vat[]
    );
  }

  handleResponse(response: any){
    console.log("handleResponse()");
    console.log(response);

    this.respStatusCode = response.status;
    this.article = response.body;

    if (this.article.barcodes.length == 0) {
      this.article.barcodes = [{barcode: "", type: ""}];
    }

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

    this.articleService.artUpdate(this.article).subscribe({
      next: response => {
        this.handleResponse(response);
        this.successMessage = "Edit article successfully executed!";
      },
      error: error => this.handleError(error)
    });
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
