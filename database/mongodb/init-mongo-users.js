// Ci connettiamo al database 'admin' per creare l'utente di autenticazione
db = db.getSiblingDB('admin');

// 1. Creiamo l'utente myAuthUser (usato da Spring Boot)
// Questo corrisponde a: authentication-database: admin
db.createUser({
  user: "myAuthUser",
  pwd: "pass123",
  roles: [
    { role: "readWrite", db: "ms-users" } // Permessi di lettura/scrittura sul DB dei dati
  ]
});