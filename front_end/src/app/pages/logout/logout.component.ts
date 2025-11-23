import { Component, OnInit } from '@angular/core';
import { AuthappService } from '../../core/services/authapp.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss']
})
export class LogoutComponent implements OnInit {

  private status = 'success';

  constructor(private authappService: AuthappService) { }

  ngOnInit(): void {
    this.authappService.logout().subscribe({
      next: () => this.status = 'success',
      error: (error) => {
        console.error("Errore in /logout", error);
        this.status = 'failed';
      }
    });
  }

}
