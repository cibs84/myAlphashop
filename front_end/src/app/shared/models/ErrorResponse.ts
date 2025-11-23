import { ErrorValidationMap } from "src/app/shared/types/ErrorValidationMap";

export interface ErrorResponse {
  date: Date;
	status: number;
	message: string;
	errorValidationMap?: ErrorValidationMap;
}
