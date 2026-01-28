import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Pagination } from '../../models/Pagination';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit {
  @Input() pagination!: Pagination;
  @Input() maxPagBtns!: number;
  @Output() pageChange = new EventEmitter<number>();

  constructor() { }

  ngOnInit(): void {
  }

  onPageBtnClick(pageNr: number){
    if (this.isCurrentPageNr(pageNr)) return;
    this.pageChange.emit(pageNr);
  }

  // Mostrare o meno il 'pageNr' su uno dei bottoni-pagina
  shouldDisplayPageNr(pageNr: number): boolean {
    const firstPage = 1;
    const lastPage = this.pagination.totalPages;
    const currentPage = this.pagination.currentPage;
    const maxTotalPagBtns = this.maxPagBtns; // Es. 7 => [previous][1][...][6][7][8][...][20][next] ('previous' e 'next' non sono conteggiati)
    const constNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante
    const isFirstOrLastPage = (pageNr === firstPage || pageNr === lastPage);

    return (
            isFirstOrLastPage
            ||
            (pageNr >= 1 && pageNr <= firstPage + (maxTotalPagBtns - constNrBtns))
              &&
            (currentPage >= firstPage && currentPage <= 4)
            ||
            (pageNr >= lastPage - (maxTotalPagBtns - constNrBtns) && pageNr <= lastPage)
              &&
            (currentPage >= lastPage-3 && currentPage <= lastPage)
            ||
            (currentPage >= firstPage+4 && currentPage <= lastPage-4)
              &&
            (pageNr >= currentPage-2 && pageNr <= currentPage+2)
           );
  }

  isCurrentPageNr(pageNr: number): boolean {
    return this.pagination.currentPage === pageNr;
  }

  // Stampa '...' o il numero di pagina ('pageNr') sul singolo bottone-pagina
  displayPageNr(pageNr: number): string {
    const firstPage = 1;
    const lastPage = this.pagination.totalPages;
    const currentPage = this.pagination.currentPage;
    const maxTotalPagBtns = this.maxPagBtns; // Es. 7 => [previous][1][...][6][7][8][...][20][next] ('previous' e 'next' non sono conteggiati)
    const constNrBtns = 2; // Il primo e l'ultimo bottone-pagina hanno valore costante

    const showEllipsis = (
                          (currentPage >= firstPage && currentPage <= 4)
                            &&
                          pageNr == firstPage+(maxTotalPagBtns-constNrBtns)
                          ||
                          (currentPage >= lastPage-3 && currentPage <= lastPage)
                            &&
                          pageNr == lastPage-(maxTotalPagBtns-constNrBtns)
                          ||
                          (currentPage >= firstPage+4 && currentPage <= lastPage-4)
                            &&
                          (pageNr == currentPage-2 || pageNr == currentPage+2)
                        );

    return showEllipsis ? "..." : pageNr.toString();
  }
}
