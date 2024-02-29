import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';

@Component({
  selector: 'app-articolo-card',
  templateUrl: './articolo-card.component.html',
  styleUrls: ['./articolo-card.component.scss']
})
export class ArticoloCardComponent implements OnInit {

  @Input()
  articolo: Articolo = {
    codArt: "",
    descrizione: "",
    um: "",
    codStat: "",
    pzCart: 0,
    pesoNetto: 0,
    idStatoArt: "",
    prezzo: 0,
    active: true,
    dataCreazione: new Date(),
    urlImage: ""
  }

  @Output()
  eventEdit = new EventEmitter();
  @Output()
  eventDelete = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  editArticle = (): void => {
    this.eventEdit.emit(this.articolo.codArt);
  }
  deleteArticle = (): void => {
    this.eventDelete.emit(this.articolo.codArt);
  }
}
