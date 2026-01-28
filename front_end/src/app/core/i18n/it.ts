import { TranslationMap } from "./translation-map.type";

export const IT: TranslationMap = {
  common: {
    genericError: "Si è verificato un errore, riprova più tardi",
    operationNotAllowed: "Operazione non consentita",
    confirmDelete: "Confermi l'eliminazione?",
    actionCancelled: "Operazione annullata"
  },
  network: {
    serverUnavailable: "Servizio temporaneamente non disponibile",
    timeout: "Timeout della richiesta",
    connectionError: "Errore di connessione",
    badRequest: "Richiesta non valida",
    internalServerError: "Errore interno del server"
  },
  auth: {
    invalidCredentials: "Credenziali non valide",
    authenticationFailed: "Autenticazione fallita",
    loginSuccess: "Login effettuato",
    sessionExpired: "Sessione scaduta, effettua di nuovo il login",
    unauthorized: "Accesso non autorizzato",
    forbidden: "Non hai i permessi per eseguire questa azione",
    userNotFound: "Utente non trovato",
    userAlreadyExists: "L'utente esiste già",
    registrationSuccess: "Registrazione completata con successo",
    logoutSuccess: "Logout effettuato con successo",
    accountDeleted: "Account eliminato con successo"
  },
  crud: {
    createSuccess: "Creazione avvenuta con successo",
    updateSuccess: "Aggiornamento avvenuto con successo",
    deleteSuccess: "Eliminazione avvenuta con successo",
    loadError: "Errore nel caricamento dei dati",
    saveError: "Errore nel salvataggio dei dati",
    deleteError: "Errore nell'eliminazione dell'elemento",
    resourceNotFound: "Risorsa non trovata",
    noChangesDetected: "Nessuna modifica rilevata",
    itemAlreadyExists: "Esiste già un elemento con questi dettagli nel sistema"
  },
  validation: {
    invalidForm: "Dati non validi. Controlla il modulo.",
    required: "Il campo è obbligatorio",
    invalidEmail: "Indirizzo email non valido",
    minLength: "Numero minimo di caratteri non rispettato",
    maxLength: "Numero massimo di caratteri superato",
    sizeMin: "Per procedere, assicurati che il campo '{0}' contenga almeno {min} elementi/caratteri.",
    passwordMismatch: "Le password non corrispondono",
    invalidFormat: "Formato non valido",
    positive: "Il numero deve essere maggiore di 0",
    positiveOrZero: "Numero negativo non consentito",
    minMax: "Il numero è fuori dall'intervallo consentito",
    lettersAndNumbers: "Sono consentiti solo lettere e numeri"
  }
};
