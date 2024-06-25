import { Component, OnInit} from '@angular/core';
import { ViewportScroller } from '@angular/common';
import { Observable } from 'rxjs';
import { Articolo } from 'src/app/models/Articolo';
import { ArticoloResponse } from 'src/app/models/ArticoloResponse';
import { Pagination } from 'src/app/models/Pagination';
import { ArticoliService } from 'src/app/services/data/articoli.service';

enum FilterTypes {
  ByCodart = 1,
  ByDesc = 2,
  ByBarcode = 3
}

enum StatusCodes {
  ServerUnavailable = 0, // e.g. server disconnected
  BadRequest = 400, // e.g. malformed request syntax
  NotFound = 404, // item not found (read)
  Conflict = 409, // e.g. item already exists (create)
  UnprocessableEntity = 422, // e.g validation error (create, update)
  Forbidden = 403, // e.g. not erasable item (delete)
  Success = 200,
  Accepted = 202
}

enum ErrorMessages {
  ServerNonDisponibile = 'Il server è momentaneamente non disponibile',
  OperazioneNonConsentita = "Operazione non consentita",
  ErroreGenerico = 'Si è verificato un errore',
  ElementoNonTrovato = 'Elemento non trovato'
}

enum StatoArt {
  Attivo = 'Attivo',
  Sospeso = 'Sospeso',
  Eliminato = 'Eliminato',
  Errore = 'Errore'
}

@Component({
  selector: 'app-articoli',
  templateUrl: './articoli.component.html',
  styleUrls: ['./articoli.component.scss']
})
export class ArticoliComponent implements OnInit {

  statoArt: typeof StatoArt = StatoArt;
  errorMessages: typeof ErrorMessages = ErrorMessages;

  // PAGINATION
  readonly MINIMUM_MAX_PAG_BTNS_NR = 5;

  articoli$: Articolo[] = [];

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

  filterType: number = FilterTypes.ByDesc; // start with getArticoliByDesc

  constructor(private articoliService: ArticoliService,
              private scroller: ViewportScroller
  ) {
    console.log("constructor()");

    // Numero massimo di bottoniPagina inclusi i bottoni '...' ed esclusi 'previous' e 'next'
    // Il minimo valore impostabile è rappresentato da MINIMUM_MAX_PAG_BTNS_NR
    // e verrà utilizzato quando 'maxPagBtnsNr' non sarà più impostato hardcoded
    this.pagination.maxPagBtnsNr = 7;
    this.pagination.pagButtonsNr = this.pagination.maxPagBtnsNr;
  }

  ngOnInit(): void {
    console.log("ngOnInit()");

    this.getArticoli();
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
    // I bottoniPagina (pagButtons) mostrano le pagine (pages)
    // attenzione a non confonderli

    // Se il numero totale delle pagine 'totalPages'
    // è inferiore al numero massimo di bottoniPagina 'maxPagBtnsNr'
    // reimposta il numero totale di bottoniPagina 'pagButtonsNr' che è
    // impostato di default come il numero massimo di bottoniPagina 'maxPagBtnsNr'
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

      this.getArticoli();
      this.lastFilter = this.filter;

      this.resetResponses(); // reset respObj$ and error$
    }
  }

  getArticoli = (): void => {
    console.log("articoli.components.ts -> getArticoli()");
    console.log("filterType -> " + this.filterType);

    const observable: Observable<ArticoloResponse> = this.getObservableByFilterType();
    observable.subscribe({
      next: this.handleResponse,
      error: this.handleError
    });
  }

  private getObservableByFilterType(): Observable<ArticoloResponse> {
    console.log("filter -> " + this.filter);
    console.log(this.pagination$);
    console.log("filterType -> " + this.filterType);

    switch (this.filterType) {
      case FilterTypes.ByCodart:
        return this.articoliService.getArticoloByCodart(this.filter, this.pagination$);
      case FilterTypes.ByDesc:
        return this.articoliService.getArticoliByDesc(this.filter, this.pagination$);
      case FilterTypes.ByBarcode:
        return this.articoliService.getArticoloByBarcode(this.filter, this.pagination$);
      default:
        throw new Error("Invalid filterType");
    }
  }

  private handleResponse = (response: any): void => {
    console.log("handleResponse()");
    console.log(response);

    // In base alla risposta, assegna ad articoli$ la lista di articoli OR il singolo articolo
    this.articoli$ = response.itemList || [response];

    // Converte lo stato id numerico di ogni articolo in letterale (es. 1 -> 'Attivo')
    this.articoli$.map(art => {
      art.idStatoArt = this.getLiteralStatoArt(art.idStatoArt);
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

    if (error.status === StatusCodes.ServerUnavailable) {
      console.log("filter: "+this.filter);
      console.log(ErrorMessages.ServerNonDisponibile);

      this.error$.code = 0;
      this.error$.message = ErrorMessages.ServerNonDisponibile;
    } else if (error.status === StatusCodes.NotFound) {
      if (this.filterType < 3) {
        console.log("error.status -> " + error.status);
        console.log("error.message -> " + error.message);
        console.log("this.filterType -> " + this.filterType);

        this.filterType++;
        this.getArticoli();
      } else {
        console.log(`Articolo con filtro '${this.filter}' non è stato trovato!`);

        this.error$.code = error.error.code;
        this.error$.message = `L'articolo '${this.filter}' non è stato trovato`;
      }
    } else {
      console.log(ErrorMessages.ErroreGenerico);
      console.error(error); // Registra l'errore nella console
    }

    this.scrollToErrorAlert();
  }

  private getLiteralStatoArt = (idStatoArt: string): string => {
    if (idStatoArt === '1') {
      return StatoArt.Attivo;
    } else if (idStatoArt === '2') {
      return StatoArt.Sospeso;
    } else if (idStatoArt === '3') {
      return StatoArt.Eliminato;
    } else {
      return StatoArt.Errore;
    }
  }

  // ******** CRUD Articoli ********
  // DELETE
  deleteArt = (codArt: string): void => {
    this.resetResponses();

    this.codArt = codArt;
    this.articoliService.deleteArticleByCodart(codArt).subscribe({
      next: this.handleSuccessResp,
      error: this.handleErrorResp
    });
  }

  private handleSuccessResp = (resp: any): void => {
    console.log("handleSuccessResp()");
    console.log(resp);
    console.log("this.codArt -> " + this.codArt);

    this.respObj$ = resp;

    this.filterType = FilterTypes.ByDesc;
    this.getArticoli();

    this.scrollToSuccessAlert();
  }
  private handleErrorResp = (error: any): void => {
    console.log("handleErrorResp()");
    console.log(error);

    this.error$ = error.error;
    if (error.status === StatusCodes.ServerUnavailable) {
      console.log(ErrorMessages.ServerNonDisponibile);

      this.error$.code = 0;
      this.error$.message = ErrorMessages.ServerNonDisponibile;

      console.log("this.error$.code -> " + this.error$.code);
      console.log('this.error$.message -> ' + this.error$.message);
    } else if (error.status === StatusCodes.NotFound){
      console.log(ErrorMessages.ElementoNonTrovato);
    } else if (error.status === StatusCodes.Forbidden){
      console.log(ErrorMessages.OperazioneNonConsentita);
    } else {
      console.log(ErrorMessages.ErroreGenerico);
      console.error(error); // Registra l'errore nella console
    }

    console.log("handleErrorResp()>this.error$.message -> " + this.error$.message);
    this.scrollToErrorAlert();
  }
  // ******** End - CRUD Articoli ********

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

  // ----- PAGINATION -----
  // pageNr -> numeroPagina passato dalla pagina HTML al metodo
  // this.pagination.pagButtonsNr -> Attuale numero di bottoniPagina visualizzato
  // this.pagination$.currentPage -> numeroPagina corrente
  // this.pagination$.totalPages -> totale delle pagine messe a disposizione dal server

  // Invocato al click del singolo bottone-pagina
  pageChange = (currentPage: string|number): void => {
    console.log("currentPage -> " + currentPage);
    this.pagination$.currentPage = typeof(currentPage)==='string' ? parseInt(currentPage) : currentPage;

    // Reimposta 'filterType' con FilterTypes.ByDesc così
    // 'this.getArticoli()' invoca 'getObservableByFilterType()'
    // che a sua volta invoca 'getArticoliByDesc()'.
    // Questo è l'unico dei 3 metodi GET che invia al server
    // i valori di 'pagination$' tramite url
    // (es. http://localhost:5051/api/articoli/cerca/descrizione/pane?currentPage=1&pageSize=10)
    this.filterType = FilterTypes.ByDesc;

    this.getArticoli();
  }

  // Ritorna un booleano per mostrare o meno il 'pageNr' su uno dei bottoniPagina
  shouldDisplayPageNr(pageNr: number): boolean {
    const firstPage = 1;
    const lastPage = this.pagination$.totalPages;
    const currentPage = this.pagination$.currentPage;
    const maxPagBtnsNr = this.pagination.maxPagBtnsNr; // 7
    const costantNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante
    const isFirstOrLastPage = (pageNr === firstPage || pageNr === lastPage);

    return (
            isFirstOrLastPage
            ||
            (pageNr >= 1 && pageNr <= firstPage+(maxPagBtnsNr-costantNrBtns))
              &&
            (currentPage >= firstPage && currentPage <= 4)
            ||
            (pageNr >= lastPage-(maxPagBtnsNr-costantNrBtns) && pageNr <= lastPage)
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
    const costantNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante

    const showEllipsis = (
                          (currentPage >= firstPage && currentPage <= 4)
                            &&
                          pageNr == firstPage+(maxPagBtnsNr-costantNrBtns)
                          ||
                          (currentPage >= lastPage-3 && currentPage <= lastPage)
                            &&
                          pageNr == lastPage-(maxPagBtnsNr-costantNrBtns)
                          ||
                          (currentPage >= firstPage+4 && currentPage <= lastPage-4)
                            &&
                          (pageNr == currentPage-2 || pageNr == currentPage+2)
                        );

    return showEllipsis ? "..." : pageNr.toString();
  }
// ----- PAGINATION - END -----
}
