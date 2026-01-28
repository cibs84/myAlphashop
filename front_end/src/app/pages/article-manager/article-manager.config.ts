// -------------------------------------------------------------------------
// Generic Error Messages | TO DO: Replace with i18n messages
// -------------------------------------------------------------------------
export const ERROR_MSGS = {
  REQ_FIELD_MSG: 'Required field',
  ONLY_LETTERS_NUMBERS_MSG: 'The field can only contain letters and numbers',
  NO_NEGATIVE_NUMBER_MSG: 'Negative number not allowed',
  GREATER_THAN_ZERO_MSG: 'Must be a number greater than zero'
}

// -------------------------------------------------------------------------
// Helper Message Builders
// -------------------------------------------------------------------------
export const getMinMaxlengthMsg = (minlength: number, maxlength: number) =>
  `Number of characters min ${minlength} - max ${maxlength}`;

export const getMinMaxMsg = (min: number, max: number) =>
  `Min ${min} - Max ${max}`;

// -------------------------------------------------------------------------
// VALIDATION CONFIGS: used by Form Validators and FE_ERROR_MSGS
// -------------------------------------------------------------------------
export const VALIDATION_CONFIGS: Record<string, Record<string, any>> = {
  codart: {
    pattern: '[a-zA-Z0-9]+',
    minlength: 5,
    maxlength: 20,
  },
  description: {
    minlength: 6,
    maxlength: 80,
  },
  pcsCart: {
    min: 0,
    max: 100,
  },
  netWeight: {
    min: 0.01,
  },
  price: {
    min: 0,
  },
};

// -------------------------------------------------------------------------
// FRONTEND ERROR MESSAGES: used by 'validation-messages' component
// -------------------------------------------------------------------------
export const FE_ERROR_MSGS: Record<string, Record<string, string>> = {
  codart: {
    required: ERROR_MSGS.REQ_FIELD_MSG,
    pattern: ERROR_MSGS.ONLY_LETTERS_NUMBERS_MSG,
    minlength: getMinMaxlengthMsg(
      VALIDATION_CONFIGS['codart']['minlength'],
      VALIDATION_CONFIGS['codart']['maxlength']
    ),
    maxlength: getMinMaxlengthMsg(
      VALIDATION_CONFIGS['codart']['minlength'],
      VALIDATION_CONFIGS['codart']['maxlength']
    ),
  },
  description: {
    required: ERROR_MSGS.REQ_FIELD_MSG,
    minlength: getMinMaxlengthMsg(
      VALIDATION_CONFIGS['description']['minlength'],
      VALIDATION_CONFIGS['description']['maxlength']
    ),
  },
  pcsCart: {
    min: getMinMaxMsg(
      VALIDATION_CONFIGS['pcsCart']['min'],
      VALIDATION_CONFIGS['pcsCart']['max']
    ),
    max: getMinMaxMsg(
      VALIDATION_CONFIGS['pcsCart']['min'],
      VALIDATION_CONFIGS['pcsCart']['max']
    ),
  },
  netWeight: {
    min: ERROR_MSGS.GREATER_THAN_ZERO_MSG,
  },
  price: {
    min: ERROR_MSGS.NO_NEGATIVE_NUMBER_MSG,
  },
  category: {
    required: ERROR_MSGS.REQ_FIELD_MSG,
  },
  vat: {
    required: ERROR_MSGS.REQ_FIELD_MSG,
  },
  idArtStatus: {
    required: ERROR_MSGS.REQ_FIELD_MSG,
  },
};
