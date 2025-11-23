import { Component, OnInit} from '@angular/core';
import { BehaviorSubject, empty, Observable } from 'rxjs';
import { Article } from 'src/app/shared/models/Article';
import { Pagination } from 'src/app/shared/models/Pagination';
import { ArticleService } from 'src/app/shared/services/article.service';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { ArtStatus, ErrorMessages, StatusCodes } from 'src/app/shared/enums';
import { ErrorResponse } from 'src/app/shared/models/ErrorResponse';
import { LoggingService } from 'src/app/core/services/logging.service';
import { ScrollService } from 'src/app/core/services/scroll.service';

enum FilterTypes {
  Codart = 1,
  Description = 2,
  Barcode = 3
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

  serverError = false;

  // Pagination
  readonly MAX_PAG_BTNS_NR = 7;
  readonly MINIMUM_MAX_PAG_BTNS_NR = 5;
  paginationModel: Pagination = new Pagination(); // default values -> currentPage:1, pageSize:10
  paginationUI: any = {};

  // Filter
  filter: string = '';
  filterType: FilterTypes = FilterTypes.Description; // start with getArticlesByDesc
  filterTypeList = [ // Used by HTML template
    { key: "Codart", value: FilterTypes.Codart },
    { key: "Description", value: FilterTypes.Description },
    { key: "Barcode", value: FilterTypes.Barcode }
  ];

  // They are used to block multiple requests with the same filter AND the same type.
  lastFilter: string = this.sanitizeFilter(this.filter);
  lastFilterType: FilterTypes = this.filterType;

  errorResponse: ErrorResponse = {
    date: new Date(),
    status: -1,
    message: ""
  }

  private articlesSubject = new BehaviorSubject<Article[]>([]);
  articles$: Observable<Article[]> = this.articlesSubject.asObservable();

  codArt: string = "";
  successMsg: string = '';

  constructor(private articleService: ArticleService,
              private router: Router,
              private logger: LoggingService,
              private scrollService: ScrollService) {
    this.logger.log("constructor()");

    // Numero massimo di bottoni-pagina inclusi i bottoni '...' ed esclusi 'previous' e 'next'
    // Il minimo valore impostabile è rappresentato da MINIMUM_MAX_PAG_BTNS_NR
    // e verrà utilizzato quando 'maxPagBtnsNr' non sarà più impostato hardcoded
    this.paginationUI.maxPagBtnsNr = this.MAX_PAG_BTNS_NR;
    this.paginationUI.pagButtonsNr = this.paginationUI.maxPagBtnsNr;
  }

  ngOnInit(): void {
    this.logger.log("ngOnInit()");

    this.getArticles();
  }

  // Rimuove spazi iniziali/finali dal filter e se è null, undefined o vuoto, restituisce stringa vuota.
  private sanitizeFilter(filter?: string): string {
  return filter?.trim() ?? '';
  }


  setPagination = (): void => {
    this.logger.log("setPagination()")
    // I bottoni-pagina (pagButtons) mostrano le pagine (pages)
    // attenzione a non confonderli

    // Se il numero totale delle pagine 'totalPages'
    // è inferiore al numero massimo di bottoni-pagina 'maxPagBtnsNr'
    // reimposta il numero totale di bottoni-pagina 'pagButtonsNr' che è
    // impostato di default come il numero massimo di bottoni-pagina 'maxPagBtnsNr'
    if (this.paginationModel.totalPages < this.paginationUI.maxPagBtnsNr) {
      this.paginationUI.pagButtonsNr = this.paginationModel.totalPages;
    }
    this.logger.log("paginationUI: ", this.paginationUI);
  }

  refresh = (): void => {
    this.logger.log("refresh()");

    const sanitizedFilter = this.sanitizeFilter(this.filter);

    if (sanitizedFilter === this.lastFilter && this.filterType === this.lastFilterType) {
      this.logger.log("Refresh skipped: same filter and type.");
      return;
    }

    this.logger.log("Refresh executed.");

    this.paginationModel = new Pagination();
    this.resetResponses();
    this.getArticles();

    this.lastFilter = sanitizedFilter;
    this.lastFilterType = this.filterType;
  }

  private getLiteralArtStatus = (idArtStatus: string): string => {
    const mapArtStatus: Record<string, ArtStatus> = {
      '1': ArtStatus.Active,
      '2': ArtStatus.Suspended,
      '3': ArtStatus.Deleted
    }
    return mapArtStatus[idArtStatus] ?? ArtStatus.Error;
  }

  // ***************************************
  // ******** CRUD Articles - Start ********
  // ***************************************
  resetResponses = (): void => {
    this.successMsg = '';
    this.errorResponse = {
      date: new Date(),
      status: -1,
      message: ""
    }
  }

  // READ - CRUD Articles
  getArticles = (): void => {
    this.logger.log("getArticles()");

    const observable: Observable<HttpResponse<any>> = this.getObservableByFilterType();
    this.logger.log("observable -> ", observable);

    observable.subscribe({
      next: response => this.handleSuccessGET(response),
      error: error => this.handleErrorGET(error)
    });
  }
  private getObservableByFilterType(): Observable<HttpResponse<any>> {
    this.logger.log("getObservableByFilterType()");
    this.logger.log("filter -> " + this.filter);
    this.logger.log("filterType -> " + this.filterType);

    const sanitizedFilter = this.sanitizeFilter(this.filter);

    const filterMap: Record<FilterTypes, () => Observable<HttpResponse<any>>> = {
      [FilterTypes.Codart]: () => this.articleService.getArticleByCodart(sanitizedFilter),
      [FilterTypes.Description]: () => this.articleService.getArticlesByDesc(sanitizedFilter, this.paginationModel),
      [FilterTypes.Barcode]: () => this.articleService.getArticleByBarcode(sanitizedFilter)
    }

    const getObs: () => Observable<HttpResponse<any>> = filterMap[this.filterType];

    if (!getObs) {
      throw new Error("Invalid filterType");
    }

    return getObs();
  }
  private handleSuccessGET = (response: any): void => {
    this.logger.log("---- H A N D L E  R E S P O N S E ----");
    this.logger.log("handleResponse()");
    this.logger.log(response);

    // In base alla risposta, assegna la lista di articles OR il singolo article
    const articles: Article[] = response.body.itemList || [response.body];

    // Converte lo stato id numerico di ogni article in letterale (es. 1 -> 'Attivo')
    articles.map(art => {
      if (art.idArtStatus) {
        art.idArtStatus = this.getLiteralArtStatus(art.idArtStatus);
      }
    });

    // Setta la paginazione se la risposta la fornisce
    if (response.body.pagination) {
      this.paginationModel = response.body.pagination;
      this.setPagination();
    }

    this.articlesSubject.next(articles);
  }
  // Gestione errore per GET (lettura articoli)
  private handleErrorGET = (error: any): void => {
    // Puliamo la lista degli articoli in caso di errore
    this.articlesSubject.next([]);

    const sanitizedFilter = this.sanitizeFilter(this.filter);

    const messages: Record<FilterTypes, { empty: string; filled: string }> = {
      [FilterTypes.Description]:{
        empty: `Currently there are no articles.`,
        filled: `We couldn't find any articles matching '${sanitizedFilter}'.`
      },
      [FilterTypes.Codart]:{
        empty: `Insert a Codart.`,
        filled: `The article with Codart '${sanitizedFilter}' is not found.`
      },
      [FilterTypes.Barcode]:{
        empty: `Insert a Barcode.`,
        filled: `The article with Barcode '${sanitizedFilter}' is not found.`
      }
    };

    // Messaggio personalizzato per NotFound (404)
    const isEmpty = sanitizedFilter === '';
    const notFoundMsg =
      isEmpty
        ? messages[this.filterType].empty
        : messages[this.filterType].filled;


    // Passiamo l'errore e il messaggio personalizzato per NotFound (404)
    this.processError(error, notFoundMsg);
  }

  // CREATE - CRUD Articles
  createArt() {
    this.router.navigate(['article-manager']);
  }

  // UPDATE - CRUD Articles
  updateArt(codArt: string) {
    this.router.navigate(['article-manager', codArt]);
  }

  // DELETE - CRUD Articles
  deleteArt = (codArt: string): void => {
    this.resetResponses();
    this.codArt = codArt;

    this.articleService.deleteArticleByCodart(codArt).subscribe({
      next: this.handleSuccessDELETE,
      error: this.handleErrorDELETE
      // error: this.handleErrorResp
    });
  }
  private handleSuccessDELETE = (response: any): void => {
    this.logger.log("handleSuccessResp()");
    this.logger.log(response);

    this.successMsg = response.body.message;

    this.filterType = FilterTypes.Description;
    this.getArticles();

    //  Scroll down the page to the alert element with the response message
    this.scrollService.scrollToAnchor('successAlert');
  }
  private handleErrorDELETE = (error: any): void => {
    // Per la cancellazione possiamo usare sempre lo stesso messaggio se non trovato
    this.processError(error, ErrorMessages.ElementNotFound);
  }

  // Metodo generale per processare un errore HTTP.
  // Gestisce sia gli errori con JSON inviato dal backend sia errori senza JSON.
  private processError(error: any, notFoundMsg: string): void {
    this.logger.log("processError()", error);

    // error.error -> risposta JSON inviata dal backend di tipo ErrorResponse
    //               (es. { status: 404, message: "Article not found" })
    //
    // error.status -> il codice HTTP reale ricevuto dal protocollo (es. 404, 500, 403)

    // Se il backend invia JSON, prendiamo il codice dal JSON (backend status)
    // Altrimenti fallback sul codice HTTP reale (protocollo)
    this.errorResponse.status = error.error?.status ?? error.status ?? -1;

    // Se il backend invia JSON, prendiamo il messaggio dal JSON (backend message)
    // altrimenti fallback su un messaggio generico.
    this.errorResponse.message = error.error?.message || ErrorMessages.GenericError;

    // Se l’errore è un NotFound imposta il messaggio personalizzato che è stato passato
    if (error.status === StatusCodes.NotFound) {
      this.errorResponse.message = notFoundMsg;
    }

    // Scrolla la pagina verso l’alert degli errori
    this.scrollService.scrollToAnchor('errorAlert');
  }
  // *************************************
  // ******** End - CRUD Articles ********
  // *************************************

  // ******** paginationUI - Start ********
  // pageNr: numeroPagina passato dalla pagina HTML al metodo
  // this.paginationUI.pagButtonsNr: Attuale numero di bottoni-pagina visualizzato
  // this.paginationModel.currentPage: numeroPagina corrente
  // this.paginationModel.totalPages: totale delle pagine messe a disposizione dal server

  // Invocato al click del singolo bottone-pagina
  pageChange = (currentPage: string|number): void => {
    this.paginationModel.currentPage = typeof(currentPage)==='string' ? parseInt(currentPage) : currentPage;

    // Reimposta 'filterType' con FilterTypes.ByDesc così
    // 'this.getArticles()' invoca 'getObservableByFilterType()'
    // che a sua volta invoca 'getArticlesByDesc()'.
    // Questo è l'unico dei 3 metodi GET che invia al server
    // i valori di 'paginationModel' tramite url
    // (es. http://localhost:5051/api/articles/cerca/descrizione/pane?currentPage=1&pageSize=10)
    this.filterType = FilterTypes.Description;

    this.resetResponses();
    this.getArticles();
  }

  // Ritorna un booleano per mostrare o meno il 'pageNr' su uno dei bottoni-pagina
  shouldDisplayPageNr(pageNr: number): boolean {
    const firstPage = 1;
    const lastPage = this.paginationModel.totalPages;
    const currentPage = this.paginationModel.currentPage;
    const maxPagBtnsNr = this.paginationUI.maxPagBtnsNr; // 7
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
    return this.paginationModel.currentPage === pageNr;
  }

  // Ritorna il valore da stampare sul singolo bottone-pagina
  displayPageNr(pageNr: number): string {
    const firstPage = 1;
    const lastPage = this.paginationModel.totalPages;
    const currentPage = this.paginationModel.currentPage;
    const maxPagBtnsNr = this.paginationUI.maxPagBtnsNr; // 7
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
// ******** End - Pagination ********
}
