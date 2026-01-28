import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { toMsgKey } from 'src/app/core/errors/to-msg-key.fn';
import { MESSAGE_KEYS } from 'src/app/core/i18n/message-keys';
import { LoggingService } from 'src/app/core/services/logging.service';
import { NotificationService } from 'src/app/core/services/notification.service';
import { TranslationService } from 'src/app/core/services/translation.service';
import { ArticleService } from 'src/app/shared/services/article.service';
import { Location } from '@angular/common';
import { toErrorViewModel } from 'src/app/core/errors/to-error-view-model.fn';
import { StatusCodes } from 'src/app/shared/enums';
import { BehaviorSubject, catchError, combineLatest, distinct, distinctUntilChanged, finalize, map, Observable, of, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { ArticleDetailState, ArticleDetailViewModel } from './ArticleDetailPage';
import { LoadingStateService } from 'src/app/core/services/loading-state.service';
import { ErrorViewModel } from 'src/app/core/errors/ErrorViewModel';
import { ModalService } from 'src/app/core/services/modal.service';

@Component({
  selector: 'app-article-detail',
  templateUrl: './article-detail.component.html',
  styleUrls: ['./article-detail.component.scss'],
})
export class ArticleDetailComponent implements OnInit {
  // *** STATE MANAGEMENT ***
  private initialState: ArticleDetailState = {
    codart: '',
    article: null,
    errorVM: null,
  };
  // Every UI change or search parameter update
  // flows through this Subject.
  private stateSubject = new BehaviorSubject<ArticleDetailState>(
    this.initialState,
  );

  private articleData$: Observable<any> = this.stateSubject.pipe(
    map((state) => state.codart),
    distinctUntilChanged(), // Avoid duplicate API calls if 'codart' does not change
    switchMap((codart) => {
      if (!codart) return of(null);

      return this.articleService.getArticleByCodart(codart).pipe(
        tap((article) => {
          this.logger.log(
            `[ArticleDetail] Fetch Article SUCCESS | codart: ${codart}`,
            article,
          );
        }),
        map((article) => {
          return {
            article,
            error: null,
          };
        }),
        catchError((error) => {
          this.logger.error(
            `[ArticleDetail] Fetch Article FAILED | codart: ${codart}`,
            error,
          );

          const errorVM = toErrorViewModel(error, this.translator);
          if (errorVM.status !== StatusCodes.NotFound && error.status !== 0) {
            this.showNotification(errorVM);
          }

          return of({
            article: null,
            error,
          });
        }),
      );
    }),
    // Emit 'null' immediately when a the loading starts.
    // It's used by ViewModel to show the loader before the API responds.
    startWith(null),
    // Share results to avoid multiple HTTP calls if multiple subscriptions (as async) exist
    shareReplay(1),
  );

  // VIEW MODEL
  // The single object consumed by the template via 'async' pipe.
  // It combines UI state, API data, and loading flags.
  viewModel$: Observable<ArticleDetailViewModel> = combineLatest({
    state: this.stateSubject.asObservable(),
    articleResult: this.articleData$,
    isLocalLoading: this.loader.localLoading$,
    isGlobalLoading: this.loader.globalLoading$,
  }).pipe(
    map(({ state, articleResult, isLocalLoading, isGlobalLoading }) => {
      const isRequestInProgress = articleResult === null;
      const article = articleResult?.article || null;
      const errorFromApi = articleResult?.error
        ? toErrorViewModel(articleResult.error, this.translator)
        : null;

      return {
        ...state,
        article,
        showLoading: (isLocalLoading || isRequestInProgress) && !isGlobalLoading,
        showData: article,
        // Priority to state errors (like from deleteArt) over API fetch errors
        errorVM: state.errorVM || errorFromApi
      };
    }),
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private logger: LoggingService,
    private articleService: ArticleService,
    private notificator: NotificationService,
    private translator: TranslationService,
    private location: Location,
    private loader: LoadingStateService,
    private modalService: ModalService
  ) {}

  ngOnInit(): void {
    this.logger.log('[ArticleDetail] Initializing component');

    const codart = this.route.snapshot.paramMap.get('codart') ?? '';

    this.logger.log(
      `[ArticleDetail] Route parameter 'codart' found: ${codart}`,
    );
    this.updateState({ codart, errorVM: null });
  }

  goToEdit() {
    const codart = this.stateSubject.value.codart;

    this.logger.log(
      `[ArticleDetail] Navigation triggered: Edit Article | codart: ${codart}`,
    );

    this.router.navigate(['articles/manage', codart]);
  }

  onDelete(): void {
    const codart = this.stateSubject.value.codart;
    this.logger.log(`[ArticleDetail] Delete requested for: ${codart}`);

    this.modalService.open({
      title: 'Confirm Deletion',
      message: `Are you sure you want to permanently delete article <b>${codart}</b>?`,
      confirmText: 'Delete Article',
      type: 'danger'
    }).subscribe(confirmed => {
      this.logger.log(`[ArticleDetail] Delete confirmation result: ${confirmed}`);
      if (confirmed) {
        this.deleteArt(codart);
      }
    })
  }

  private deleteArt(codart: string) {
    this.loader.setGlobal(true);

    this.articleService.deleteArticleByCodart(codart)
      .pipe(
        finalize(() => {
          this.loader.setGlobal(false);
        }),
      )
      .subscribe({
        next: () => {
          this.logger.log(`[ArticleDetail] DELETE success | codart: ${codart}`);
          this.notificator.setNotificationSuccess(
            MESSAGE_KEYS.crud.deleteSuccess,
          );
          this.router.navigate(['articles']);
        },
        error: (error) => {
          this.logger.error(
            `[ArticleDetail] DELETE failed | codart: ${codart}`,
            error,
          );

          const errorVM = toErrorViewModel(error, this.translator);
          this.updateState({ errorVM });

          if (error.status !== 403) {
            this.showNotification(errorVM);
          }
        },
      });
  }

  goBack = () => {
    this.location.back();
  };

  private showNotification(errorVM: ErrorViewModel): void {
    const msgKey = toMsgKey(errorVM.code!);
    this.notificator.setNotificationError(msgKey);
  }

  updateState(partialState: Partial<ArticleDetailState>) {
    this.stateSubject.next({
      ...this.stateSubject.value,
      ...partialState,
    });
  }
}
