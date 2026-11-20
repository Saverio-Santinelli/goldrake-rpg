package it.unicam.cs.mpgc.rpg129548.controller;

import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Mossa;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import it.unicam.cs.mpgc.rpg129548.persistence.ArchivioPartita;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/** Gestisce la partita corrente e i progressi non salvati, senza conoscere la vista. */
public final class SessionePartita {
    private final ConfigurazioneDuelli configurazione;
    private final ArchivioPartita archivio;
    private DuelloGoldrake partita;
    private boolean progressiDaSalvare;

    public SessionePartita(ConfigurazioneDuelli configurazione, ArchivioPartita archivio) {
        this.configurazione = Objects.requireNonNull(configurazione);
        this.archivio = Objects.requireNonNull(archivio);
        nuovaPartita();
    }

    public DuelloGoldrake partita() {
        return partita;
    }

    public boolean haProgressiDaSalvare() {
        return progressiDaSalvare;
    }

    public void nuovaPartita() {
        partita = new DuelloGoldrake(configurazione, Tappa.VALLE);
        progressiDaSalvare = false;
    }

    public DuelloGoldrake.EsitoTurno attacca(Mossa mossa) {
        return registra(partita.giocaTurno(mossa));
    }

    public DuelloGoldrake.EsitoTurno usaGuardia() {
        return registra(partita.usaGuardia());
    }

    private DuelloGoldrake.EsitoTurno registra(DuelloGoldrake.EsitoTurno turno) {
        if (turno.eseguito()) {
            progressiDaSalvare = true;
        }
        return turno;
    }

    public void prosegui() {
        partita = partita.prosegui(Optional.empty());
        progressiDaSalvare = true;
    }

    /** Il nuovo stato diventa corrente solo dopo un salvataggio riuscito. */
    public void scegliTecnica(TecnicaPremio premio) throws IOException {
        DuelloGoldrake prossimo = partita.prosegui(Optional.of(premio));
        archivio.salva(prossimo);
        partita = prossimo;
        progressiDaSalvare = false;
    }

    public void salva() throws IOException {
        archivio.salva(partita);
        progressiDaSalvare = false;
    }

    public void carica() throws IOException {
        partita = archivio.carica();
        progressiDaSalvare = false;
    }
}
