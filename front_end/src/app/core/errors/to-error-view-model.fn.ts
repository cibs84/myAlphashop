import { ErrorViewModel } from './ErrorViewModel';
import { TranslationService } from '../services/translation.service';
import { toMsgKey } from './to-msg-key.fn';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from 'src/app/shared/models/ErrorResponse';
import { ErrorValidationMap } from 'src/app/shared/types/ErrorValidationMap.type';
import { MsgKey } from '../i18n/msg-key.type';

// error.error -> risposta JSON inviata dal backend di tipo ErrorResponse
//               (es. { status: 404, message: "Article not found" })
//
// error.status -> il codice HTTP reale ricevuto dal protocollo (es. 404, 500, 403)
export function toErrorViewModel(
  error: HttpErrorResponse,
  translator: TranslationService
): ErrorViewModel {

  const isNetworkError: boolean = error.status === 0;
  const errorResponse: ErrorResponse | null = (!isNetworkError && error.error) ? error.error : null;
  const backendCode: string = isNetworkError ? "SERVER_UNAVAILABLE" : (errorResponse?.code || "GENERIC_ERROR");
  const msgKey: MsgKey = toMsgKey(backendCode);
  const errorValidationMap: ErrorValidationMap | null = errorResponse?.errorValidationMap || null;

  return {
    message: translator.translate(msgKey),
    code: backendCode,
    status: errorResponse?.status ?? error.status,
    severity: severityMap(errorResponse?.status ?? error.status),
    retryable: isRetryable(errorResponse?.status ?? error.status),
    errorValidationMap: errorValidationMap
  };
}

/* ---------- Helpers ---------- */

function severityMap(status: number): "info" | "warning" | "error" | undefined {
  switch (status) {
    case 400:
    case 422:
      return 'warning';
    case 401:
    case 403:
      return 'error';
    case 404:
      return 'info';
    default:
      return 'error';
  }
}

function isRetryable(status: number): boolean {
  return status === 500 || status === 503
}
