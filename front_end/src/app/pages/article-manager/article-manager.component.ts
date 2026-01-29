import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticleService } from 'src/app/shared/services/article.service';
import { StatusCodes } from 'src/app/shared/enums';
import { LoggingService } from 'src/app/core/services/logging.service';
import { BehaviorSubject, catchError, combineLatest, distinctUntilChanged, finalize, forkJoin, map, Observable, of, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { ArticleCreateRequest } from 'src/app/shared/models/ArticleCreateRequest';
import { ArticleResponse } from 'src/app/shared/models/ArticleResponse';
import { ArticleUpdateRequest } from 'src/app/shared/models/ArticleUpdateRequest';
import { NotificationService } from 'src/app/core/services/notification.service';
import { MESSAGE_KEYS } from 'src/app/core/i18n/message-keys';
import { ErrorViewModel } from 'src/app/core/errors/ErrorViewModel';
import { toErrorViewModel } from 'src/app/core/errors/to-error-view-model.fn';
import { TranslationService } from 'src/app/core/services/translation.service';
import { toMsgKey } from 'src/app/core/errors/to-msg-key.fn';
import { Location } from '@angular/common';
import { ArticleManagerState, ArticleManagerViewModel } from './ArticleManagerPage';
import { LoadingStateService } from 'src/app/core/services/loading-state.service';
import { ComponentCanDeactivate } from 'src/app/core/guards/pending-changes.guard';
import { ModalService } from 'src/app/core/services/modal.service';
import { FE_ERROR_MSGS, VALIDATION_CONFIGS } from './article-manager.config';

@Component({
  selector: 'app-article-manager',
  templateUrl: './article-manager.component.html',
  styleUrls: ['./article-manager.component.scss'],
})
export class ArticleManagerComponent implements OnInit, ComponentCanDeactivate {
  // -------------------------------------------------------------------------
  // CONSTANTS & CONFIGURATIONS
  // -------------------------------------------------------------------------
  readonly CREATE_MODE_TITLE = 'Create Article';
  readonly EDIT_MODE_TITLE = 'Edit Article';

  readonly VALIDATION_CONFIGS = VALIDATION_CONFIGS;
  readonly FE_ERROR_MSGS = FE_ERROR_MSGS;


  // -------------------------------------------------------------------------
  // FORM & STATE MANAGEMENT
  // -------------------------------------------------------------------------
  form!: UntypedFormGroup;

  private initialState: ArticleManagerState = {
    codart: '',
    barcodes: [],
    categories: [],
    vatList: [],
    errorVM: null
  }
  private stateSubject = new BehaviorSubject<ArticleManagerState>(this.initialState);


  // -------------------------------------------------------------------------
  // REACTIVE STREAMS (DATA LOGIC)
  // -------------------------------------------------------------------------
  private dataLoad$ = this.stateSubject.pipe(
    map(state => state.codart),
    distinctUntilChanged(), // Avoid duplicate API calls if 'codart' does not change
    switchMap((codart) => {
      return forkJoin({
        categories: this.articleService.getCategories(),
        vatList: this.articleService.getVatList(),
        article: codart ? this.articleService.getArticleByCodart(codart) : of(null)
      }).pipe(
        tap(({article, categories, vatList}) => {
          this.logger.log(`[ArticleManager] Fetch Data SUCCESS`);

          this.updateState({
            categories,
            vatList,
            barcodes: article ? article.barcodes : []
          });

          // If 'article' exists it's in EDIT MODE
          if (article) {
            this.patchForm(article);
          }
        }),
        map((result) => {
          return {
            data: result,
            error: null
          }
        }),
        catchError((error) => {
          this.logger.error(`[ArticleManager] Fetch Data FAILED`, error);

          const errorVM = toErrorViewModel(error, this.translator);
          if (error.status !== StatusCodes.NotFound) {
            this.showNotification(errorVM);
          }

          return of({
            data: null,
            error: error
          })
        })
      )
    }),
    // Emit 'null' immediately when a the loading starts.
    // It's used by ViewModel to show the loader before the API responds.
    startWith(null),
    // Share results to avoid multiple HTTP calls if multiple subscriptions (as async) exist
    shareReplay(1)
  );

  // VIEW MODEL: The single object consumed by the template via 'async' pipe.
  // It combines UI state, API data, and loading flags.
  viewModel$: Observable<ArticleManagerViewModel> = combineLatest({
    state: this.stateSubject.asObservable(),
    dataLoadResult: this.dataLoad$,
    isLocalLoading: this.loader.localLoading$,
    isGlobalLoading: this.loader.globalLoading$
  }).pipe(
    map(({state, dataLoadResult, isLocalLoading, isGlobalLoading}) => {

      const isRequestInProgress = dataLoadResult === null;
      const errorFromApi = dataLoadResult?.error
                            ? toErrorViewModel(dataLoadResult.error, this.translator)
                            : null;

      const isEditMode = !!state.codart;
      const title = isEditMode ? this.EDIT_MODE_TITLE : this.CREATE_MODE_TITLE;

      const showLoading = (isLocalLoading || isRequestInProgress) && !isGlobalLoading;

      return {
        ...state,
        isEditMode,
        title,
        showLoading,
        showData: !showLoading,
        errorVM: state.errorVM || errorFromApi
      }
    })
  )


  // -------------------------------------------------------------------------
  // CONSTRUCTOR & LIFECYCLE
  // -------------------------------------------------------------------------
  constructor(
    private route: ActivatedRoute,
    private articleService: ArticleService,
    private logger: LoggingService,
    private fb: UntypedFormBuilder,
    private notificationService: NotificationService,
    private router: Router,
    private translator: TranslationService,
    private location: Location,
    private loader: LoadingStateService,
    private modalService: ModalService
  ) {
    this.buildForm();
  }

  ngOnInit(): void {
    this.logger.log('[ArticleManager] ngOnInit() initialized');

    const codart = this.route.snapshot.paramMap.get('codart') ?? '';
    this.updateState({codart, errorVM: null});
  }


  // -------------------------------------------------------------------------
  // USER INTERACTION (PUBLIC METHODS)
  // -------------------------------------------------------------------------

  // Used by SAVE button
  onSubmit(): void {
    const state = this.stateSubject.value;
    const isEditMode = !!state.codart;
    this.logger.log(`[ArticleManager] SUBMIT requested | isEditMode: ${isEditMode}`);

    if (isEditMode && !this.form.dirty) {
      this.notificationService.setNotificationInfo(MESSAGE_KEYS.crud.noChangesDetected);
      return;
    }
    if (this.form.invalid || !this.form.dirty) {
      this.form.markAllAsTouched();
      return;
    }

    this.loader.setGlobal(true);
    const operation$ = isEditMode
                        ? this.articleService.updateArt(this.buildUpdateReqFromForm(), state.codart)
                        : this.articleService.createArt(this.buildCreateReqFromForm());

    operation$.pipe(
      finalize(() => this.loader.setGlobal(false))
    ).subscribe({
      next: (response) => {
        const msgKey = isEditMode
          ? MESSAGE_KEYS.crud.updateSuccess
          : MESSAGE_KEYS.crud.createSuccess;
        this.notificationService.setNotificationSuccess(msgKey);

        this.form.markAsPristine(); // Avoid 'PendingChangesGuard' blocking
        this.router.navigate(['articles/detail', response.codart]);
      },
      error: (error) => {
        this.handleError(error);
      },
    });

  };

  // Used by CANCEL button and 'status-info' component
  goBack(): void {
    // 'PendingChangesGuard' will decide whether
    // to go back immediately or show the modal window
    this.location.back();
  }

  // Used by DELETE button
  onDelete(): void {
    const codart = this.stateSubject.value.codart;
    if (!codart) return;

    this.logger.log(`[ArticleManager] Delete requested for: ${codart}`);

    this.modalService.open({
      title: 'Confirm Deletion',
      message: `Are you sure you want to permanently delete article <b>${codart}</b>?`,
      confirmText: 'Delete Article',
      type: 'danger'
    }).subscribe(confirmed => {
      this.logger.log(`[ArticleManager] Delete confirmation result: ${confirmed}`);
      if (confirmed) {
        this.deleteArt(codart);
      }
    });
  }


  // -------------------------------------------------------------------------
  // GUARDS (CAN DEACTIVATE)
  // -------------------------------------------------------------------------

  // Used by 'PendingChangesGuard'
  canDeactivate: () => boolean | Observable<boolean> = () => {
    this.logger.log('[ArticleManager] canDeactivate() | Checking canDeactivate. Form dirty:', this.form.dirty);
    // If the form is not "dirty" (unmodified), the user can exit without warnings.
    return !this.form.dirty;
  };

  // Used by 'PendingChangesGuard'
  openConfirmModal(): Observable<boolean> {
    this.logger.log('[ArticleManager] openConfirmModal() triggered by Guard');
    return this.modalService.open({
      title: 'Unsaved Changes',
      message: 'You have unsaved changes. Do you really want to leave?',
      confirmText: 'Leave',
      cancelText: 'Stay',
      type: 'warning'
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

    if (
      (!formControl.dirty && !formControl.touched) ||
      formControl.disabled
    ) {
      return '';
    }
    return formControl.valid && this.getBackendErrorsByField(field).length === 0
               ? 'is-valid' : 'is-invalid';
  }

  // Get Backend Errors by field without falsy elements such as:
  // "" (empty string), null, undefined, 0, false, NaN)
  getBackendErrorsByField(field: string): string[] {
    return (this.stateSubject.value.errorVM?.errorValidationMap?.[field] ?? []).filter(
      (code) => !!code
    )
    .map(code => this.translator.translate(toMsgKey(code)));
  }

  // -------------------------------------------------------------------------
  // PRIVATE LOGIC AND API OPERATIONS
  // -------------------------------------------------------------------------
  private deleteArt(codart: string): void {
    this.loader.setGlobal(true);

    this.articleService.deleteArticleByCodart(codart).subscribe({
      next: () => {
        this.logger.log(`[ArticleManager] onDelete() -> Success: ${codart} deleted`);

        this.updateState({ errorVM: null });

        this.loader.setGlobal(false);
        this.notificationService.setNotificationSuccess(MESSAGE_KEYS.crud.deleteSuccess);

        this.form.markAsPristine(); // Avoid 'PendingChangesGuard' blocking
        this.router.navigate(['/articles']);
      },
      error: (error) => {
        this.loader.setGlobal(false);
        this.handleError(error);
      }
    })
  }

  private buildForm(): void {
    this.form = this.fb.group({
      codart: [
        '',
        [
          Validators.required,
          Validators.pattern(this.VALIDATION_CONFIGS['codart']['pattern']),
          Validators.minLength(this.VALIDATION_CONFIGS['codart']['minlength']),
          Validators.maxLength(this.VALIDATION_CONFIGS['codart']['maxlength']),
        ],
      ],
      description: [
        '',
        [
          Validators.required,
          Validators.minLength(
            this.VALIDATION_CONFIGS['description']['minlength']
          ),
          Validators.maxLength(
            this.VALIDATION_CONFIGS['description']['maxlength']
          ),
        ],
      ],
      um: [''],
      pcsCart: [
        null,
        [
          Validators.min(this.VALIDATION_CONFIGS['pcsCart']['min']),
          Validators.max(this.VALIDATION_CONFIGS['pcsCart']['max']),
        ],
      ],
      netWeight: [
        null,
        [Validators.min(this.VALIDATION_CONFIGS['netWeight']['min'])],
      ],
      idArtStatus: [null, Validators.required],
      price: [
        0,
        [
          Validators.required,
          Validators.min(this.VALIDATION_CONFIGS['price']['min']),
        ],
      ],
      category: [null, [Validators.required]],
      vat: [null, [Validators.required]],
      barcodes: '',
      currency: ['EUR', [Validators.required]],
      imageUrl: [''],
      ingredients: [null],
    });
  }

  private patchForm(resArticle: ArticleResponse): void {
    this.logger.log('[ArticleManager] patchForm()', resArticle);

    // Populates the form fields with data received from the backend
    this.form.patchValue({
      ...resArticle,
      price: resArticle.price || 0,
      category: resArticle.category.id,
      vat: resArticle.vat.idVat,
      barcodes: resArticle.barcodes.map((b) => b.barcode).join(', '),
      ingredients: resArticle.ingredients?.info,
    });

    // Specific for Edit Mode
    this.form.get('codart')?.removeValidators(Validators.required);
    this.form.get('codart')?.updateValueAndValidity();
    this.form.get('codart')?.disable();
    this.form.get('barcodes')?.disable();
  }

  private handleError(error: any): void {
    this.logger.error('[ArticleManager] handleError()', error);

    // Transform error into ErrorViewModel using centralized logic
    const errorVM = toErrorViewModel(error, this.translator);

    // If the backend has sent specific validations (422 Unprocessable Entity), we save them
    if (error.status === StatusCodes.UnprocessableEntity && error.error?.errorValidationMap) {
      errorVM.errorValidationMap = error.error.errorValidationMap;
    }

    this.updateState({errorVM});

    this.showNotification(errorVM);
  }

  private showNotification(ErrorVM: ErrorViewModel): void {

    const msgKey = toMsgKey(ErrorVM.code!);

    switch (ErrorVM.status) {
      // Notify warning if Article to save already exists (409)
      case StatusCodes.Conflict:
        this.notificationService.setNotificationWarning(msgKey);
        break;

      // Do not notify if item not found (404)
      case StatusCodes.NotFound:
        break;

      default:
        this.notificationService.setNotificationError(msgKey);
        break;
    }
  }

  private buildCreateReqFromForm(): ArticleCreateRequest {
    const formValues = this.form.value;
    const state = this.stateSubject.value;

    return {
      ...formValues,
      category: formValues.category ? { id: formValues.category } : null,
      vat: formValues.vat !== null && formValues.vat !== undefined
        ? { idVat: formValues.vat }
        : null,
      barcodes: state.barcodes.length > 0 ? state.barcodes : null,
      ingredients: formValues.ingredients
        ? { codart: state.codart, info: formValues.ingredients }
        : null,
    };
  }

  private buildUpdateReqFromForm(): ArticleUpdateRequest {
    const {codart, ...rest} = this.buildCreateReqFromForm();

    // 'rest' is an ArticleCreateRequest without the 'codart' field
    return rest;
  }

  private updateState(partialState: Partial<ArticleManagerState>): void {
    const state = this.stateSubject.value;
    this.stateSubject.next({
      ...state,
      ...partialState
    })
  }
}
