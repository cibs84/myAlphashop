import { BasePageState, BasePageUIState } from "src/app/shared/models/BasePageModel";

export interface WelcomeState extends BasePageState {
  title: string,
  subtitle: string,
  username: string
}

// WelcomeViewModel: The union of state data and UI flags
export interface WelcomeViewModel extends WelcomeState, BasePageUIState {}
