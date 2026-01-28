export const MESSAGE_KEYS = {
  common: {
    genericError: 'common.genericError',
    operationNotAllowed: 'common.operationNotAllowed',
    confirmDelete: 'common.confirmDelete',
    actionCancelled: 'common.actionCancelled'
  },

  network: {
    serverUnavailable: 'network.serverUnavailable',
    timeout: 'network.timeout',
    connectionError: 'network.connectionError',
    badRequest: 'network.badRequest',
    internalServerError: 'network.internalServerError'
  },

  auth: {
    invalidCredentials: 'auth.invalidCredentials',
    authenticationFailed: 'auth.authenticationFailed',
    loginSuccess: 'auth.loginSuccess',
    sessionExpired: 'auth.sessionExpired',
    unauthorized: 'auth.unauthorized',
    forbidden: 'auth.forbidden',
    userNotFound: 'auth.userNotFound',
    userAlreadyExists: 'auth.userAlreadyExists',
    registrationSuccess: 'auth.registrationSuccess',
    logoutSuccess: 'auth.logoutSuccess',
    accountDeleted: 'auth.accountDeleted'
  },

  crud: {
    createSuccess: 'crud.createSuccess',
    updateSuccess: 'crud.updateSuccess',
    deleteSuccess: 'crud.deleteSuccess',
    loadError: 'crud.loadError',
    saveError: 'crud.saveError',
    deleteError: 'crud.deleteError',
    resourceNotFound: 'crud.resourceNotFound',
    noChangesDetected: 'crud.noChangesDetected',
    itemAlreadyExists: 'crud.itemAlreadyExists'
  },

  validation: {
    invalidForm: 'validation.invalidForm',
    required: 'validation.required',
    invalidEmail: 'validation.invalidEmail',
    minLength: 'validation.minLength',
    maxLength: 'validation.maxLength',
    sizeMin: 'validation.sizeMin',
    passwordMismatch: 'validation.passwordMismatch',
    invalidFormat: 'validation.invalidFormat',
    positive: 'validation.positive',
    positiveOrZero: 'validation.positiveOrZero',
    minMax: 'validation.minMax',
    lettersAndNumbers: 'validation.lettersAndNumbers'
  }
} as const;
