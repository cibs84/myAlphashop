import { Article } from "./Article";
import { Pagination } from "./Pagination";

export interface ArticleResponse extends Pagination {

  pagination: Pagination;
  itemList: Article[];
}
