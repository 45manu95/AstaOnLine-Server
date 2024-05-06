# AstaOnLine-Server
Progetto ingegneria del software - server

Alcuni accorgimenti
1) Il nome delle tabelle nel database inizia con lettera minuscola.
2) All'interno del database ci sono alcuni accorgimenti che vanno valutati in corso di creazione delle tabelle. Ho inserito gli screenShot delle strutture dati all'interno del deliverable.
3) Rimuovere le classi di testing. Siccome è stato effettuato un test sull'invio di un'offerta su un determinato prodotto, allora il sistema entrerà in errore e l'applicazione non sarà compilata, dato che quando si trasporterà questo sistema in un nuovo dispositivo il database sarà nuovo e vuoto.
4) Avendo un collegamento con un server MySQL bisogna integrare le giuste libreria dentro java e scaricare il connector.
