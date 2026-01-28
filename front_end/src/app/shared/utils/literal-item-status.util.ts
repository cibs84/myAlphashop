import { ItemStatus } from "../enums";

export function literalItemStatus(numItemStatus: number | null): string {
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
