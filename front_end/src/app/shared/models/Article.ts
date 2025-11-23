import { Barcode } from "./Barcode";
import { Category } from "./Category";
import { Vat } from "./Vat";

export interface Article {
  codArt: string,
  description: string,
  um?: string,
  codStat?: string,
  pcsCart?: number,
  netWeight?: number,
  idArtStatus?: string,
  price?: number,
  creationDate: Date,
  urlImage?: string,
  category?: Category | null,
  vat?: Vat | null,
  barcodes: Barcode[]
}
export { Category, Vat, Barcode };
