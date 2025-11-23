import { Pagination } from "./Pagination";

export interface PaginatedResponseList<T> {

  pagination: Pagination;
  itemList: T[];
}
