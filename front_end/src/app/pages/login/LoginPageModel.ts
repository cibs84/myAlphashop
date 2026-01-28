import { BasePageState, BasePageUIState } from "src/app/shared/models/BasePageModel";

export interface LoginState extends BasePageState {
  title: string,
  subtitle: string
}

// The union of state data and UI flags
export interface LoginViewModel extends LoginState, BasePageUIState {
}
