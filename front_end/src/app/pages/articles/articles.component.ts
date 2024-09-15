import { Component, OnInit} from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { Observable } from 'rxjs';
import { Article } from 'src/app/models/Article';
import { Pagination } from 'src/app/models/Pagination';
import { ArticleService } from 'src/app/services/data/article.service';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { ArtStatus, ErrorMessages, StatusCodes } from 'src/app/shared/Enums';
import { scrollToErrorAlert, scrollToSuccessAlert } from 'src/app/shared/scroll-helpers';
import { ErrorResponse } from 'src/app/models/ErrorResponse';
import { log } from 'console';

enum FilterTypes {
  ByCodart = 1,
  ByDesc = 2,
  ByBarcode = 3
}

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss']
})
export class ArticlesComponent implements OnInit {

  // ENUMS
  artStatus: typeof ArtStatus = ArtStatus;
  errorMessages: typeof ErrorMessages = ErrorMessages;
  statusCodes: typeof StatusCodes = StatusCodes;

  // PAGINATION
  readonly MAX_PAG_BTNS_NR = 7;
  readonly MINIMUM_MAX_PAG_BTNS_NR = 5;
  pagination$: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10
  pagination: any = {};

  filter: string = '';
  filterType: number = FilterTypes.ByDesc; // start with getArticlesByDesc

  // It's used to block multiple requests with the same filter.
  private lastFilter: string = '';

  errorResp$: ErrorResponse = {
    date: new Date(),
    code: -1,
    message: ""
  }

  articles$: Article[] = [];
  codArt: string = "";
  successMsg: string = '';

  constructor(private articleService: ArticleService,
              private scroller: ViewportScroller,
              private router: Router
  ) {
    console.log("constructor()");

    // Numero massimo di bottoni-pagina inclusi i bottoni '...' ed esclusi 'previous' e 'next'
    // Il minimo valore impostabile è rappresentato da MINIMUM_MAX_PAG_BTNS_NR
    // e verrà utilizzato quando 'maxPagBtnsNr' non sarà più impostato hardcoded
    this.pagination.maxPagBtnsNr = this.MAX_PAG_BTNS_NR;
    this.pagination.pagButtonsNr = this.pagination.maxPagBtnsNr;
  }

  ngOnInit(): void {
    console.log("ngOnInit()");

    this.getArticles();
  }

  setPagination = (): void => {
    console.log("setPagination()")
    // I bottoni-pagina (pagButtons) mostrano le pagine (pages)
    // attenzione a non confonderli

    // Se il numero totale delle pagine 'totalPages'
    // è inferiore al numero massimo di bottoni-pagina 'maxPagBtnsNr'
    // reimposta il numero totale di bottoni-pagina 'pagButtonsNr' che è
    // impostato di default come il numero massimo di bottoni-pagina 'maxPagBtnsNr'
    if (this.pagination$.totalPages < this.pagination.maxPagBtnsNr) {
      this.pagination.pagButtonsNr = this.pagination$.totalPages;
    }
    console.log(this.pagination);
  }

  refresh = (): void => {
    console.log("refresh()");

    if (this.filter !== this.lastFilter || this.filter === '') {
      console.log("refresh() - IF");

      this.pagination$ = new Pagination();

      // Se si invia la ricerca col campo vuoto
      // si imposta il filtro ricerca per descrizione
      this.filterType = this.filter === '' ? FilterTypes.ByDesc : FilterTypes.ByCodart;

      this.resetResponses();
      this.getArticles();
      this.lastFilter = this.filter;
    }
  }

  private getLiteralArtStatus = (idArtStatus: string): string => {
    if (idArtStatus === '1') {
      return ArtStatus.Active;
    } else if (idArtStatus === '2') {
      return ArtStatus.Suspended;
    } else if (idArtStatus === '3') {
      return ArtStatus.Deleted;
    } else {
      return ArtStatus.Error;
    }
  }

  // ******** CRUD Articles ********
  resetResponses = (): void => {
    this.successMsg = '';
    this.errorResp$ = {
      date: new Date(),
      code: -1,
      message: ""
    }
  }

  // READ
  getArticles = (): void => {
    console.log("getArticles()");

    const observable: Observable<HttpResponse<any>> = this.getObservableByFilterType();
    observable.subscribe({
      next: this.handleResponse,
      error: this.handleError
    });
  }

  private getObservableByFilterType(): Observable<HttpResponse<any>> {
    console.log("getObservableByFilterType()");
    console.log("filter -> " + this.filter);
    console.log("filterType -> " + this.filterType);

    if (this.filterType === FilterTypes.ByCodart) {
      return this.articleService.getArticleByCodart(this.filter, this.pagination$);
    } else if (this.filterType === FilterTypes.ByDesc) {
      return this.articleService.getArticlesByDesc(this.filter, this.pagination$);
    } else if (this.filterType === FilterTypes.ByBarcode) {
      if (this.filter === '') {
        return this.articleService.getArticlesByDesc(this.filter, this.pagination$);;
      } else {
        return this.articleService.getArticleByBarcode(this.filter, this.pagination$);
      }
    } else {
      throw new Error("Invalid filterType");
    }
  }

  private handleResponse = (response: any): void => {
    console.log("handleResponse()");
    console.log(response);

    // In base alla risposta, assegna ad articles$ la lista di articles OR il singolo article
    this.articles$ = response.body.itemList || [response.body];

    // Converte lo stato id numerico di ogni article in letterale (es. 1 -> 'Attivo')
    this.articles$.map(art => {
      if (art.idArtStatus) {
        art.idArtStatus = this.getLiteralArtStatus(art.idArtStatus);
      }
    });

    if (response.body.pagination) {
      this.pagination$ = response.body.pagination;
      this.setPagination();
    }

    this.filterType = FilterTypes.ByCodart;
  }

  private handleError = (error: any): void => {
    console.log("handleError()");
    console.log(error);
    console.log(error.error);

    this.articles$ = []; // Clean article list
    this.errorResp$ = Object.assign({}, this.errorResp$, error.error);

    if (error.status === StatusCodes.UnavailableServer) {
      this.errorResp$.code = 0;
      this.errorResp$.message = ErrorMessages.UnavailableServer;

    } else if (error.status === StatusCodes.NotFound) {
      if (this.filterType < 3) {
        this.filterType++;
        this.resetResponses();
        this.getArticles();
      } else if (this.filter === '') {
          this.errorResp$.message = `Currently there are no articles!`;
      } else {
        this.errorResp$.message = `The article '${this.filter}' is not found`;
      }

    } else if (error.status === StatusCodes.Unauthorized){
      console.error(ErrorMessages.AuthenticationException);
      this.errorResp$.message = error.error;
    } else {
      this.errorResp$.message = ErrorMessages.GenericError;
    }

    //  Scrolla la pagina all'elemento di alert con il messaggio d'errore
    scrollToErrorAlert(this.scroller);
  }

  // DELETE
  deleteArt = (codArt: string): void => {
    this.resetResponses();
    this.codArt = codArt;

    this.articleService.deleteArticleByCodart(codArt).subscribe({
      next: this.handleSuccessResp,
      error: this.handleErrorResp
    });
  }

  // CREATE
  createArt() {
    this.router.navigate(['article-manager']);
  }

  // UPDATE
  updateArt(codArt: string) {
    this.router.navigate(['article-manager', codArt]);
  }

  private handleSuccessResp = (response: any): void => {
    console.log("handleSuccessResp()");
    console.log(response);

    this.successMsg = response.body.message;

    this.filterType = FilterTypes.ByDesc;
    this.getArticles();

    //  Scroll down the page to the alert element with the response message
    scrollToSuccessAlert(this.scroller);
  }
  private handleErrorResp = (error: any): void => {
    console.log("handleErrorResp()");
    console.log(error);

    this.errorResp$ = error.error;

    if (error.status === StatusCodes.UnavailableServer) {
      this.errorResp$.code = 0;
      this.errorResp$.message = ErrorMessages.UnavailableServer;
    } else if (error.status === StatusCodes.NotFound){
      console.error(ErrorMessages.ElementNotFound);
    } else if (error.status === StatusCodes.Forbidden){
      console.error(ErrorMessages.OperationNotAllowed);
    } else if (error.status === StatusCodes.Unauthorized){
      console.error(ErrorMessages.AuthenticationException);
      this.errorResp$.code = 401;
      this.errorResp$.message = ErrorMessages.AuthenticationException;
    } else {
      this.errorResp$.message = ErrorMessages.GenericError;
    }
    //  Scroll down the page to the alert element with the error message
    scrollToErrorAlert(this.scroller);
  }
  // ******** End - CRUD Articles ********


  // ----- PAGINATION -----
  // pageNr: numeroPagina passato dalla pagina HTML al metodo
  // this.pagination.pagButtonsNr: Attuale numero di bottoni-pagina visualizzato
  // this.pagination$.currentPage: numeroPagina corrente
  // this.pagination$.totalPages: totale delle pagine messe a disposizione dal server

  // Invocato al click del singolo bottone-pagina
  pageChange = (currentPage: string|number): void => {
    this.pagination$.currentPage = typeof(currentPage)==='string' ? parseInt(currentPage) : currentPage;

    // Reimposta 'filterType' con FilterTypes.ByDesc così
    // 'this.getArticles()' invoca 'getObservableByFilterType()'
    // che a sua volta invoca 'getArticlesByDesc()'.
    // Questo è l'unico dei 3 metodi GET che invia al server
    // i valori di 'pagination$' tramite url
    // (es. http://localhost:5051/api/articles/cerca/descrizione/pane?currentPage=1&pageSize=10)
    this.filterType = FilterTypes.ByDesc;

    this.resetResponses();
    this.getArticles();
  }

  // Ritorna un booleano per mostrare o meno il 'pageNr' su uno dei bottoni-pagina
  shouldDisplayPageNr(pageNr: number): boolean {
    const firstPage = 1;
    const lastPage = this.pagination$.totalPages;
    const currentPage = this.pagination$.currentPage;
    const maxPagBtnsNr = this.pagination.maxPagBtnsNr; // 7
    const constNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante
    const isFirstOrLastPage = (pageNr === firstPage || pageNr === lastPage);

    return (
            isFirstOrLastPage
            ||
            (pageNr >= 1 && pageNr <= firstPage+(maxPagBtnsNr-constNrBtns))
              &&
            (currentPage >= firstPage && currentPage <= 4)
            ||
            (pageNr >= lastPage-(maxPagBtnsNr-constNrBtns) && pageNr <= lastPage)
              &&
            (currentPage >= lastPage-3 && currentPage <= lastPage)
            ||
            (currentPage >= firstPage+4 && currentPage <= lastPage-4)
              &&
            (pageNr >= currentPage-2 && pageNr <= currentPage+2)
           );
  }

  isCurrentPageNr(pageNr: number): boolean {
    return this.pagination$.currentPage === pageNr;
  }

  // Ritorna il valore da stampare sul singolo bottone-pagina
  displayPageNr(pageNr: number): string {
    const firstPage = 1;
    const lastPage = this.pagination$.totalPages;
    const currentPage = this.pagination$.currentPage;
    const maxPagBtnsNr = this.pagination.maxPagBtnsNr; // 7
    const constNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante

    const showEllipsis = (
                          (currentPage >= firstPage && currentPage <= 4)
                            &&
                          pageNr == firstPage+(maxPagBtnsNr-constNrBtns)
                          ||
                          (currentPage >= lastPage-3 && currentPage <= lastPage)
                            &&
                          pageNr == lastPage-(maxPagBtnsNr-constNrBtns)
                          ||
                          (currentPage >= firstPage+4 && currentPage <= lastPage-4)
                            &&
                          (pageNr == currentPage-2 || pageNr == currentPage+2)
                        );

    return showEllipsis ? "..." : pageNr.toString();
  }
// ----- PAGINATION - END -----
}
