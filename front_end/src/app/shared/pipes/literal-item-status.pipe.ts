import { Pipe, PipeTransform } from '@angular/core';
import { ItemStatus } from '../enums';

@Pipe({
  name: 'literalItemStatus'
})
export class LiteralItemStatusPipe implements PipeTransform {

  transform(numItemStatus: number | null): unknown {
    const mapArtStatus: Record<number, ItemStatus> = {
      1: ItemStatus.Active,
      2: ItemStatus.Suspended,
      3: ItemStatus.Deleted
    }
    if (numItemStatus == null) {
      return ItemStatus.None;
    }
    return mapArtStatus[numItemStatus] ?? ItemStatus.Error;
  }

}
