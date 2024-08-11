import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-gestart',
  templateUrl: './gestart.component.html',
  styleUrls: ['./gestart.component.scss']
})
export class GestartComponent implements OnInit {

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {

    console.log("Selezionato articolo " + this.route.snapshot.params['codart']);
  }

}
