# Goldrake RPG

Progetto universitario per i corsi di Metodologie di Programmazione e Modellazione e Gestione della Conoscenza, A.A. 2025/26.

Il progetto ha come obiettivo la realizzazione di un gioco di ruolo a tema Goldrake in Java, con interfaccia grafica e persistenza dei dati.

## Autore

Saverio Santinelli — Matricola 129548

Package di riferimento: `it.unicam.cs.mpgc.rpg129548`.

- `model` contiene le regole del duello e i dati dei personaggi.
- `view` contiene l’interfaccia Swing e quella da terminale.
- `persistence` legge il catalogo XML e gestisce il salvataggio.

## Stato del progetto

La versione grafica include tre duelli, ciascuno con un'arena illustrata e un avversario con una mossa propria. La campagna parte dalla valle montana, attraversa la città di notte e si conclude alla base lunare di Vega. Dal terminale si può giocare il singolo duello lunare.

## Come eseguire il progetto

### Prerequisiti

- JDK 25 (LTS).
- Git per clonare il repository.
- Una connessione a Internet per il primo download di Gradle e delle eventuali dipendenze.

Il progetto include il **Gradle Wrapper**, che scarica e usa la versione di Gradle prevista dal progetto.

### Avvio rapido

Su macOS o Linux:

```bash
git clone https://github.com/Saverio-Santinelli/goldrake-rpg.git
cd goldrake-rpg
./gradlew build
./gradlew run
```

Su Windows PowerShell:

```powershell
git clone https://github.com/Saverio-Santinelli/goldrake-rpg.git
cd goldrake-rpg
.\gradlew.bat build
.\gradlew.bat run
```

Il comando `run` apre il menu principale del gioco.

Il progetto è stato verificato con una prova della campagna completa, inclusi la scelta della tecnica, il caricamento di un salvataggio XML con un attacco già annunciato e un nuovo tentativo dopo una sconfitta.

## Come si gioca

Dal menu principale puoi iniziare una **Nuova partita** nella valle montana, **Carica salvataggio**, aprire il briefing del Dottor Procton con **Come si gioca** oppure uscire. Durante un duello, **Menu** apre la schermata iniziale e **Torna al duello** riprende lo scontro in corso.
Nel duello scegli **Maglio Perforante** o **Alabarda Spaziale** per attaccare. Dopo una vittoria, **Prossimo duello** sblocca la tappa seguente e ripristina vita ed energia. Dopo una sconfitta, **Riprova duello** ricomincia la stessa tappa.
Quando sconfiggi il primo boss, premi **Scegli tecnica**. **Tuono Spaziale** infligge 45 danni e costa 30 energia. **Raggio Antigravità** infligge 18 danni e costa 10 energia. La scelta avvia e salva il duello in città. La tecnica rimane disponibile fino alla fine della campagna.
Ogni boss prepara una mossa speciale diversa e la annuncia un turno prima. **Guardia** usa il turno per dimezzare il danno in arrivo (arrotondato per eccesso) e recuperare fino a 10 energia. Puoi anche attaccare per provare a sconfiggere il boss prima che usi la mossa speciale.
Se hai progressi non salvati, il gioco chiede conferma prima di uscire, iniziare una nuova partita o caricare un salvataggio.
Durante un duello, **Salva partita** aggiorna il salvataggio in `.goldrake-rpg/partita.xml` nella cartella utente.
**Carica salvataggio** ripristina la tappa, la vita dei combattenti, l'energia di Goldrake, l'eventuale tecnica scelta e la fase del boss, compreso un attacco già annunciato. Se il file XML è assente, il gioco può leggere il precedente `partita.properties`. I salvataggi della versione a duello singolo riprendono dalla Luna.

Dopo la vittoria sulla Luna, **Statistiche della campagna** mostra i danni effettivi e il numero di mosse riuscite nei tre tentativi vinti. I salvataggi più vecchi possono mostrare conteggi parziali o assenti.

Le statistiche iniziali di Goldrake e dei tre boss, insieme ai contrattacchi dei boss, sono definite in [personaggi.xml](src/main/resources/personaggi.xml). Il gioco legge questo catalogo all'avvio.

Per giocare dal terminale usa `./gradlew -q runConsole` oppure `.\gradlew.bat -q runConsole` su Windows.
Premi `1` per il Maglio Perforante, `2` per l’Alabarda Spaziale, `3` per Guardia oppure `0` per uscire. Conferma con Invio.

## Uso di strumenti di intelligenza artificiale

Durante la realizzazione del progetto è stato utilizzato ChatGPT come strumento di supporto allo sviluppo e alla revisione. Il contributo personale dell’autore ha riguardato la scelta del tema e delle funzionalità, la valutazione delle proposte e dell’interfaccia e le prove manuali del gioco. ChatGPT ha fornito spiegazioni, proposte di implementazione e assistenza nella scrittura e nella revisione di parti del codice. Le soluzioni sono state adattate alle esigenze di Goldrake RPG e verificate durante lo sviluppo.

In particolare, ChatGPT è stato utilizzato per approfondire e svolgere queste attività.

- Approfondire aspetti teorici e pratici di Java e Swing e configurare Java 25, Gradle, IntelliJ IDEA e Git.
- Confrontare alternative di progettazione e organizzazione del codice, condividendo le regole dei duelli tra interfaccia grafica e terminale.
- Preparare e rivedere parti del combattimento, dell’interfaccia, del catalogo dei personaggi e della persistenza XML.
- Analizzare errori di compilazione e problemi incontrati durante lo sviluppo ed eseguire verifiche del gioco e dei salvataggi.
- Generare o modificare sprite e sfondi delle arene e del menu attraverso gli strumenti di generazione di immagini di ChatGPT.
- Preparare e rivedere la documentazione del progetto.

Per una descrizione più dettagliata dell’uso dell’IA, delle responsabilità delle classi, dell’organizzazione dei dati e degli esempi di estensione è disponibile la [Wiki del progetto](https://github.com/Saverio-Santinelli/goldrake-rpg/wiki).
