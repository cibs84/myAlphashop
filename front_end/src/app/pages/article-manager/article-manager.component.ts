import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Article, Barcode, Category, Vat } from 'src/app/models/Article';
import { ArticleService } from 'src/app/services/data/article.service';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  title: string = "Edit Article";

  codArt: string = "";

  article: Article = {
    codArt: "",
    description: "",
    um: "",
    codStat: "",
    pcsCart: 0,
    netWeight: 0,
    idArtStatus: "",
    price: 0,
    category: {id: 0, description: ""},
    vat: {idVat: 0, description: "", taxRate: 0},
    barcodes: [],
    active: true,
    creationDate: new Date(),
    urlImage: ""
  }

  categories: Category[] = [];
  vatList: Vat[] = [];



  constructor(private route: ActivatedRoute,
              private articleService: ArticleService) {

  }

  ngOnInit(): void {
    this.codArt = this.route.snapshot.params['codArt'];
    console.log("Selected article " + this.codArt);

    this.articleService.getArticleByCodart(this.codArt).subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    });

    this.articleService.getCategories().subscribe(
      response => {
        this.categories = response;
      }
    );

    this.articleService.getVatList().subscribe(
      response => {
        this.vatList = response;
      }
    );
  }

  handleResponse(response: any){
    this.article = response;

    console.log(this.article);
  };

  handleError(error: any){
    console.log(error);
  }
}
