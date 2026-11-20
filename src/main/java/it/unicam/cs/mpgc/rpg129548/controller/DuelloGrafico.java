package it.unicam.cs.mpgc.rpg129548.controller;

import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Mossa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import it.unicam.cs.mpgc.rpg129548.persistence.CatalogoPersonaggi;
import it.unicam.cs.mpgc.rpg129548.persistence.SalvataggioPartita;
import it.unicam.cs.mpgc.rpg129548.view.FinestraGioco;
import it.unicam.cs.mpgc.rpg129548.view.Schermata;
import it.unicam.cs.mpgc.rpg129548.view.VistaGioco;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import javax.swing.SwingUtilities;

/** Coordina i comandi dell'utente; layout e persistenza sono delegati a collaboratori. */
public final class DuelloGrafico implements AzioniGioco {
    private final SessionePartita sessione;
    private final VistaGioco vista;

    public DuelloGrafico(SessionePartita sessione, VistaGioco vista) {
        this.sessione = Objects.requireNonNull(sessione);
        this.vista = Objects.requireNonNull(vista);
    }

    public static void avvia() {
        SwingUtilities.invokeLater(() -> {
            var configurazione = CatalogoPersonaggi.carica();
            var archivio = new SalvataggioPartita(configurazione,
                    Path.of(System.getProperty("user.home"), ".goldrake-rpg"));
            var sessione = new SessionePartita(configurazione, archivio);
            var vista = new FinestraGioco();
            var controller = new DuelloGrafico(sessione, vista);
            vista.collega(controller, sessione.partita());
            vista.mostra();
        });
    }

    @Override
    public void usaMaglio() {
        attacca(sessione.partita().maglioPerforante());
    }

    @Override
    public void usaAlabarda() {
        attacca(sessione.partita().alabardaSpaziale());
    }

    @Override
    public void usaTecnicaPremio() {
        sessione.partita().tecnicaPremio().map(TecnicaPremio::attacco).ifPresent(this::attacca);
    }

    private void attacca(Mossa mossa) {
        var turno = sessione.attacca(mossa);
        if (!turno.eseguito()) {
            vista.scrivi("Energia insufficiente. Scegli un'altra mossa.");
            return;
        }
        mostraTurno(turno, "Goldrake usa " + mossa.getNome() + ".");
    }

    @Override
    public void usaGuardia() {
        mostraTurno(sessione.usaGuardia(), "Goldrake si mette in guardia.");
    }

    private void mostraTurno(DuelloGoldrake.EsitoTurno turno, String azione) {
        if (!turno.eseguito()) {
            return;
        }
        var partita = sessione.partita();
        vista.scrivi(azione);
        turno.rispostaBoss().ifPresent(mossa -> vista.scrivi("%s usa %s (%d danni).".formatted(
                partita.nemico().getNome(), mossa.getNome(), mossa.getDanno())));
        if (partita.isTerminato()) {
            vista.scrivi(messaggioEsito(partita));
        }
        vista.aggiorna(partita);
        if (partita.isTerminato() && !partita.goldrake().isSconfitto()
                && partita.tappaDaGiocare().isEmpty()) {
            vista.mostraSchermata(Schermata.FINALE);
        }
    }

    private String messaggioEsito(DuelloGoldrake partita) {
        if (partita.goldrake().isSconfitto()) {
            return "Sconfitta. Puoi riprovare questo duello.";
        }
        if (partita.deveSceglierePremio()) {
            return "Vittoria! Scegli una nuova tecnica per il prossimo duello.";
        }
        return partita.tappaDaGiocare().isEmpty()
                ? "Campagna completata! La Terra è salva."
                : "Vittoria! Hai sbloccato il prossimo duello.";
    }

    @Override
    public void nuovaPartita() {
        if (confermaAbbandono("Nuova partita")) {
            sessione.nuovaPartita();
            mostraNuovoDuello();
        }
    }

    private void mostraNuovoDuello() {
        var partita = sessione.partita();
        vista.impostaCronaca("%s%nTi sfida: %s. Scegli una mossa.%n"
                .formatted(partita.tappa().luogo(), partita.nemico().getNome()));
        vista.aggiorna(partita);
        vista.mostraSchermata(Schermata.DUELLO);
    }

    @Override
    public void prosegui() {
        if (sessione.partita().deveSceglierePremio()) {
            vista.mostraSchermata(Schermata.TECNICHE);
        } else {
            sessione.prosegui();
            mostraNuovoDuello();
        }
    }

    @Override
    public void scegliTecnica(TecnicaPremio premio) {
        try {
            sessione.scegliTecnica(premio);
            mostraNuovoDuello();
            vista.scrivi("Tecnica sbloccata: " + premio.attacco().getNome() + ". Partita salvata.");
        } catch (IOException errore) {
            vista.mostraErroreTecnica("Impossibile salvare la tecnica. " + errore.getMessage());
        }
    }

    @Override
    public void salvaPartita() {
        try {
            sessione.salva();
            vista.scrivi("Partita salvata.");
        } catch (IOException errore) {
            vista.scrivi("Impossibile salvare la partita. " + errore.getMessage());
        }
    }

    @Override
    public void caricaPartita() {
        if (!confermaAbbandono("Carica salvataggio")) {
            return;
        }
        try {
            sessione.carica();
            vista.impostaCronaca("Partita caricata: %s. Scegli una mossa.%n"
                    .formatted(sessione.partita().tappa().luogo()));
            vista.aggiorna(sessione.partita());
            vista.mostraSchermata(Schermata.DUELLO);
        } catch (NoSuchFileException errore) {
            vista.mostraErroreCaricamento("Nessun salvataggio. Premi Salva partita durante un duello.");
        } catch (IOException errore) {
            vista.mostraErroreCaricamento("Impossibile caricare la partita. " + errore.getMessage());
        }
    }

    private boolean confermaAbbandono(String azione) {
        return !sessione.haProgressiDaSalvare() || vista.confermaAbbandono(azione);
    }

    @Override
    public void esci() {
        if (confermaAbbandono("Esci")) {
            vista.chiudi();
        }
    }
}
