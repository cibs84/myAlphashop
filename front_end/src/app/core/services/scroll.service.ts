import { ViewportScroller } from "@angular/common";
import { LoggingService } from "./logging.service";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ScrollService {

  constructor(private scroller: ViewportScroller) { }

  scrollToAnchor(anchor: string): void {
    // This delay ensures the element is ready for interaction before scrolling.
    setTimeout(() => {
      this.scroller.scrollToAnchor(anchor);
    }, 100);
  }
}
