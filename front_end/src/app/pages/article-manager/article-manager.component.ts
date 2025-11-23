import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Article, Category, Vat } from 'src/app/shared/models/Article';
import { ErrorResponse } from 'src/app/shared/models/ErrorResponse';
import { ArticleService } from 'src/app/shared/services/article.service';
import { ErrorMessages, StatusCodes } from 'src/app/shared/enums';
import { ErrorValidationMap } from 'src/app/shared/types/ErrorValidationMap';
import { LoggingService } from 'src/app/core/services/logging.service';
import { forkJoin, switchMap } from 'rxjs';
import { ScrollService } from 'src/app/core/services/scroll.service';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  // Titles for the page
  readonly CREATE_MODE_TITLE = "Create Article";
  readonly EDIT_MODE_TITLE = "Edit Article";

  // Error messages
  readonly REQ_FIELD_MSG = "Required field";
  readonly PATTERN_FIELD_MSG = "The field can only contain letters and numbers"
  readonly MIN_MAX_NR_FIELD_MSG = "Min 0 - Max 100";
  readonly NO_NEG_NR_FIELD_MSG = "No negative numbers";
  readonly NO_NEG_NR_OR_ZERO_FIELD_MSG = "No negative numbers or 0";

  title: string = "";
  isEditMode: boolean = false;

  get operationType(): string {
    return this.isEditMode ? "edit" : "creation";
  }
  get succOperationMsg(): string {
    return "Article " + this.operationType + " successfully executed!";
  }

  successMsg: string = "";
  errorResp: ErrorResponse = {
    date: new Date(),
    status: -1,
    message: "",
    errorValidationMap: {}
  }

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
  selectedBarcode?: string | null;

  constructor(private route: ActivatedRoute,
              private articleService: ArticleService,
              private logger: LoggingService,
              private scrollService: ScrollService) {

  }

  ngOnInit(): void {

    this.errorResp.errorValidationMap = this.createErrorValidationMap<Article>(this.article);

    console.log("article and errorValidationMap : ", this.article, this.errorResp.errorValidationMap);


    // GET ARTICLE(S)
    const codArt = this.route.snapshot.paramMap.get('codArt') ?? '';

    if (codArt) {
      this.title = this.EDIT_MODE_TITLE;
      this.isEditMode = true;

      this.logger.log("Selected article with codArt: " + codArt);

      this.articleService.getArticleByCodart(codArt).pipe(
        switchMap(article => {

          this.article = article.body as Article;

          this.logger.log("ARTICOLO_0 - ngOnInit()", this.article);

          // GET CATEGORIES and VAT LIST
          return forkJoin({
            categories: this.articleService.getCategories(),
            vatList: this.articleService.getVatList()
          });
        })
      ).subscribe({
        next: ({categories, vatList}) => {
          this.categories = categories.body as Category[];
          this.vatList = vatList.body as Vat[];
          this.handleResponse(this.article);
        },
        error: (error) => this.handleError(error)
      });
    } else {
      this.title = this.CREATE_MODE_TITLE;
      this.isEditMode = false;
    }
  }
  // ******* ngOnInit() - END *******

  handleResponse(response: any){
    this.logger.log("handleResponse()", response);

    this.logger.log("ARTICOLO_1 - handleResponse()", this.article);

    // Updates the existing 'article' by 'response.body'
    // Only the informations that are already in the 'article' will be replaced with the new values,
    // while the new informations will be added.
    this.article = Object.assign({}, this.article, response.body);

    this.logger.log("ARTICOLO_2 - handleResponse()", this.article);

    // Used by form elements with [(ngModel)]
    this.selectedVat = this.article.vat?.idVat ?? null;
    this.selectedCategory = this.article.category ? this.article.category.id : null;
    this.selectedStatus = this.article.idArtStatus ?? null;
    this.selectedBarcode = this.article.barcodes.length > 0  ? this.article.barcodes[0].barcode : null;

    //  Scroll down the page to the alert element with the response message
    this.scrollService.scrollToAnchor('successAlert');
  };

  handleError(error: any){
    this.logger.log("handleError()", error);

    this.errorResp = Object.assign({}, this.errorResp, error.error);

    if (error.status === StatusCodes.NotFound){
      this.logger.error(ErrorMessages.ElementNotFound);
      this.errorResp.message = ErrorMessages.ElementNotFound;
    } else if (error.status === StatusCodes.UnprocessableEntity){
      this.logger.error(ErrorMessages.ValidationError);
      this.errorResp.message = ErrorMessages.ValidationError;
    } else {
      this.logger.error(ErrorMessages.GenericError, error);
      this.errorResp.message = ErrorMessages.GenericError;
    }
    //  Scroll down the page to the alert element with the error message
    this.scrollService.scrollToAnchor('errorAlert');
  }

  saveArt = (artForm: NgForm) => {
    this.logger.log("saveArt()");
    this.logger.log("ART-FORM - saveArt()", artForm.value);

    // RESET response variables
    this.successMsg = '';
    this.errorResp = {
      date: new Date(),
      status: -1,
      message: "",
      errorValidationMap: this.createErrorValidationMap<Article>(this.article)
    }

    this.logger.log("ARTICLE - saveArt() - PRE-MOD", this.article);

    this.article.category = this.categories.find(cat => cat.id === artForm.value.category);
    this.article.vat = this.vatList.find(vat => vat.idVat === artForm.value.vat);

    this.logger.log("ARTICLE - saveArt() - POST-MOD", this.article);

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

  // USED BY TEMPLATE: Assign a CSS class to form fields
  // based on their validity and state (dirty/touched).
  getValidationClass(field: NgModel): string {
    if (field.dirty || field.touched || this.successMsg || this.errorResp.message) {
      return field.valid ? 'is-valid' : 'is-invalid';
    }
    return '';
  }

  // Creates a map object to initialize error messages for each item property
  // Used to set an empty initial value for each possible validation error
  private createErrorValidationMap<T>(item: T): ErrorValidationMap {
    const map: ErrorValidationMap = {};
    for (const key in item) {
      if (Object.prototype.hasOwnProperty.call(item, key)) {
        map[key] = [''];
      }
    }
    return map;
  }
}
