import { Injectable } from '@angular/core';
import { Articolo } from '../models/Articolo';

@Injectable({
  providedIn: 'root'
})
export class ArticoliService {

  articoli: Articolo[] = [
    {codart : '014600301', descrizione : 'BARILLA FARINA 1 KG', um : 'PZ', pzcart : 24, peso : 1, prezzo : 1.09, active : true, data : new Date(), urlImage : "assets/img/articles/farina_barilla_1kg.png"},
    {codart : "013500121", descrizione : "BARILLA PASTA GR.500 N.70 1/2 PENNE", um : "PZ", pzcart : 30, peso : 0.5, prezzo : 1.3, active : true, data : new Date(), urlImage : "assets/img/articles/barilla_mezze_penne_1kg.jpeg"},
    {codart : "007686402", descrizione : "FINDUS FIOR DI NASELLO 300 GR", um : "PZ", pzcart : 8, peso : 0.3, prezzo : 6.46, active : true, data : new Date(), urlImage : "assets/img/articles/findus_fior_di_nasello_300gr.jpeg"},
    {codart : "057549001", descrizione : "FINDUS CROCCOLE 400 GR", um : "PZ", pzcart : 12, peso : 0.4, prezzo : 5.97, active : true, data : new Date(), urlImage : "assets/img/articles/findus_croccole_400gr.jpeg"}
  ];

  constructor() { }

  getArticoli = (): Articolo[] => this.articoli;

  getArticoloByCodart = (codart: string): Articolo => {
    let index: number = this.articoli.findIndex(articolo => articolo.codart === codart);
    return this.articoli[index];
  };
}
