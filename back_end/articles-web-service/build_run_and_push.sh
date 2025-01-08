#!/bin/bash

# Esegui il comando package di Maven per creare il jar dell'applicazione.
# Alla fine della build dell'immagine viene eseguito il jar attraverso l'ENTRYPOINT nel Dockerfile
mvn clean package

# Verifica se il comando Maven è riuscito
if [ $? -eq 0 ]; then
  echo "Maven package eseguito con successo."

  #!/bin/bash

  # Nome e tag dell'immagine
  IMAGE_NAME="article-management"
  IMAGE_TAG="article-management"
  IMAGE_TAG_DOCKERHUB="cibs84/article-management"
  CONTAINER_NAME="article-management"
  DOCKER_FILE_PATH="./docker/Dockerfile.article-management"
  # La porta interna deve essere uguale a quella impostata nell'application yml di springboot
  PORTS=5060:5051
  NETWORK="alphashop"

  # Rimuove l'immagine esistente
  docker rmi ${IMAGE_NAME} -f

  # Rimuove l'immagine esistente con il tag utilizzato in Dockerhub
  docker rmi ${IMAGE_TAG_DOCKERHUB} -f

  # Crea la nuova immagine
  docker build -f ${DOCKER_FILE_PATH} -t ${IMAGE_TAG} .

  # Clona la nuova immagine con il tag utilizzato in Dockerhub
  docker tag ${IMAGE_TAG} ${IMAGE_TAG_DOCKERHUB}
  
  # Verifica se il comando Docker build è riuscito
  if [ $? -eq 0 ]; then
    echo "Docker build eseguito con successo."

    docker stop ${CONTAINER_NAME}
    docker rm ${CONTAINER_NAME}

    # Esegui il container Docker
    # dall'immagine creata in fase di build utilizzando il Dockerfile
    docker run -d --name ${CONTAINER_NAME} --network ${NETWORK} -p ${PORTS} ${IMAGE_NAME} sh

    # Verifica se il comando Docker run è riuscito
    if [ $? -eq 0 ]; then
      echo "Container Docker avviato con successo."

      docker push ${IMAGE_TAG_DOCKERHUB}

      # Verifica se il push della nuova immagine in DockerHub è riuscito
      if [ $? -eq 0 ]; then
      echo "PUSH in DockerHub eseguito con successo."
      else
        echo "Errore durante il PUSH in DockerHub."
        exit 1
      fi
    else
      echo "Errore durante l'esecuzione del comando Docker RUN."
      exit 1
    fi
  else
    echo "Errore durante l'esecuzione del comando Docker BUILD."
    exit 1
  fi
else
  echo "Errore durante l'esecuzione del comando Maven CLEAN PACKAGE."
  exit 1
fi