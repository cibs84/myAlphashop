import { ErrorValidationMap } from "src/app/shared/types/ErrorValidationMap.type";

export interface ErrorResponse {
  date: Date,
	status: number,
	code: string,
	errorValidationMap?: ErrorValidationMap | null
}
