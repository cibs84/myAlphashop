import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../shared/components/header/header.component';
import { FooterComponent } from '../shared/components/footer/footer.component';
import { JumbotronComponent } from '../shared/components/jumbotron/jumbotron.component';
import { RouterModule } from '@angular/router';
import { SpinnerComponent } from '../shared/components/spinner/spinner.component';
import { ValidationMessagesComponent } from '../shared/components/validation-messages/validation-messages.component';
import { NotificationAreaComponent } from '../shared/components/notification-area/notification-area.component';

@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    JumbotronComponent,
    SpinnerComponent,
    ValidationMessagesComponent,
    NotificationAreaComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    JumbotronComponent,
    SpinnerComponent,
    ValidationMessagesComponent,
    NotificationAreaComponent
  ]
})
export class CoreModule { }
