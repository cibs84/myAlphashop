import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {

    console.log("Selected article " + this.route.snapshot.params['codart']);
  }

}
