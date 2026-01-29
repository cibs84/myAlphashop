import { Component, OnInit} from '@angular/core';
import { BehaviorSubject, catchError, combineLatest, distinctUntilChanged, EMPTY, filter, finalize, map, mergeMap, Observable, of, shareReplay, startWith, Subject, switchMap, take, takeUntil, tap, throwError } from 'rxjs';
import { Pagination } from 'src/app/shared/models/Pagination';
import { ArticleService } from 'src/app/shared/services/article.service';
import { Router } from '@angular/router';
import { LoggingService } from 'src/app/core/services/logging.service';
import { ArticleResponse } from 'src/app/shared/models/ArticleResponse';
import { CardFieldsConfig } from 'src/app/shared/models/CardFieldConfig';
import { PaginatedResponseList } from 'src/app/shared/models/PaginatedResponseList';
import { NotificationService } from 'src/app/core/services/notification.service';
import { MESSAGE_KEYS } from 'src/app/core/i18n/message-keys';
import { MsgKey } from 'src/app/core/i18n/msg-key.type';
import { toMsgKey } from 'src/app/core/errors/to-msg-key.fn';
import { toErrorViewModel } from 'src/app/core/errors/to-error-view-model.fn';
import { TranslationService } from 'src/app/core/services/translation.service';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { LoadingStateService } from 'src/app/core/services/loading-state.service';
import { ArticlesState, ArticlesViewModel, FilterTypes } from './ArticlesPageModel';
import { strSanitize } from 'src/app/shared/utils/string.utils';
import { ModalService } from 'src/app/core/services/modal.service';

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss'],
})
export class ArticlesComponent implements OnInit {
  // -------------------------------------------------------------------------
  // CONSTANTS & CONFIGURATIONS
  // -------------------------------------------------------------------------
  readonly NO_RESULT_MSG_KEY: MsgKey = MESSAGE_KEYS.crud.resourceNotFound;

  // Configuration for input field validations
  private readonly VALIDATION_CONFIGS: Record<string, Record<string, any>> = {
    codart: {
      pattern: '[a-zA-Z0-9]+',
      minlength: 5,
      maxlength: 20,
    },
    description: {
      maxlength: 80,
    },
  };

  // FRONT END ERROR MESSAGES - VALIDATION
  readonly FE_ERROR_MSGS: Record<string, Record<string, string>> = {
    filter: {
      required: this.translator.translate(MESSAGE_KEYS.validation.required),
      maxlength: this.translator.translate(MESSAGE_KEYS.validation.maxLength),
      pattern: this.translator.translate(
        MESSAGE_KEYS.validation.lettersAndNumbers
      ),
    },
  };

  // PAGINATION
  readonly MAX_TOT_PAG_BTNS = 7;
  private readonly MINIMUM_TOT_PAG_BTNS = 5;

  // Filter types for the template dropdown
  readonly filterTypeList = [
    { key: 'Codart', value: FilterTypes.Codart },
    { key: 'Description', value: FilterTypes.Description },
    { key: 'Barcode', value: FilterTypes.Barcode },
  ];

  // Mapping of search strategies based on selected filter type
  private readonly searchStrategyMap: Record<
    FilterTypes,
    (
      filter: string,
      pagination: Pagination
    ) => Observable<ArticleResponse | PaginatedResponseList<ArticleResponse>>
  > = {
    [FilterTypes.Codart]: (filter: string) =>
      this.articleService.getArticleByCodart(filter),
    [FilterTypes.Description]: (filter: string) =>
      this.articleService.getArticlesByDesc(
        filter,
        this.stateSubject.value.pagination
      ),
    [FilterTypes.Barcode]: (filter: string) =>
      this.articleService.getArticleByBarcode(filter),
  };

  // Generic card component configuration
  readonly cardFieldsConfig: CardFieldsConfig<ArticleResponse> = {
    actionId: 'codart',
    title: 'description',
    text: 'codart',
    status: 'idArtStatus',
  };


  // -------------------------------------------------------------------------
  // FORM & STATE MANAGEMENT
  // -------------------------------------------------------------------------
  private initialState: ArticlesState = {
    articles: [],
    pagination: new Pagination(),
    viewMode: 'table',
    filter: '',
    filterType: FilterTypes.Description,
    isMutating: false,
    errorVM: null
  };
  // Every UI change or search parameter update flows through this Subject.
  private stateSubject = new BehaviorSubject<ArticlesState>(this.initialState);

  // FORM GROUP
  form!: UntypedFormGroup;


  // -------------------------------------------------------------------------
  // REACTIVE STREAMS (DATA LOGIC)
  // -------------------------------------------------------------------------

  // Controls when to trigger a new API call based on state changes
  private articleData$: Observable<any> = this.stateSubject.pipe(
    // Pass to switchMap when the condition is false
    distinctUntilChanged((prev, curr) => {
      // 1.Check if core search parameters have changed
      const sameSearchCriteria =
        prev.filter === curr.filter &&
        prev.filterType === curr.filterType &&
        prev.pagination.currentPage === curr.pagination.currentPage;

      // If search criteria are different (e.g. new page or new filter), always allow the call
      if (!sameSearchCriteria) {
        return false; // False = Make the call (go to switchMap)
      }

      // 2. If criteria are the same, determine if we should block the call (return true)

      // CASE: Mutation Error Reset (isMutationg transition true -> false)
      // Don't refresh the list if the mutation failed
      const isErrorReset = prev.isMutating === true && curr.isMutating === false;

      /// CASE: Stable State (isMutating transition false -> false)
      // Don't refresh if the user clicks "Search" without changing inputs
      const isAlreadyIdle = prev.isMutating === false && curr.isMutating === false;

      return isErrorReset || isAlreadyIdle;
    }),
    switchMap(state => {
      const searchObs = this.searchStrategyMap[state.filterType];
      return searchObs(state.filter, state.pagination).pipe(
        tap(() => {
          if (state.isMutating) {
            this.loader.setGlobal(false);
            this.notificationService.setNotificationSuccess(
              MESSAGE_KEYS.crud.deleteSuccess
            );
          }
        }),
        map(result => ({ data: result, error: null })),
        catchError(error => {
          this.logger.log('[Articles] Fetch data FAILED');
          return of({ data: null, error });
        })
      )
    }),
    // Emit 'null' immediately when a new search starts.
    // It's used by the ViewModel to show the loader before the API responds.
    startWith(null),
    // Share results to avoid multiple HTTP calls if multiple subscriptions (as async) exist
    shareReplay(1)
  );

  // VIEW MODEL: The single object consumed by the template via 'async' pipe.
  // It combines UI state, API data, and loading flags.
  viewModel$: Observable<ArticlesViewModel> = combineLatest({
    state: this.stateSubject.asObservable(),
    searchResult: this.articleData$,
    isLocalLoading: this.loader.localLoading$,
    isGlobalLoading: this.loader.globalLoading$,
  }).pipe(
    tap(({searchResult}) => {
      console.log("SEARCH_RESULT:", searchResult);
    }),
    map(({ state, searchResult, isLocalLoading, isGlobalLoading }) => {
        // If 'searchResult' is null, it means startWith(null) has fired in 'articleData$'.
        // but the server hasn't replied yet
        const isRequestInProgress = searchResult === null;
        const articleData = searchResult?.data;
        const error = searchResult?.error;

        // Based on the response, assign 'article list' OR 'single article'
        const articles = articleData
                          ? ('itemList' in articleData ? articleData.itemList : [articleData])
                          : [];

        const pagination = articleData && 'itemList' in articleData
                            ? new Pagination(articleData.pagination)
                            : new Pagination();

        const errorFromApi = error ? toErrorViewModel(error, this.translator) : null;

        // Show 'spinner' in template if the Interceptor/Service is active
        // OR if we are waiting for a new API response ('isRequestInProgress').
        const showLocalLoading = (isLocalLoading || isRequestInProgress) && !isGlobalLoading;

        return {
          ...state,
          articles,
          pagination,
          showLoading: showLocalLoading,
          showData: !showLocalLoading, // It also works with 404 status (resource not found).
          showPagination: pagination.totalPages > 1 && !errorFromApi,
          // Priority to state errors (like from deleteArt) over API fetch errors
          errorVM: state.errorVM || errorFromApi
        }
      })
    );


  // -------------------------------------------------------------------------
  // CONSTRUCTOR & LIFECYCLE
  // -------------------------------------------------------------------------
  constructor(
    private articleService: ArticleService,
    private router: Router,
    private logger: LoggingService,
    private notificationService: NotificationService,
    private translator: TranslationService,
    private fb: UntypedFormBuilder,
    private loader: LoadingStateService,
    private modalService: ModalService
  ) {
    // Initializing 'form'
    this.form = this.fb.group({
      filter: [''],
      filterType: [FilterTypes.Description],
    });
  }

  ngOnInit(): void {
    this.logger.log('[Articles] ngOnInit() initialized');
  }


  // -------------------------------------------------------------------------
  // USER INTERACTION (PUBLIC METHODS)
  // -------------------------------------------------------------------------
  updateFilterValidators(type: FilterTypes) {
    const control = this.form.get('filter');
    if (!control) return;

    control.clearValidators();

    switch (type) {
      case FilterTypes.Description:
        control.setValidators([
          Validators.maxLength(
            this.VALIDATION_CONFIGS['description']['maxlength']
          ),
        ]);
        break;
      case FilterTypes.Codart:
        control.setValidators([
          Validators.required,
          Validators.pattern(this.VALIDATION_CONFIGS['codart']['pattern']),
          Validators.maxLength(this.VALIDATION_CONFIGS['codart']['maxlength']),
        ]);
        break;
      case FilterTypes.Barcode:
        control.setValidators([Validators.required]);
        break;
    }

    control.updateValueAndValidity();
  }

  onSubmit = (): void => {
    this.logger.log('[Articles] refresh() triggered');

    // If the form is invalid, mark as touched, clear the ViewModel, clear last search markers and stop
    if (this.form.get('filter')?.invalid) {
      this.logger.warn('[Articles] refresh() -> Form invalid, aborting search');

      this.form.get('filter')?.markAsTouched();
      return;
    }

    const formFilter = strSanitize(this.form.get('filter')?.value);
    const formFilterType = this.form.get('filterType')?.value;

    // Triggering a search simply means updating the State.
    this.updateState({
      filter: formFilter,
      filterType: formFilterType,
      pagination: new Pagination(),
      isMutating: false,
      errorVM: null
    });

    this.logger.log('Refresh executed.');
  };

  goToCreate() {
    this.logger.log('[Articles] Redirect to create page');
    this.router.navigate(['articles/manage/new']);
  }
  goToEdit(codart: string) {
    this.logger.log('[Articles] Redirect to edit page');
    this.router.navigate(['articles/manage', codart]);
  }
  goToDetail(codart: string) {
    this.logger.log('[Articles] Redirect to detail page');
    this.router.navigate(['articles/detail', codart]);
  }

  onDelete(codart: string) {
    this.logger.log(`[Articles] Delete requested for: ${codart}`);

    this.modalService.open({
      title: 'Confirm Deletion',
      message: `Are you sure you want to permanently delete article <b>${codart}</b>?`,
      confirmText: 'Delete Article',
      type: 'danger'
    }).subscribe(confirmed => {
      this.logger.log(`[Articles] Delete confirmation result: ${confirmed}`);
      if (confirmed) {
        this.deleteArt(codart);
      }
    })
  }

  // Invoked on click of a page button of the 'Pagination' component
  pageChange = (currentPage: number): void => {
    this.logger.log(`[Articles] pageChange() -> Moving to page ${currentPage}`);

    this.updateState({
      pagination: new Pagination({
        ...this.stateSubject.value.pagination,
        currentPage
      }),
    });
  };

  // Switch view mode between Table and Grid
  switchView() {
    const viewMode = this.stateSubject.value.viewMode == 'table' ? 'grid' : 'table'
    this.logger.log(`[Articles] switchView() -> ${viewMode}`);
    this.updateState({viewMode});
  }


  // -------------------------------------------------------------------------
  // TEMPLATE HELPERS
  // -------------------------------------------------------------------------

  // USED BY TEMPLATE: Assign a CSS class to form fields
  // based on their validity and state (dirty/touched).
  getValidationClass(field: string): string {
    const formControl = this.form.get(field);

    if (
      !formControl ||
      (!formControl.dirty && !formControl.touched) ||
      formControl.disabled
    ) {
      return '';
    }
    return formControl.valid ? 'is-valid' : 'is-invalid';
  }


  // -------------------------------------------------------------------------
  // PRIVATE LOGIC AND API OPERATIONS
  // -------------------------------------------------------------------------
  private deleteArt = (codart: string): void => {
    this.logger.log(`[Articles] deleteArt() -> Target: ${codart}`);

    this.loader.setGlobal(true);
    let isSuccess = false;

    this.articleService.deleteArticleByCodart(codart).pipe()
      .subscribe({
        next: () => {
          this.logger.log(`[Articles] deleteArt() -> Success: ${codart} deleted`);
          isSuccess = true;
          this.updateState({isMutating: true, errorVM: null});
        },
        error: (error) => {
          this.logger.error(`[Articles] deleteArt() -> Error deleting ${codart}`, error);

          this.loader.setGlobal(false);

          const errorVM = toErrorViewModel(error, this.translator);

          this.updateState({isMutating: false, errorVM});

          if (error.status !== 403) {
            this.notificationService.setNotificationError(toMsgKey(errorVM.code!));
          }
        }
      });
  };

  // UPDATE COMPONENT STATE
  private updateState(partialState: Partial<ArticlesState>): void {
    this.stateSubject.next({
      ...this.stateSubject.value,
      ...partialState,
    });
  }
}
