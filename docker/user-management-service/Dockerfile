FROM ubuntu:18.04

LABEL Mario Rossi <m.rossi@alphashop.com> 

RUN apt-get update && apt-get install -y mongodb && apt-get install nano

# Create the MongoDB data directory
RUN mkdir -p var/lib/mongodb/data/

# Expose default port 27017 from the container to the host
EXPOSE 27017

#Start MongoDb
CMD ["mongod", "-f", "/etc/mongodb.conf"]



