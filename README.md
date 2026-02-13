# Progetto Full Stack - Sistema Gestione Articoli e Utenti

Questo progetto nasce come ambiente di studio e sperimentazione pratica sulle architetture a microservizi, focalizzandosi sull'integrazione tra diverse tecnologie e sulla gestione reattiva dello stato nel frontend.

## Architettura di Sistema

L'applicazione è composta da una catena di servizi containerizzati, tutti basati su **Java 21** e **Spring Boot 3.4**:

- **Frontend: Angular 20**
  - Styling gestito tramite **Bootstrap 6** e **SCSS**.
- **Backend 1: Articles Service (Port 8080)**
  - Framework: **Spring Boot 3.4**.
  - Database: **PostgreSQL** con **Flyway** per la migrazione degli schemi.
  - Testing: **JUnit** e integrazione con **Testcontainers**.
- **Backend 2: User Management Service (Port 8081)**
  - Framework: **Spring Boot 3.4**.
  - Database: **MongoDB** (Spring Data MongoDB).
- **Backend 3: JWT Auth Service (Port 8082)**
  - Framework: **Spring Boot 3.4**.
  - Sicurezza: **JJWT** per la generazione e validazione dei token.
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
- **Testing**: Creazione della suite di test per Angular e per il servizio di gestione degli utenti (user-management-service).

## Note Tecniche

1. **Logica Reattiva**: Gestione dello stato tramite `BehaviorSubject` e operatori RxJS (`switchMap`, `combineLatest`, `shareReplay`).
2. **Evoluzione Angular**: Utilizzo del nuovo Control Flow (`@if`, `@for`) per la gestione dei template.
3. **Sicurezza**: I pulsanti per le operazioni non permesse sono visibili anche a utenti con permessi limitati per verificare la risposta del sistema (403 Forbidden) e la gestione degli errori tramite Interceptor.

## CI/CD Pipeline

Il progetto integra una pipeline CI/CD realizzata con Jenkins.

### Flusso della pipeline:

1. Trigger automatico ad ogni push su GitHub (CI).
2. Build dei microservizi Spring Boot.
3. Build del frontend Angular con selezione ambiente (dev/prod).
4. Creazione e tagging delle immagini Docker.
5. Push automatico su Docker Hub.

### Gestione ambienti

La pipeline consente di selezionare l'ambiente di build:

- `dev` → configurazione per esecuzione locale
- `prod` → configurazione per deploy su VPS

Le immagini vengono taggate in modo coerente con l'ambiente selezionato.

## Istruzioni per l'esecuzione (Docker)

Il progetto è configurato per essere avviato immediatamente tramite Docker. Le immagini dei microservizi sono pre-buildate e ospitate su **Docker Hub**, garantendo che l'applicazione sia testabile senza la necessità di installare localmente Java, Maven o Angular.

### Prerequisiti

1. **Docker e Docker Compose** installati sul sistema.
2. Presenza del file `.env` nella root del progetto (necessario per mappare i nomi delle immagini e le porte).

### Avvio dell'applicazione

Per assicurarti di utilizzare le versioni più recenti dei servizi buildate dalla pipeline CI/CD (Jenkins), esegui i seguenti comandi dalla root del progetto:

```bash
# Scarica le ultime versioni delle immagini dal Docker Hub
docker compose pull

# Avvia l'intero ecosistema (ignora la build locale se l'immagine è presente)
docker compose up -d

```

> **Nota per il test**: Se desideri forzare un avvio pulito rimuovendo eventuali volumi di database precedenti, utilizza:
> `docker compose down -v && docker compose pull && docker compose up -d`

### Accesso all'applicazione

Una volta avviati i container, l'interfaccia frontend sarà raggiungibile all'indirizzo:
**[http://localhost:8084](https://www.google.com/search?q=http://localhost:8084)**

**Credenziali di test:**
| Ruolo | Username | Password |
| :--- | :--- | :--- |
| **Amministratore** | `userAdmin` | `pass1234` |
| **Utente standard** | `userRead` | `pass1234` |

---

## Deploy su VPS

Il deploy in produzione avviene tramite:

1. Pull delle immagini Docker versionate.
```
docker compose pull
```
2. Avvio dei container con variabili ambiente di produzione.
```
docker compose --env-file .env --env-file .env.production up -d
```
> Il secondo file .env.production sovrascrive le variabili condivise in .env,
consentendo la configurazione di IP, profili Spring e parametri specifici per l'ambiente di produzione.
3. Reverse proxy Nginx configurato per esporre il frontend e instradare le richieste ai microservizi.

Non è necessario ricompilare il codice in produzione.
