import { ErrorViewModel } from "src/app/core/errors/ErrorViewModel";

export interface BasePageState {
  errorVM: ErrorViewModel | null
}

export interface BasePageUIState {
  showLoading: boolean,
  showData: boolean
}
