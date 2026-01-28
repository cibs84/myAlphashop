import { Barcode } from "src/app/shared/models/Barcode";
import { BasePageState, BasePageUIState } from "src/app/shared/models/BasePageModel";
import { Category } from "src/app/shared/models/Category";
import { Vat } from "src/app/shared/models/Vat";

export interface ArticleManagerState extends BasePageState {
  codart: string,
  barcodes: Barcode[],
  categories: Category[],
  vatList: Vat[],
}

// The union of state data and UI flags
export interface ArticleManagerViewModel extends ArticleManagerState, BasePageUIState {
  title: string,
  isEditMode: boolean,
}
