#!/bin/bash

# Stampa in console le variabili d'ambiente assegnate nel docker-compose.yml
echo "ART_MNG__HOST: $ART_MNG__HOST"
echo "USR_MNG__HOST: $USR_MNG__HOST"
echo "JWT_AUTH__HOST: $JWT_AUTH__HOST"
echo "API_PORT: $API_PORT"

# Sostituisce nel file di configurazione 'template' di Nginx (nginx.conf.template)
# le variabili d'ambiente assegnate nel docker-compose.yml
# e salva il risultato nel file di configurazione effettivo di Nginx (nginx.conf).
envsubst '\$API_PORT \$ART_MNG__HOST \$USR_MNG__HOST \$JWT_AUTH__HOST' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

# Stampa in console la configurazione di Nginx
cat /etc/nginx/nginx.conf

# Avvia NGINX
nginx -g 'daemon off;'
