import { ViewportScroller } from "@angular/common";

export function scrollToSuccessAlert(scroller: ViewportScroller): void {
  console.log("scrollToSuccessAlert()");

  // This delay ensures the element is ready for interaction before scrolling.
  setTimeout(() => {
    scroller.scrollToAnchor('successAlert');
  }, 100);
}

export function scrollToErrorAlert(scroller: ViewportScroller): void {
  console.log("scrollToErrorAlert()");

  // This delay ensures the element is ready for interaction before scrolling.
  setTimeout(() => {
    scroller.scrollToAnchor('errorAlert');
  }, 100);
}
