import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthappService } from 'src/app/core/services/authapp.service';
import { UserStateService } from 'src/app/core/services/user-state.service';

@Component({
    selector: 'header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss'],
    standalone: false
})
export class HeaderComponent implements OnInit {

  usernameLogged$: Observable<string | null> = this.userStateService.getUsernameLogged();
  isAuthenticated$: Observable<boolean> = this.authappService.isLogged();

  constructor(public authappService: AuthappService,
              private userStateService: UserStateService,
              private elementRef: ElementRef) { }

  ngOnInit(): void {
  }

  // Prendiamo il riferimento al div del menu e al pulsante
  @ViewChild('menu') menu!: ElementRef;
  @ViewChild('toggler') toggler!: ElementRef;
  @ViewChild('userMenu') userMenu!: ElementRef;
  @ViewChild('dwUserMenu') dwUserMenu!: ElementRef;

  // Chiude il menu a tendina aperto dal toggler
  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    const isMenuOpen = this.menu.nativeElement.classList.contains('show');

    if (!isMenuOpen) return;

    const clickedInside = this.elementRef.nativeElement.contains(event.target);

    const isLinkClick = event.target.tagName === 'A' || event.target.closest('a');
    const isTogglerClick = this.toggler.nativeElement.contains(event.target);
    const isUserMenuClick = this.userMenu.nativeElement.contains(event.target);
    const isDwUserMenuClick = this.dwUserMenu.nativeElement.contains(event.target);

    if (isDwUserMenuClick) {
      this.toggler.nativeElement.click();
      // Esco così non valuto la condizioni con !isUserMenuClick
      // che è in contrasto visto che 'isUserMenuClick' contiene 'isDwUserMenuClick'
      return;
    }
    if ((!clickedInside || isLinkClick) && !isTogglerClick && !isUserMenuClick) {
      this.toggler.nativeElement.click();
    }
  }

}
