import { ErrorValidationMap } from "src/app/shared/types/ErrorValidationMap.type";

export interface ErrorViewModel {
  message: string;
  code?: string;
  status?: number;
  severity?: 'info' | 'warning' | 'error';
  retryable?: boolean;
  errorValidationMap?: ErrorValidationMap | null;
}
