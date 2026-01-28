import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { CardFieldsConfig } from '../../models/CardFieldConfig';
import { literalItemStatus } from '../../utils/literal-item-status.util';

@Component({
  selector: 'app-generic-card',
  templateUrl: './generic-card.component.html',
  styleUrls: ['./generic-card.component.scss'],
})
export class GenericCardComponent<T> implements OnInit {
  @Input() data!: T;
  @Input() fieldsConfig!: CardFieldsConfig<T>;
  @Output() edit = new EventEmitter();
  @Output() delete = new EventEmitter();

  constructor() {}

  ngOnInit(): void {}

  editItem(){
    this.edit.emit(this.data[this.fieldsConfig.actionId]);
  }

  deleteItem(){
    this.delete.emit(this.data[this.fieldsConfig.actionId]);
  }

  isStatusValue(statusValue: number | null): boolean {
    if (!this.fieldsConfig.status || this.data[this.fieldsConfig.status] === null) {
      return true;
    }
    return Number(this.data[this.fieldsConfig.status]) === statusValue;
  }

  getItemStatus(status: any): string {
    return literalItemStatus(status);
  }
}
