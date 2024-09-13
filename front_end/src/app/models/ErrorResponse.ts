import { ErrorValidationMap } from "../shared/Types";

export interface ErrorResponse {
  date: Date;
	code: number;
	message: string;
	errorValidationMap?: ErrorValidationMap;
}
