import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthappService } from 'src/app/core/services/authapp.service';
import { UserStateService } from 'src/app/core/services/user-state.service';

@Component({
  selector: 'header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  usernameLogged$: Observable<string | null> = this.userStateService.getUsernameLogged();
  isAuthenticated$: Observable<boolean> = this.authappService.isLogged();
  constructor(public authappService: AuthappService, private userStateService: UserStateService) { }

  ngOnInit(): void {
  }

}
