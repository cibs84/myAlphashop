import { Barcode } from "./Barcode";
import { CategoryRef } from "./CategoryRef";
import { Ingredients } from "./Ingredients";
import { VatRef } from "./VatRef";

export interface ArticleUpdateRequest {
  description?: string,
  um?: string,
  codStat?: string,
  pcsCart?: number,
  netWeight?: number,
  idArtStatus?: number,
  price?: number,
  // urlImage?: string,
  category?: CategoryRef | null,
  vat?: VatRef | null,
  barcodes?: Barcode[] | null,
  ingredients?: Ingredients | null
}
