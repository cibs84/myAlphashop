import { Component, Input, OnInit, Output } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';
import { EventEmitter } from '@angular/core';

@Component({
  selector: 'app-articolo-card',
  templateUrl: './articolo-card.component.html',
  styleUrls: ['./articolo-card.component.scss']
})
export class ArticoloCardComponent implements OnInit {

  @Input()
  articolo: Articolo = {
    codart: "",
    descrizione: "",
    um: "",
    pzcart: 0,
    peso: 0,
    prezzo: 0,
    active: true,
    data: new Date(),
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
    this.eventEdit.emit(this.articolo.codart);
  }
  deleteArticle = (): void => {
    this.eventDelete.emit(this.articolo.codart);
  }
}
