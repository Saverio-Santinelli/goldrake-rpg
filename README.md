# Goldrake RPG

Progetto universitario per i corsi di Metodologie di Programmazione e Modellazione e Gestione della Conoscenza, A.A. 2025/26.

Il progetto ha come obiettivo la realizzazione di un gioco di ruolo a tema Goldrake in Java, con interfaccia grafica e persistenza dei dati.

## Autore

Saverio Santinelli — Matricola 129548

Package di riferimento: `it.unicam.cs.mpgc.rpg129548`.

## Stato del progetto

È disponibile un primo duello a turni, giocabile in una finestra o dal terminale. La persistenza è in programma.

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
Nella finestra scegli **Pugno** o **Raggio** per attaccare. Con **Nuova partita** ricominci il duello.

Per giocare dal terminale usa `./gradlew -q runConsole` oppure `.\gradlew.bat -q runConsole` su Windows.
Premi `1` per il pugno, `2` per il raggio oppure `0` per uscire. Conferma con Invio.

## Uso di strumenti di intelligenza artificiale

È stato utilizzato ChatGPT come supporto alla configurazione di Git, GitHub, IntelliJ IDEA e Gradle, all’impostazione di Java 25, alla stesura di questo README e allo sviluppo del duello a turni e delle sue interfacce.

La dichiarazione sarà aggiornata durante lo sviluppo. Una descrizione dettagliata dell’uso degli strumenti di AI sarà mantenuta nella Wiki del repository.
