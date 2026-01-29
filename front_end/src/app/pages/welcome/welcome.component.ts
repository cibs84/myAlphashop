import { Component } from '@angular/core';
import { BehaviorSubject, catchError, combineLatest, map, Observable, of, Subject, tap } from 'rxjs';
import { LoadingStateService } from 'src/app/core/services/loading-state.service';
import { UserStateService } from 'src/app/core/services/user-state.service';
import { WelcomeState, WelcomeViewModel } from './WelcomePageModel';
import { UserInfoResponse } from 'src/app/shared/models/UserInfoResponse';
import { toErrorViewModel } from 'src/app/core/errors/to-error-view-model.fn';
import { TranslationService } from 'src/app/core/services/translation.service';
import { ErrorViewModel } from 'src/app/core/errors/ErrorViewModel';
import { Location } from '@angular/common';

@Component({
    selector: 'app-welcome',
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.scss'],
    standalone: false
})
export class WelcomeComponent {
  // -------------------------------------------------------------
  // CONSTANTS
  // -------------------------------------------------------------
  readonly TITLE: string = 'Welcome to Alphashop';
  readonly SUBTITLE: string = 'View deals of the day';


  // -------------------------------------------------------------
  // STATE MANAGEMENT
  // -------------------------------------------------------------
  private initialState: WelcomeState = {
    title: this.TITLE,
    subtitle: this.SUBTITLE,
    username: '',
    errorVM: null,
  };
  private stateSubject = new BehaviorSubject<WelcomeState>(this.initialState);


  // -------------------------------------------------------------------------
  // REACTIVE STREAMS (DATA LOGIC)
  // -------------------------------------------------------------------------
  private userInfo$: Observable<{
    data: UserInfoResponse | null;
    errorVM: ErrorViewModel | null;
  }> = this.userStateService.getUserInfo().pipe(
    map((userInfo) => ({ data: userInfo, errorVM: null })),
    catchError((error) => {
      return of({
        data: null,
        errorVM: toErrorViewModel(error, this.translator),
      });
    }),
  );

  // VIEW MODEL: The single object consumed by the template via 'async' pipe.
  // It combines UI state, API data, and loading flags.
  viewModel$: Observable<WelcomeViewModel> = combineLatest({
    state: this.stateSubject.asObservable(),
    showLoading: this.loader.localLoading$,
    userInfo: this.userInfo$,
  }).pipe(
    map(({ state, showLoading, userInfo }) => {
      const username = userInfo?.data?.username || state.username;
      const errorVM = userInfo?.errorVM || state.errorVM;

      return {
        ...state,
        username,
        showLoading,
        showData: !showLoading && !errorVM,
        errorVM
      };
    }),
  );


  // -------------------------------------------------------------
  // CONSTRUCTOR
  // -------------------------------------------------------------
  constructor(
    private userStateService: UserStateService,
    private loader: LoadingStateService,
    private translator: TranslationService,
    private location: Location
  ) {}


  // -------------------------------------------------------------------------
  // USER INTERACTION (PUBLIC METHODS)
  // -------------------------------------------------------------------------
  goBack() {
    this.location.back();
  }


  // -------------------------------------------------------------------------
  // PRIVATE LOGIC AND API OPERATIONS
  // -------------------------------------------------------------------------
  private updateState(partialState: Partial<WelcomeState>): void {
    const state = this.stateSubject.value;
    this.stateSubject.next({
      ...state,
      ...partialState,
    });
  }

}
