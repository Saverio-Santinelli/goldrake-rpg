package it.unicam.cs.mpgc.rpg129548;

import java.util.Optional;

/** Coordina il duello e il passaggio di tappa per le due interfacce. */
final class DuelloGoldrake {

    private static final CatalogoPersonaggi PERSONAGGI = CatalogoPersonaggi.carica();

    final Tappa tappa;
    final Combattente goldrake;
    final Combattente nemico;
    private final Combattimento combattimento;
    final Attacco maglioPerforante = new Attacco("Maglio Perforante", 10, 0);
    final Attacco alabardaSpaziale = new Attacco("Alabarda Spaziale", 30, 20);
    final Attacco contrattacco;

    DuelloGoldrake(Tappa tappa) {
        this.tappa = tappa;
        goldrake = PERSONAGGI.goldrake().creaCombattente();
        CatalogoPersonaggi.Avversario avversario = PERSONAGGI.avversario(tappa);
        nemico = avversario.profilo().creaCombattente();
        combattimento = new Combattimento(goldrake, nemico);
        contrattacco = avversario.contrattacco();
    }

    EsitoTurno giocaTurno(Attacco mossa) {
        if (!inAttesaDiMossa() || !combattimento.attacca(mossa)) {
            return EsitoTurno.NON_ESEGUITO;
        }
        if (combattimento.isTerminato()) {
            return EsitoTurno.SOLO_ATTACCO;
        }
        combattimento.attacca(contrattacco);
        return EsitoTurno.CON_CONTRATTACCO;
    }

    boolean isTerminato() {
        return combattimento.isTerminato();
    }

    boolean inAttesaDiMossa() {
        return !isTerminato() && combattimento.getDiTurno() == goldrake;
    }

    Optional<Tappa> tappaDaGiocare() {
        if (goldrake.isSconfitto()) {
            return Optional.of(tappa);
        }
        if (nemico.isSconfitto() && tappa != Tappa.LUNA) {
            return Optional.of(tappa.successiva());
        }
        return Optional.empty();
    }

    enum EsitoTurno {
        NON_ESEGUITO, SOLO_ATTACCO, CON_CONTRATTACCO
    }
}
