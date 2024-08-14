import { Component, OnInit} from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { Observable } from 'rxjs';
import { Article } from 'src/app/models/Article';
import { ArticleResponse } from 'src/app/models/ArticleResponse';
import { Pagination } from 'src/app/models/Pagination';
import { ArticleService } from 'src/app/services/data/article.service';

enum FilterTypes {
  ByCodart = 1,
  ByDesc = 2,
  ByBarcode = 3
}

enum StatusCodes {
  UnavailableServer = 0, // e.g. server disconnected
  BadRequest = 400, // e.g. malformed request syntax
  NotFound = 404, // item not found (read)
  Conflict = 409, // e.g. item already exists (create)
  UnprocessableEntity = 422, // e.g validation error (create, update)
  Forbidden = 403, // e.g. not erasable item (delete)
  Success = 200,
  Accepted = 202,
  NoContent = 204
}

enum ErrorMessages {
  UnavailableServer = 'The server is temporarily unavailable',
  OperationNotAllowed = "Operation not allowed",
  GenericError = 'An error occurred',
  ElementNotFound = 'Element not found'
}

enum ArtStatus {
  Active = 'Active',
  Suspended = 'Suspended',
  Deleted = 'Deleted',
  Error = 'Error'
}

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss']
})
export class ArticlesComponent implements OnInit {

  artStatus: typeof ArtStatus = ArtStatus;
  errorMessages: typeof ErrorMessages = ErrorMessages;
  statusCodes: typeof StatusCodes = StatusCodes;

  // PAGINATION
  readonly MAX_PAG_BTNS_NR = 7;
  readonly MINIMUM_MAX_PAG_BTNS_NR = 5;

  articles$: Article[] = [];

  filter: string = '';

  // It's used to block multiple requests with the same filter.
  private lastFilter: string = '';

  respObj$:any = {
    code: "",
    message: ""
  };

  error$:any = {
    date: new Date(),
    code: "",
    message: ""
  }

  codArt: string = "";

  pagination$: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10
  pagination: any = {};

  filterType: number = FilterTypes.ByDesc; // start with getArticlesByDesc

  constructor(private articleService: ArticleService,
              private scroller: ViewportScroller
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

  scrollToSuccessAlert = (): void => {
    console.log("scrollToSuccessAlert()");

    // This delay ensures the element is ready for interaction before scrolling.
    setTimeout(() => {
      this.scroller.scrollToAnchor('successAlert');
    }, 100);
  }

  scrollToErrorAlert = (): void => {
    console.log("scrollToErrorAlert()");

    // This delay ensures the element is ready for interaction before scrolling.
    setTimeout(() => {
      this.scroller.scrollToAnchor('errorAlert');
    }, 100);
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
    this.respObj$ = {
      code: "",
      message: ""
    };

    this.error$ = {
      date: new Date(),
      code: "",
      message: ""
    }
  }

  // READ
  getArticles = (): void => {
    this.resetResponses(); // reset respObj$ and error$

    console.log("articles.components.ts -> getArticles()");
    console.log("filterType -> " + this.filterType);

    const observable: Observable<ArticleResponse> = this.getObservableByFilterType();
    observable.subscribe({
      next: this.handleResponse,
      error: this.handleError
    });
  }

  private getObservableByFilterType(): Observable<ArticleResponse> {
    console.log("filter -> " + this.filter);
    console.log(this.pagination$);
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
    this.articles$ = response.itemList || [response];

    // Converte lo stato id numerico di ogni article in letterale (es. 1 -> 'Attivo')
    this.articles$.map(art => {
      art.idArtStatus = this.getLiteralArtStatus(art.idArtStatus);
    });

    if (response.pagination) {
      this.pagination$ = response.pagination;
      this.setPagination();
    }

    this.filterType = FilterTypes.ByCodart;
  }

  private handleError = (error: any): void => {
    console.log("handleError()");
    console.log(error);

    this.articles$ = []; // Clean article list

    if (error.status === StatusCodes.UnavailableServer) {
        console.log("filter: "+this.filter);
        console.log(ErrorMessages.UnavailableServer);

        this.error$.code = 0;
        this.error$.message = ErrorMessages.UnavailableServer;
    } else if (error.status === StatusCodes.NotFound) {
      if (this.filterType < 3) {
        console.log("error.status -> " + error.status);
        console.log("error.message -> " + error.message);
        console.log("this.filterType -> " + this.filterType);

        this.filterType++;
        this.getArticles();
      } else if (this.filter === '') {
          console.log(`Currently there are no articles!`);

          this.error$.code = error.error.code;
          this.error$.message = `Currently there are no articles!`;
      } else {
        console.log(`Article with filter '${this.filter}' is not found!`);

        this.error$.code = error.error.code;
        this.error$.message = `The article '${this.filter}' is not found`;
      }
    } else {
      console.log(ErrorMessages.GenericError);
      console.error(error); // Registra l'errore nella console
    }
    //  Scrolla la pagina all'elemento di alert con il messaggio d'errore
    this.scrollToErrorAlert();
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

  // UPDATE
  // updateArt(codArt: string) {
  //   this.resetResponses();

  //   this.codArt = codArt;
  //   this.articleService.updateArticle(codArt).subscribe({
  //     next: this.handleSuccessResp,
  //     error: this.handleErrorResp
  //   });
  // }

  private handleSuccessResp = (resp: any): void => {
    console.log("handleSuccessResp()");
    console.log(resp);
    console.log("this.codArt -> " + this.codArt);

    this.respObj$ = resp;

    this.filterType = FilterTypes.ByDesc;
    this.getArticles();

    //  Scrolla la pagina all'elemento di alert con il messaggio di risposta
    this.scrollToSuccessAlert();
  }
  private handleErrorResp = (error: any): void => {
    console.log("handleErrorResp()");
    console.log(error);

    this.error$ = error.error;
    if (error.status === StatusCodes.UnavailableServer) {
      console.log(ErrorMessages.UnavailableServer);

      this.error$.code = 0;
      this.error$.message = ErrorMessages.UnavailableServer;

      console.log("this.error$.code -> " + this.error$.code);
      console.log('this.error$.message -> ' + this.error$.message);
    } else if (error.status === StatusCodes.NotFound){
      console.error(ErrorMessages.ElementNotFound);
    } else if (error.status === StatusCodes.Forbidden){
      console.error(ErrorMessages.OperationNotAllowed);
    } else {
      console.error(ErrorMessages.GenericError);
      console.error(error); // Registra l'errore nella console
    }
    //  Scrolla la pagina all'elemento di alert con il messaggio di errore
    this.scrollToErrorAlert();
  }
  // ******** End - CRUD Articles ********


  // ----- PAGINATION -----
  // pageNr -> numeroPagina passato dalla pagina HTML al metodo
  // this.pagination.pagButtonsNr -> Attuale numero di bottoni-pagina visualizzato
  // this.pagination$.currentPage -> numeroPagina corrente
  // this.pagination$.totalPages -> totale delle pagine messe a disposizione dal server

  // Invocato al click del singolo bottone-pagina
  pageChange = (currentPage: string|number): void => {
    console.log("currentPage -> " + currentPage);
    this.pagination$.currentPage = typeof(currentPage)==='string' ? parseInt(currentPage) : currentPage;

    // Reimposta 'filterType' con FilterTypes.ByDesc così
    // 'this.getArticles()' invoca 'getObservableByFilterType()'
    // che a sua volta invoca 'getArticlesByDesc()'.
    // Questo è l'unico dei 3 metodi GET che invia al server
    // i valori di 'pagination$' tramite url
    // (es. http://localhost:5051/api/articles/cerca/descrizione/pane?currentPage=1&pageSize=10)
    this.filterType = FilterTypes.ByDesc;

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
