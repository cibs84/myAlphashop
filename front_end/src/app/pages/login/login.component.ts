import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, NgForm, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthappService } from '../../core/services/authapp.service';
import { LoggingService } from 'src/app/core/services/logging.service';
import { NotificationService } from 'src/app/core/services/notification.service';
import { MESSAGE_KEYS } from 'src/app/core/i18n/message-keys';
import { TranslationService } from 'src/app/core/services/translation.service';
import { LoginState, LoginViewModel } from './LoginPageModel';
import { BehaviorSubject, combineLatest, finalize, map, Observable, tap } from 'rxjs';
import { LoadingStateService } from 'src/app/core/services/loading-state.service';
import { toErrorViewModel } from 'src/app/core/errors/to-error-view-model.fn';
import { ERROR_MSGS } from '../article-manager/article-manager.config';
import { toMsgKey } from 'src/app/core/errors/to-msg-key.fn';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  // --------------------------------------------------------------
  // CONSTANTS & CONFIGURATIONS
  // --------------------------------------------------------------
  readonly TITLE: string = 'Login & Authentication';
  readonly SUBTITLE: string = 'Login or Sign Up';

  readonly VALIDATION_CONFIGS: Record<string, Record<string, any>> = {
    password: {
      minlength: 8,
    },
  };

  readonly FE_ERROR_MSGS: Record<string, Record<string, string>> = {
    username: {
      required: ERROR_MSGS.REQ_FIELD_MSG,
    },
    password: {
      required: ERROR_MSGS.REQ_FIELD_MSG,
      minlength: `Number of characters min ${this.VALIDATION_CONFIGS['password']['minlength']}`,
    },
  };


  // --------------------------------------------------------------
  // FORM & STATE MANAGEMENT
  // --------------------------------------------------------------
  form!: UntypedFormGroup;

  initialState: LoginState = {
    title: this.TITLE,
    subtitle: this.SUBTITLE,
    errorVM: null,
  };
  stateSubject = new BehaviorSubject<LoginState>(this.initialState);


  // -------------------------------------------------------------------------
  // REACTIVE STREAMS (DATA LOGIC)
  // -------------------------------------------------------------------------

  // VIEW MODEL: The single object consumed by the template via 'async' pipe.
  // It combines UI state, API data, and loading flags.
  viewModel$: Observable<LoginViewModel> = combineLatest({
    state: this.stateSubject.asObservable(),
    isGlobalLoading: this.loader.globalLoading$,
  }).pipe(
    map(({ state, isGlobalLoading }) => {
      return {
        ...state,
        showLoading: isGlobalLoading,
        showData: false,
      };
    }),
  );


  // --------------------------------------------------------------
  // CONSTRUCTOR & LIFECYCLE
  // --------------------------------------------------------------
  constructor(
    private route: Router,
    private authapp: AuthappService,
    private logger: LoggingService,
    private notificator: NotificationService,
    private translator: TranslationService,
    private fb: UntypedFormBuilder,
    private loader: LoadingStateService,
  ) {
    this.buildForm();
  }

  ngOnInit(): void {}


  // -------------------------------------------------------------------------
  // USER INTERACTION (PUBLIC METHODS)
  // -------------------------------------------------------------------------
  onSubmit(): void {
    this.logger.log('[LoginComponent] onSubmit()');

    // this.form.markAllAsTouched();
    if (this.form.invalid || this.loader.isGlobalLoading) {
      this.form.markAllAsTouched();
      return;
    }

    this.updateState({ errorVM: null });

    this.loader.setGlobal(true);

    const { username, password } = this.form.value;

    this.authapp
      .login(username, password)
      .pipe(finalize(() => this.loader.setGlobal(false)))
      .subscribe({
        next: (success) => {
          if (success) {
            this.logger.log(
              '[LoginComponent] ✅ Login riuscito!',
              'Navigate to Welcome page',
            );
            this.notificator.setNotificationSuccess(
              MESSAGE_KEYS.auth.loginSuccess,
            );
            this.route.navigate(['welcome']);
          } else {
            this.logger.log(
              '[LoginComponent] ❌ Login fallito durante il recupero delle info utente.',
            );
            this.handleError({ status: 401 });
          }
        },
        error: (error) => {
          this.logger.log('[LoginComponent] ❌ Login fallito', error);
          this.handleError(error);
        },
      });
  }


  // -------------------------------------------------------------------------
  // TEMPLATE HELPERS
  // -------------------------------------------------------------------------

  // Assign a CSS class to form fields
  // based on their validity and state (dirty/touched).
  getValidationClass(field: string): string {
    const formControl = this.form.get(field);

    if (!formControl) return '';

    if ((!formControl.dirty && !formControl.touched) || formControl.disabled) {
      return '';
    }
    return formControl.valid && this.getBackendErrorsByField(field).length === 0
      ? 'is-valid'
      : 'is-invalid';
  }

  // Get Backend Errors by field without falsy elements such as:
  // "" (empty string), null, undefined, 0, false, NaN)
  getBackendErrorsByField(field: string): string[] {
    return (this.stateSubject.value.errorVM?.errorValidationMap?.[field] ?? [])
      .filter((code) => !!code)
      .map((code) => this.translator.translate(toMsgKey(code)));
  }


  // -------------------------------------------------------------------------
  // PRIVATE LOGIC AND API OPERATIONS
  // -------------------------------------------------------------------------
  private buildForm(): void {
    this.form = this.fb.group({
      username: ['', [Validators.required]],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(
            this.VALIDATION_CONFIGS['password']['minlength'],
          ),
        ],
      ],
    });
  }

  private updateState(partialState: Partial<LoginState>): void {
    const state = this.stateSubject.value;
    this.stateSubject.next({
      ...state,
      ...partialState,
    });
  }

  private handleError(error: any) {
    this.form.reset();
    const errorVM = toErrorViewModel(error, this.translator);
    this.updateState({ errorVM });
  }
}
