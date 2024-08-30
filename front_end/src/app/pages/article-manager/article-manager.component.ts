import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Article, Category, Vat } from 'src/app/models/Article';
import { ArticleService } from 'src/app/services/data/article.service';
import { ErrorMessages } from 'src/app/enums/Enums';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss']
})
export class ArticleManagerComponent implements OnInit {

  title: string = "Edit Article";

  codArt: string = "";

  errorMessage: string = "";
  successMessage: string = "";
  respStatusCode: number = -1;

  article: Article = {
    codArt: "",
    description: "",
    um: "",
    codStat: "",
    pcsCart: 0,
    netWeight: 0,
    idArtStatus: "",
    price: 0,
    active: true,
    creationDate: new Date(),
    urlImage: "",
    category: {id: 0, description: ""},
    vat: {idVat: 0, description: "", taxRate: 0},
    barcodes: [{barcode: "", type: ""}],
  }

  categories: Category[] = [];
  vatList: Vat[] = [];

  isOnInit: boolean = true;

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
        this.categories = response.body  as Category[];
      }
    );

    this.articleService.getVatList().subscribe(
      response => {
        this.vatList = response.body as Vat[];
      }
    );
  }

  handleResponse(response: any){
    console.log("handleResponse()");
    console.log(response);

    this.respStatusCode = response.status;
    this.article = response.body;

    if (this.article.barcodes.length == 0) {
      this.article.barcodes = [{barcode: "", type: ""}];
    }
  };

  handleError(error: any){
    console.log("handleError()");
    console.log(error);

    this.errorMessage = error.error.message || ErrorMessages.UnavailableServer;
    this.respStatusCode = error.status;
  }

  saveArt = () => {
    console.log("saveArt()");

    this.errorMessage = "";
    this.successMessage = "";

    this.articleService.artUpdate(this.article).subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    });

    this.successMessage = "Edit article successfully executed!";
  }
}
