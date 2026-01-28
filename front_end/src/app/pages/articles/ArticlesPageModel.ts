import { ArticleResponse } from "src/app/shared/models/ArticleResponse";
import { BasePageState, BasePageUIState } from "src/app/shared/models/BasePageModel";
import { Pagination } from "src/app/shared/models/Pagination";

export enum FilterTypes {
  Codart = 1,
  Description = 2,
  Barcode = 3
}

export interface ArticlesState extends BasePageState {
  articles: ArticleResponse[],
  pagination: Pagination,
  viewMode: 'table' | 'grid',
  filter: string,
  filterType: FilterTypes,
  isMutating: boolean
}

// The union of state data and UI flags
export interface ArticlesViewModel extends ArticlesState, BasePageUIState {
  showPagination: boolean
}
