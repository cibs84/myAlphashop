import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

@Component({
    selector: 'forbidden',
    templateUrl: './forbidden.component.html',
    styleUrls: ['./forbidden.component.scss'],
    standalone: false
})
export class ForbiddenComponent implements OnInit {

  constructor(private location: Location) { }

  ngOnInit(): void {
  }

  goBack = () => {
    this.location.back();
  }
}
