import { Component, OnInit } from '@angular/core';
import { AuthappService } from '../../core/services/authapp.service';
import { LoggingService } from 'src/app/core/services/logging.service';

@Component({
    selector: 'app-logout',
    templateUrl: './logout.component.html',
    styleUrls: ['./logout.component.scss'],
    standalone: false
})
export class LogoutComponent implements OnInit {

  constructor(private authappService: AuthappService,
              private logger: LoggingService
  ) { }

  ngOnInit(): void {
    this.authappService.logout().subscribe({
      next: () => {
        this.logger.log("[LogoutComponent] Logout and redirect to /logout successfully completed")
      },
      error: (error) => {
        this.logger.error("[LogoutComponent] Errore in /logout", error);
      }
    });
  }

}
