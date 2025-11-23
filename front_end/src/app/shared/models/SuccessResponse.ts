import { PaginatedResponseList } from "./PaginatedResponseList";

export interface SuccessResponse<T> {
  pagResponseList?: PaginatedResponseList<T>;
  item?: T;
  code: number;
  message: string;
}
