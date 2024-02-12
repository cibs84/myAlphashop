import { Component, OnInit } from '@angular/core';
import { AuthappService } from '../services/authapp.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  constructor(private basicAuth: AuthappService) { }

  ngOnInit(): void {
    this.basicAuth.clearAll();
  }

}
