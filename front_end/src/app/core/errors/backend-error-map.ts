import { MsgKey } from "../i18n/msg-key.type";
import { MESSAGE_KEYS } from "../i18n/message-keys";

/**
 * Mappa i codici di errore provenienti dal backend verso le chiavi di messaggi
 * del frontend (MESSAGE_KEYS).
 * - Validazioni DTO → codici ValidationErrorCode
 * - Errori generali → codici definiti lato backend
 */
export const BACKEND_ERROR_MAP: Record<string, MsgKey> = {
  // Validazioni
  VALIDATION_ERROR: MESSAGE_KEYS.validation.invalidForm,
  REQUIRED: MESSAGE_KEYS.validation.required,
  MIN_LENGTH: MESSAGE_KEYS.validation.minLength,
  MAX_lENGTH: MESSAGE_KEYS.validation.maxLength,
  SIZE_MIN: MESSAGE_KEYS.validation.sizeMin,
  INVALID_FORMAT: MESSAGE_KEYS.validation.invalidFormat,
  POSITIVE: MESSAGE_KEYS.validation.positive,
  POSITIVE_OR_ZERO: MESSAGE_KEYS.validation.positiveOrZero,
  MIN_MAX: MESSAGE_KEYS.validation.minMax,

  // Errori CRUD generici
  ITEM_NOT_FOUND: MESSAGE_KEYS.crud.resourceNotFound,
  NO_CHANGES_DETECTED: MESSAGE_KEYS.crud.noChangesDetected,
  CREATE_FAILED: MESSAGE_KEYS.crud.saveError,
  UPDATE_FAILED: MESSAGE_KEYS.crud.saveError,
  DELETE_FAILED: MESSAGE_KEYS.crud.deleteError,
  LOAD_FAILED: MESSAGE_KEYS.crud.loadError,
  ITEM_ALREADY_EXISTS: MESSAGE_KEYS.crud.itemAlreadyExists,

  // Errori Auth
  USER_ALREADY_EXISTS: MESSAGE_KEYS.auth.userAlreadyExists,
  USER_NOT_FOUND: MESSAGE_KEYS.auth.userNotFound,
  INVALID_CREDENTIALS: MESSAGE_KEYS.auth.invalidCredentials,
  SESSION_EXPIRED: MESSAGE_KEYS.auth.sessionExpired,
  UNAUTHORIZED: MESSAGE_KEYS.auth.unauthorized,
  FORBIDDEN: MESSAGE_KEYS.auth.forbidden,
  AUTHENTICATION_FAILED: MESSAGE_KEYS.auth.authenticationFailed,
  TOKEN_EXPIRED: MESSAGE_KEYS.auth.sessionExpired,
  INVALID_TOKEN: MESSAGE_KEYS.auth.authenticationFailed,

  // Errori di Rete
  SERVER_UNAVAILABLE: MESSAGE_KEYS.network.serverUnavailable,

  // Errori comuni
  GENERIC_ERROR: MESSAGE_KEYS.common.genericError,
  OPERATION_NOT_ALLOWED: MESSAGE_KEYS.common.operationNotAllowed
} as const;
