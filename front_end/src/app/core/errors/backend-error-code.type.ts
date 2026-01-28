import { BACKEND_ERROR_MAP } from "./backend-error-map";

export type BackendErrorCode = keyof typeof BACKEND_ERROR_MAP;
