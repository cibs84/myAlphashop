import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-grid-articles',
  templateUrl: './grid-articles.component.html',
  styleUrls: ['./grid-articles.component.scss']
})
export class GridArticlesComponent implements OnInit {

  public numberIterates: number[] = [10, 4, 5, 6, 7, 8, 34, 65, 76, 23];

  constructor() { }

  ngOnInit(): void {
  }

}
