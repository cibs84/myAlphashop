import { Component, OnInit } from '@angular/core';
import { Articolo } from 'src/app/models/Articolo';
import { ArticoliService } from 'src/app/services/articoli.service';

@Component({
  selector: 'app-articoli',
  templateUrl: './articoli.component.html',
  styleUrls: ['./articoli.component.scss']
})
export class ArticoliComponent implements OnInit {

  articoli$: Articolo[] = [];

  constructor(private articoliService: ArticoliService) { }

  ngOnInit(): void {
    this.articoli$ = this.articoliService.getArticoli();
  }

}
