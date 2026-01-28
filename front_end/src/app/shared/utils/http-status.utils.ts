/**
 * Checks whether the status code represents a “Server Error” (HTTP 5xx)
 * or a "Network Error" (status == 0).
 */
export function isNetworkOrServerError(status: number): boolean {
  return status === 0 || status >= 500;
}

export function isClientErrorStatus(status: number): boolean {
  return status >= 400 && status < 500;
}

export function isRedirectStatus(status: number): boolean {
  return status >= 300 && status < 400;
}

export function isRetryableStatus(status: number): boolean {
  return isNetworkOrServerError(status);
}

export function isUnauthorized(status: number): boolean {
  return status === 401;
}

export function isForbidden(status: number): boolean {
  return status === 403;
}

