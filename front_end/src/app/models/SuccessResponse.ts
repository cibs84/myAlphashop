import { Article } from "./Article";
import { PaginatedResponseList } from "./PaginatedResponseList";

export interface SuccessResponse {
  pagResponseList?: PaginatedResponseList<Article>;
  article?: Article,
  code: number;
  message: string;
}
