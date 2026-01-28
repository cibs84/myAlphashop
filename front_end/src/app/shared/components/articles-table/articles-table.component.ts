import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { ArticleResponse } from '../../models/ArticleResponse';
import { MESSAGE_KEYS } from 'src/app/core/i18n/message-keys';
import { MsgKey } from 'src/app/core/i18n/msg-key.type';

@Component({
  selector: 'app-articles-table',
  templateUrl: './articles-table.component.html',
  styleUrls: ['./articles-table.component.scss']
})
export class ArticlesTableComponent implements OnInit {
  @Input() data!: ArticleResponse[];
  @Output() edit = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>();
  @Output() detail = new EventEmitter<string>();

  readonly NO_RESULT_MSG_KEY: MsgKey = MESSAGE_KEYS.crud.resourceNotFound;

  constructor() {}

  ngOnInit(): void {
  }

  setSelectedArticle(article: ArticleResponse){
    this.delete.emit(article.codart);
  }

  goToEdit(codart: string){
    this.edit.emit(codart);
  }

  goToDetail(codart: string){
    this.detail.emit(codart);
  }
}
