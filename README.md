# Goldrake RPG

Progetto universitario per i corsi di Metodologie di Programmazione e Modellazione e Gestione della Conoscenza, A.A. 2025/26.

Il progetto ha come obiettivo la realizzazione di un gioco di ruolo a tema Goldrake in Java, con interfaccia grafica e persistenza dei dati.

## Autore

Saverio Santinelli — Matricola 129548

Package di riferimento: `it.unicam.cs.mpgc.rpg129548`.

## Stato del progetto

La versione grafica include tre duelli, ciascuno con un'arena illustrata e un avversario con una mossa propria. La campagna parte dalla valle montana, attraversa la città di notte e si conclude alla base lunare di Vega. Dal terminale si può giocare il singolo duello lunare.

## Requisiti

- JDK 25 installato.
- Connessione Internet per il primo download di Gradle e delle dipendenze.

Il repository include Gradle Wrapper: non è necessario installare Gradle separatamente.

## Build e test

Dalla cartella principale del progetto, su macOS o Linux:

```bash
./gradlew build
```

Su Windows PowerShell:

```powershell
.\gradlew.bat build
```

Attualmente non sono presenti test; verranno aggiunti durante lo sviluppo.

## Esecuzione

Dalla cartella principale del progetto, apri il gioco con `./gradlew -q run` su macOS o Linux, oppure `.\gradlew.bat -q run` su Windows PowerShell.
Dal menu principale puoi iniziare una **Nuova partita** nella valle montana, **Carica salvataggio** oppure uscire. Durante un duello, **Menu** apre la schermata iniziale e **Torna al duello** riprende lo scontro in corso.
Nel duello scegli **Maglio Perforante** o **Alabarda Spaziale** per attaccare. Dopo una vittoria, **Prossimo duello** sblocca la tappa seguente e ripristina vita ed energia. Dopo una sconfitta, **Riprova duello** ricomincia la stessa tappa.
Durante un duello, **Salva partita** aggiorna il salvataggio in `.goldrake-rpg/partita.xml` nella cartella utente.
**Carica salvataggio** ripristina la tappa, i punti vita dei due combattenti e l'energia di Goldrake dall'ultimo salvataggio manuale, anche dopo aver riaperto il gioco. Se il file XML è assente, il gioco può leggere il precedente `partita.properties`. I salvataggi della versione a duello singolo riprendono dalla Luna.

Le statistiche iniziali di Goldrake e dei tre boss, insieme ai contrattacchi dei boss, sono definite in [personaggi.xml](src/main/resources/personaggi.xml). Il gioco legge questo catalogo all'avvio.

Per giocare dal terminale usa `./gradlew -q runConsole` oppure `.\gradlew.bat -q runConsole` su Windows.
Premi `1` per il Maglio Perforante, `2` per l’Alabarda Spaziale oppure `0` per uscire. Conferma con Invio.

## Uso di strumenti di intelligenza artificiale

È stato utilizzato ChatGPT come supporto alla configurazione di Git, GitHub, IntelliJ IDEA e Gradle, all’impostazione di Java 25, alla stesura di questo README e allo sviluppo del duello a turni, delle sue interfacce, del salvataggio e del catalogo XML dei personaggi.
Gli sprite e gli sfondi delle arene e del menu sono stati realizzati con uno strumento di generazione di immagini.

La dichiarazione sarà aggiornata durante lo sviluppo. Una descrizione dettagliata dell’uso degli strumenti di AI sarà mantenuta nella Wiki del repository.
