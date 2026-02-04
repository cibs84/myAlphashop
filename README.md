# Progetto Full Stack - Sistema Gestione Articoli e Utenti

Questo progetto nasce come ambiente di studio e sperimentazione pratica sulle architetture a microservizi, focalizzandosi sull'integrazione tra diverse tecnologie e sulla gestione reattiva dello stato nel frontend.

## Architettura di Sistema

L'applicazione è composta da una catena di servizi containerizzati, tutti basati su **Java 21** e **Spring Boot 3.4.x**:

- **Frontend**: Angular 20 (implementata nuova sintassi *Control Flow*).
  - Styling gestito tramite **Bootstrap 6** e **SCSS**.
- **Backend 1: Articles Service (Port 8080)**
  - Framework: **Spring Boot 3.4.0**.
  - Database: **PostgreSQL** con **Flyway** per la migrazione degli schemi.
  - Testing: Integrazione con **Testcontainers** (attualmente in fase di aggiornamento).
- **Backend 2: User Management Service (Port 8081)**
  - Framework: **Spring Boot 3.4.3**.
  - Database: **MongoDB** (Spring Data MongoDB).
- **Backend 3: JWT Auth Service (Port 8082)**
  - Framework: **Spring Boot 3.4.0**.
  - Sicurezza: **JJWT 0.12.3** per la generazione e validazione dei token.
- **Reverse Proxy**: Nginx (configurato come webserver e gateway per il routing dei servizi).

Questa scelta tecnologica eterogenea è stata adottata per l'utilizzo di database relazionali e documentali all'interno della stessa infrastruttura.

## Stato dello Sviluppo

Il progetto è un prototipo funzionale in fase di sviluppo ("Work in Progress"). Al momento sono implementate le seguenti funzionalità:
- Flusso di Autenticazione (Login/Logout).
- Operazioni CRUD sulla gestione articoli.
- Sistema di ricerca articoli per Descrizione, Codice Articolo e Barcode.
- Paginazione e gestione della visualizzazione (Tabella/Card).
- Sistema centralizzato di notifiche (Toast), gestione dei loader e degli errori.
- Gestione dello stato basata su RxJS.

### Roadmap e feature in fase di sviluppo:
- **Registrazione Utenti**: Implementazione del CRUD lato frontend per la gestione della tabella user (endpoint backend già predisposti).
- **Modernizzazione Angular 20**: Refactoring progressivo per l'adozione degli standard della versione 20 (Signals, Standalone Components, Required Inputs).
- **Nuovi Microservizi**:
  - **Price Management Service**: Gestione listini, sconti, arrotondamenti e regole di business sui prezzi degli articoli.
  - **Promo Management Service**: Sistema dedicato alla gestione delle promozioni.
- **Comunicazione Inter-Service**: Implementazione del sistema di comunicazione tra microservizi tramite **Spring Cloud OpenFeign**.
- **State Management**: Integrazione prevista di **NgRx** per la gestione globale dello stato.
- **Gestione Ruoli**: Implementazione completa lato frontend dei ruoli.
- **Testing**: Creazione della suite di test per Angular e ripristino dei test backend post-refactoring.

## Note Tecniche

1. **Logica Reattiva**: Gestione dello stato tramite `BehaviorSubject` e operatori RxJS (`switchMap`, `combineLatest`, `shareReplay`).
2. **Evoluzione Angular**: Utilizzo del nuovo Control Flow (`@if`, `@for`) per la gestione dei template.
3. **Sicurezza**: I pulsanti per le operazioni non permesse sono visibili anche a utenti con permessi limitati per verificare la risposta del sistema (403 Forbidden) e la gestione degli errori tramite Interceptor.

## Istruzioni per l'esecuzione (Docker)

Per avviare l'intero ecosistema con il seeding automatico dei dati, eseguire dalla root del progetto:

```bash
docker compose down -v && docker compose up -d --build
```

L'applicazione sarà raggiungibile all'indirizzo: http://localhost:8084

Dati di accesso predefiniti:
- Amministratore: userAdmin / pass1234
- Utente standard: userRead / pass1234