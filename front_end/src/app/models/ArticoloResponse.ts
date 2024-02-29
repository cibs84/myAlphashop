import { Articolo } from "./Articolo";
import { Pagination } from "./Pagination";

export interface ArticoloResponse extends Pagination {

  pagination: Pagination;
  itemList: Articolo[];
}
