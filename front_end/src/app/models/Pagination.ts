export class Pagination {

  private _currentPage: number = 1;
	private _totalPages: number = 1;
	private _nextPage: number = 1;
	private _previousPage: number = 1;
	private _pageSize: number = 10; // numero massimo di elementi presenti nella pagina
	private _totalElements: number = 10; // numero elementi presenti nella pagina corrente

  constructor(currentPage?: number|any, pageSize?: number|any) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
  }

  public set currentPage(currentPage: number){
    this._currentPage = typeof(currentPage)!=='undefined' && currentPage > -1 ? currentPage : this._currentPage;
  }

  public get currentPage(){
    return this._currentPage;
  }

  public set pageSize(pageSize: number){
    this._pageSize = typeof(pageSize)!=='undefined' && pageSize > 0 ? pageSize : this._pageSize;
  }

  public get pageSize(){
    return this._pageSize;
  }

  public set totalPages(totalPages: number){
    this._totalPages = typeof(totalPages)!=='undefined' && totalPages > 0 ? totalPages : this._totalPages;
  }

  public get totalPages(){
    return this._totalPages;
  }

  public set nextPage(nextPage: number){
    this._totalPages = typeof(nextPage)!=='undefined' && nextPage > 0 && nextPage <= this._totalPages
                       ? nextPage : this._nextPage;
  }

  public get nextPage(){
    return this._nextPage;
  }

  public set previousPage(previousPage: number){
    this._previousPage = typeof(previousPage)!=='undefined' && previousPage > 0
                         ? previousPage : this._previousPage;
  }

  public get previousPage(){
    return this._previousPage;
  }

  public set totalElements(totalElements: number){
    this._totalElements = typeof(totalElements)!=='undefined' && totalElements > 0 && totalElements <= this._pageSize
                          ? totalElements : this._totalElements;
  }

  public get totalElements(){
    return this._totalElements;
  }
}
