import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Article } from 'src/app/models/Article';

@Component({
  selector: 'app-article-card',
  templateUrl: './article-card.component.html',
  styleUrls: ['./article-card.component.scss']
})
export class ArticleCardComponent implements OnInit {

  @Input()
  article: Article = {
    codArt: "",
    description: "",
    um: "",
    codStat: "",
    pcsCart: 0,
    netWeight: 0,
    idArtStatus: "",
    price: 0,
    active: true,
    creationDate: new Date(),
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
    this.eventEdit.emit(this.article.codArt);
  }
  deleteArticle = (): void => {
    this.eventDelete.emit(this.article.codArt);
  }
}
