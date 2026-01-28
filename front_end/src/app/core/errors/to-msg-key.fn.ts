import { MsgKey } from "../i18n/msg-key.type";
import { BackendErrorCode } from "./backend-error-code.type";
import { BACKEND_ERROR_MAP } from "./backend-error-map";

/**
 * Converte un codice di errore proveniente dal backend in un MsgKey
 * utilizzabile dal servizio di translation.
 *
 * @param code - codice di errore proveniente dal backend (es. "REQUIRED", "ITEM_NOT_FOUND")
 * @returns MsgKey corrispondente (es. 'validation.required')
 */
export function toMsgKey(code: BackendErrorCode): MsgKey {
  return BACKEND_ERROR_MAP[code] ?? BACKEND_ERROR_MAP["GENERIC_ERROR"];
}
