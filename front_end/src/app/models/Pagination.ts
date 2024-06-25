export class Pagination {

  private _currentPage: number = 1;
	private _totalPages: number = 1;
	private _nextPage: number = 1;
	private _previousPage: number = 1;
	private _pageSize: number = 10; // numero massimo di elementi presenti nella pagina
	private _totalElements: number = 10; // numero elementi presenti nella pagina corrente

  private _totalPagesArray: number[] = Array.of(this.totalPages); // usato con ngFor per creare i pulsanti di paginazione nel template ( deafult: un elemento/un pulsante)


  constructor(currentPage?: number, pageSize?: number) {
    this.currentPage = currentPage || this.currentPage;
    this.pageSize = pageSize || this.pageSize;
  }

  public set currentPage(currentPage: number){
    this._currentPage = currentPage > -1 ? currentPage : this.currentPage;
  }

  public get currentPage(){
    return this._currentPage;
  }

  public set pageSize(pageSize: number){
    this._pageSize = pageSize > 0 ? pageSize : this.pageSize;
  }

  public get pageSize(){
    return this._pageSize;
  }

  public set totalPages(totalPages: number){
    this._totalPages = totalPages > 0 ? totalPages : this.totalPages;
    this.totalPagesArray = Array.of(this.totalPages);
  }

  public get totalPages(){
    return this._totalPages;
  }

  public set nextPage(nextPage: number){
    this._totalPages = nextPage > 0 && nextPage <= this.totalPages
                       ? nextPage : this.nextPage;
  }

  public get nextPage(){
    return this._nextPage;
  }

  public set previousPage(previousPage: number){
    this._previousPage = previousPage > 0
                         ? previousPage : this.previousPage;
  }

  public get previousPage(){
    return this._previousPage;
  }

  public set totalElements(totalElements: number){
    this._totalElements = totalElements > 0 && totalElements <= this.pageSize
                          ? totalElements : this.totalElements;
  }

  public get totalElements(){
    return this._totalElements;
  }

  public set totalPagesArray(newTotalPagesArray: number[]){
    this._totalPagesArray = newTotalPagesArray;
  }

  public get totalPagesArray(): number[] {
    return this._totalPagesArray;
  }
}
