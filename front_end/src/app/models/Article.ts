export interface Article {
  codArt: string,
  description: string,
  um: string,
  codStat: string,
  pcsCart: number,
  netWeight: number,
  idArtStatus: string,
  price: number,
  active: boolean,
  creationDate: Date,
  urlImage: string,
  category: Category,
  vat: Vat,
  barcodes: Barcode[]
}

export interface Category {
  id: number;
  description: string;
 }

 export interface Vat {
  idVat: number;
  description: string;
  taxRate: number;
 }

 export interface Barcode {
  barcode: string;
  type: string;
 }
