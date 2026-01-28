import { TranslationMap } from "./translation-map.type";

export const EN: TranslationMap = {
  common: {
    genericError: "An error occurred, please try again later",
    operationNotAllowed: "Operation not allowed",
    confirmDelete: "Do you confirm deletion?",
    actionCancelled: "Operation cancelled"
  },
  network: {
    serverUnavailable: "Service temporarily unavailable",
    timeout: "Request timeout",
    connectionError: "Connection error",
    badRequest: "Bad request",
    internalServerError: "Internal server error"
  },
  auth: {
    invalidCredentials: "Invalid credentials",
    authenticationFailed: "Authentication failed",
    loginSuccess: "Login successful",
    sessionExpired: "Session expired, please log in again",
    unauthorized: "Unauthorized access",
    forbidden: "You do not have permission to perform this action",
    userNotFound: "User not found",
    userAlreadyExists: "User already exists",
    registrationSuccess: "Registration completed successfully",
    logoutSuccess: "Logout successful",
    accountDeleted: "Account successfully deleted"
  },
  crud: {
    createSuccess: "Created successfully",
    updateSuccess: "Updated successfully",
    deleteSuccess: "Deleted successfully",
    loadError: "Error loading data",
    saveError: "Error saving data",
    deleteError: "Error deleting item",
    resourceNotFound: "Resource not found",
    noChangesDetected: "No changes detected",
    itemAlreadyExists: "An item with these details already exists in the system"
  },
  validation: {
    invalidForm: "Invalid data. Please check the form.",
    required: "Field is required",
    invalidEmail: "Invalid email address",
    minLength: "Minimum length not met",
    maxLength: "Maximum length exceeded",
    sizeMin: "To proceed, please ensure that the '{0}' field contains at least {min} elements/characters.",
    passwordMismatch: "Passwords do not match",
    invalidFormat: "Invalid format",
    positive: "Number must be greater than 0",
    positiveOrZero: "Negative number not allowed",
    minMax: "Number is out of allowed range",
    lettersAndNumbers: "Only letters and numbers allowed"
  }
};
