import { Component, OnInit } from '@angular/core';
import { AuthappService } from 'src/app/services/authapp.service';

@Component({
  selector: 'header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(public basicAuth: AuthappService) { }

  ngOnInit(): void {
  }

}
