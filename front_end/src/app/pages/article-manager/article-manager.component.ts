import { ViewportScroller } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { log } from 'console';
import { Article, Barcode, Category, Vat } from 'src/app/models/Article';
import { ErrorResponse } from 'src/app/models/ErrorResponse';
import { ArticleService } from 'src/app/services/data/article.service';
import { ErrorMessages, StatusCodes } from 'src/app/shared/Enums';
import { ErrorValidationMap } from 'src/app/shared/Types';
import { scrollToErrorAlert, scrollToSuccessAlert } from 'src/app/shared/scroll-helpers';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  readonly CREATE_MODE_TITLE = "Create Article";
  readonly EDIT_MODE_TITLE = "Edit Article";
  readonly REQ_FIELD_MSG = "Required field";
  readonly PATTERN_FIELD_MSG = "The field can only contain letters and numbers"
  readonly MIN_MAX_NR_FIELD_MSG = "Min 0 - Max 100";
  readonly NO_NEG_NR_FIELD_MSG = "No negative numbers";
  readonly NO_NEG_NR_OR_ZERO_FIELD_MSG = "No negative numbers or 0";

  title: string = "";
  codArt: string = "";
  isEditMode: boolean = false;

  errorResp$: ErrorResponse = {
    date: new Date(),
    code: -1,
    message: "",
    errorValidationMap: {}
  }

  get operationType(): string {
    return this.isEditMode ? "edit" : "creation";
  }
  get succOperationMsg(): string {
    return "Article " + this.operationType + " successfully executed!";
  }

  successMsg: string = "";
  respStatusCode: number = -1;

  article: Article = {
    codArt: "",
    description: "",
    barcodes: [],
    creationDate: new Date()
  }
  categories: Category[] = [];
  vatList: Vat[] = [];

  // Used from form elements with [(ngModel)]
  selectedVat?: number | null;
  selectedCategory?: number | null;
  selectedStatus?: string | null;
  selectedBarcodes: Barcode[] = [];

  constructor(private route: ActivatedRoute,
              private articleService: ArticleService,
              private scroller: ViewportScroller) {

  }

  ngOnInit(): void {

    this.errorResp$.errorValidationMap = this.createErrorValidationMap<Article>(this.article);

    // GET ARTICLE/S
    if (this.route.snapshot.params['codArt']) {
      this.title = this.EDIT_MODE_TITLE;
      this.isEditMode = true;

      this.codArt = this.route.snapshot.params['codArt'];
      console.log("Selected article " + this.codArt);

      this.articleService.getArticleByCodart(this.codArt).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    } else {
      this.title = this.CREATE_MODE_TITLE;
      this.isEditMode = false;
    }
    // GET CATEGORIES
    this.articleService.getCategories().subscribe(
      response => this.categories = response.body as Category[]
    );
    // GET VAT-LIST
    this.articleService.getVatList().subscribe(
      response => this.vatList = response.body as Vat[]
    );
  }
  // ******* ngOnInit() - END *******

  handleResponse(response: any){
    console.log("handleResponse()");
    console.log(response);

    console.log("ARTICOLO_1 - handleResponse()");
    console.log(this.article);

    this.respStatusCode = response.status;
    this.article = response.body;
    this.article.vat = this.article.vat || null;

    console.log("ARTICOLO_2 - handleResponse()");
    console.log(this.article);

    // Used from form elements with [(ngModel)]
    this.selectedVat = this.article.vat ? this.article.vat.idVat : null;
    this.selectedCategory = this.article.category ? this.article.category.id : null;
    this.selectedStatus = this.article.idArtStatus != null ? this.article.idArtStatus : null;
    this.selectedBarcodes = this.article.barcodes.length > 0  ? response.body.barcodes[0].barcode : [];

    //  Scroll down the page to the alert element with the response message
    scrollToSuccessAlert(this.scroller);
  };

  handleError(error: any){
    console.log("handleError()");
    console.log(error);

    this.errorResp$ = error.error;

    if (error.status === StatusCodes.UnavailableServer) {
      this.errorResp$.code = 0;
      this.errorResp$.message = ErrorMessages.UnavailableServer;
    } else if (error.status === StatusCodes.NotFound){
      console.error(ErrorMessages.ElementNotFound);
    } else if (error.status === StatusCodes.Forbidden){
      console.error(ErrorMessages.OperationNotAllowed);
    } else {
      console.error(ErrorMessages.GenericError);
      console.error(error); // Registra l'errore nella console
    }
    //  Scroll down the page to the alert element with the error message
    scrollToErrorAlert(this.scroller);
  }

  saveArt = (artForm: NgForm) => {
    console.log("saveArt()");

    const barcodes: Barcode[] = this.article.barcodes;

    console.log("ART-FORM - saveArt()");
    console.log(artForm.value);
    this.article = artForm.value;

    this.article.barcodes = barcodes;
    this.article.category = this.categories.find(cat => cat.id === artForm.value.category);
    this.article.vat = this.vatList.find(vat => vat.idVat === artForm.value.vat);

    // RESET response variables
    this.successMsg = '';
    this.respStatusCode = -1;
    this.errorResp$ = {
      date: new Date(),
      code: -1,
      message: "",
      errorValidationMap: this.createErrorValidationMap<Article>(this.article)
    }

    console.log("ARTICLE - saveArt()");
    console.log(this.article);

    if (this.isEditMode) {  // EDIT MODE
      this.articleService.updateArt(this.article).subscribe({
        next: response => {
          this.handleResponse(response);
          this.successMsg = this.succOperationMsg;
        },
        error: error => this.handleError(error)
      });
    } else {  // CREATE MODE
      this.articleService.createArt(this.article).subscribe({
        next: response => {
          this.handleResponse(response);
          this.successMsg = this.succOperationMsg;
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
