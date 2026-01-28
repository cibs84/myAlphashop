```markdown
# Progetto Full Stack - Sistema Gestione Articoli e Utenti

Questo progetto nasce come ambiente di studio e sperimentazione pratica sulle architetture a microservizi, focalizzandosi sull'integrazione tra diverse tecnologie e sulla gestione reattiva dello stato nel frontend.

## Architettura di Sistema

L'applicazione è composta da una catena di servizi containerizzati:
- Frontend: Angular 13.
- Backend 1: Servizio Gestione Articoli (PostgreSQL).
- Backend 2: Servizio Gestione Utenti (MongoDB).
- Backend 3: Servizio Autenticazione (JWT).
- Reverse Proxy: Nginx (configurato come webserver e gateway per il routing dei servizi).

Questa scelta tecnologica eterogenea è stata adottata per approfondire sia database relazionali che documentali all'interno della stessa infrastruttura.

## Stato dello Sviluppo

Il progetto è un prototipo funzionale ("Work in Progress"). Al momento sono implementate le seguenti funzionalità:
- Flusso completo di Autenticazione (Login/Logout).
- Operazioni CRUD complete sulla gestione articoli.
- Sistema di ricerca articoli per Descrizione, Codice Articolo e Barcode.
- Paginazione custom e gestione della visualizzazione (Tabella/Card).
- Sistema centralizzato di notifiche (Toast), gestione dei loader e degli errori.
- Gestione dello stato basata su RxJS.

### Funzionalità non implementate o in fase di sviluppo:
- Persistenza delle immagini prodotto (placeholder presenti nel codice).
- CRUD utenti lato frontend (Registrazione).
- Filtri avanzati di ricerca (eccetto i selettori di categoria).
- Internazionalizzazione (il sistema custom è predisposto ma non popolato).
- Ottimizzazione responsive e rifinitura dello stile CSS.

## Note Tecniche

1. Logica Reattiva: Non è stato utilizzato NgRx per scelta didattica, preferendo approfondire la gestione dello stato tramite BehaviorSubject e operatori complessi di RxJS (switchMap, combineLatest, shareReplay).
2. Sicurezza: I pulsanti per le operazioni non permesse sono visibili anche a utenti con permessi limitati (es: ROLE_USER) per permettere di testare agevolmente la risposta del sistema (403 Forbidden) e la gestione degli errori lato frontend tramite Interceptor.
3. Migrazione: È prevista una migrazione ad Angular 19/20 per l'integrazione di feature più recenti come Signals.

## Istruzioni per l'esecuzione (Docker)

Per avviare l'intero ecosistema con il seeding automatico dei dati, eseguire dalla root del progetto:

docker compose down -v && docker compose up -d --build

Dati di accesso predefiniti:
- Amministratore: userAdmin / pass1234
- Utente standard: userRead / pass1234

```