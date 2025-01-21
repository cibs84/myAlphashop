### Avviamento Container in VPS
Per avviare i container utilizzando le variabili dei file `.env` e `.env.production`:
```
docker compose --env-file .env --env-file .env.production up -d
```
**N.B.** In caso di duplicazioni delle variabili, quelle del file precedente vengono sovrascritte da quelle del file successivo. In questo caso quelle di `.env.production` sovrascrivono quelle di `.env`