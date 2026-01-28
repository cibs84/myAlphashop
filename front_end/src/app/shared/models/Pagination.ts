export class Pagination {
  currentPage: number = 1;
  totalPages: number = 1;
  pageSize: number = 10; // maximum number of elements on the page
  totalElements: number = 0; // number of all elements on the response

  constructor(init?: Partial<Pagination>) {
    Object.assign(this, init);
  }

  // Next page number, capped at totalPages
  public get nextPage(): number {
    return Math.min(this.currentPage + 1, this.totalPages);
  }

  // Previous page number, capped at 1
  public get previousPage(): number {
    return Math.max(this.currentPage - 1, 1);
  }

  // Used with ngFor to create the pagination buttons in the template
  // Get array of page numbers [1,2,3,...totalPages]
  public get pageNrArray(): number[] {
    return Array.from({length: this.totalPages}, ((_, i) => i + 1));
  }

  // True if pagination has more than 1 page
  public get hasPages(): boolean {
    return this.totalPages > 1;
  }
}
