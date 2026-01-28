import { ArticleResponse } from "src/app/shared/models/ArticleResponse";
import { BasePageState, BasePageUIState } from "src/app/shared/models/BasePageModel";

export interface ArticleDetailState extends BasePageState {
  article: ArticleResponse | null,
  codart: string
}

// The union of state data and UI flags
export interface ArticleDetailViewModel extends ArticleDetailState, BasePageUIState {
}
