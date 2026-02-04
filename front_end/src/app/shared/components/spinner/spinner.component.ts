import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-spinner[isVisible]', // TODO in Angular 16+: Remove [isVisible] and migrate to @Input({required: true})
    templateUrl: './spinner.component.html',
    styleUrls: ['./spinner.component.scss'],
    standalone: false
})
export class SpinnerComponent {
  @Input() isVisible!: boolean;
  @Input() isGlobal: boolean = false;
}
