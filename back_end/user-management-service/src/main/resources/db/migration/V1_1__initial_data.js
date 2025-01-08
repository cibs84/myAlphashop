db.users.drop(); // Elimina la collezione se esiste

db.users.insertMany([
    {
        "id": "66f59bdb434c31046d1766e1",
        "userId": "ernesto",
        "password": "$2a$10$YqVKwFlmoDTtJhi8sFrC7ebOjdySyrcDxEPB9hjOFW5SxXsPJNQiC",
        "active": true,
        "roles": [
            "USER"
        ]
    },
    {
        "id": "66f59be7434c31046d1766e2",
        "userId": "evaristo",
        "password": "$2a$10$lH6idMwg4Fesx2wSGdsBju7VzV8OiNhN4EXa6PUBKMgv7N6iZCc9e",
        "active": true,
        "roles": [
            "USER"
        ]
    }
]);