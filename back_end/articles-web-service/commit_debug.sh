#!/bin/bash

# Crea 'debug-branch' se non esiste già e ci si sposta
git checkout -B debug-branch

# Aggiunge tutte le modifiche all'area di staging
git add .

# Esegue il commit con un messaggio predefinito
git commit -m "Commit automatico per prove di debug"

