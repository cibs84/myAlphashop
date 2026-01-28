import { Barcode } from "./Barcode";
import { Category } from "./Category";
import { Ingredients } from "./Ingredients";
import { Vat } from "./Vat";

export interface ArticleResponse {
  codart: string,
  description: string,
  um: string,
  codStat: string,
  pcsCart: number,
  netWeight: number,
  idArtStatus: number,
  price: number,
  creationDate: Date,
  category: Category,
  vat: Vat,
  barcodes: Barcode[],
  ingredients: Ingredients
}
