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
    private final Optional<TecnicaPremio> tecnicaPremio;

    DuelloGoldrake(Tappa tappa) {
        this(tappa, Optional.empty());
    }

    DuelloGoldrake(Tappa tappa, Optional<TecnicaPremio> tecnicaPremio) {
        if (tappa == Tappa.VALLE && tecnicaPremio.isPresent()) {
            throw new IllegalArgumentException("La tecnica premio si sblocca dopo la valle");
        }
        this.tappa = tappa;
        this.tecnicaPremio = tecnicaPremio;
        goldrake = PERSONAGGI.goldrake().creaCombattente();
        CatalogoPersonaggi.Avversario avversario = PERSONAGGI.avversario(tappa);
        nemico = avversario.profilo().creaCombattente();
        combattimento = new Combattimento(goldrake, nemico);
        contrattacco = avversario.contrattacco();
    }

    EsitoTurno giocaTurno(Attacco mossa) {
        boolean usaPremio = tecnicaPremio.map(premio -> premio.attacco() == mossa).orElse(false);
        if (mossa != maglioPerforante && mossa != alabardaSpaziale && !usaPremio) {
            throw new IllegalArgumentException("Mossa non disponibile per Goldrake");
        }
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

    Optional<TecnicaPremio> tecnicaPremio() {
        return tecnicaPremio;
    }

    boolean deveSceglierePremio() {
        return tappa == Tappa.VALLE && nemico.isSconfitto() && !goldrake.isSconfitto();
    }

    DuelloGoldrake prosegui(Optional<TecnicaPremio> scelta) {
        Tappa destinazione = tappaDaGiocare()
                .orElseThrow(() -> new IllegalStateException("Nessun duello da iniziare"));
        if (scelta.isPresent() != deveSceglierePremio()) {
            throw new IllegalArgumentException("La scelta è richiesta solo dopo la vittoria nella valle");
        }
        return new DuelloGoldrake(destinazione, scelta.or(() -> tecnicaPremio));
    }

    enum EsitoTurno {
        NON_ESEGUITO, SOLO_ATTACCO, CON_CONTRATTACCO
    }
}
