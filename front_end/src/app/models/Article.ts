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
