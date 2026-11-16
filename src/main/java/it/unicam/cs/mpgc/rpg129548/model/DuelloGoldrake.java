package it.unicam.cs.mpgc.rpg129548.model;

import java.util.Optional;

/** Coordina il duello e il passaggio di tappa per le due interfacce. */
public final class DuelloGoldrake {

    public static final int ENERGIA_GUARDIA = 10;
    private final ConfigurazioneDuelli configurazione;

    private final Tappa tappa;
    private final Combattente goldrake;
    private final Combattente nemico;
    private final Combattimento combattimento;
    private final Attacco maglioPerforante = new Attacco("Maglio Perforante", 10, 0);
    private final Attacco alabardaSpaziale = new Attacco("Alabarda Spaziale", 30, 20);
    private final ConfigurazioneDuelli.Avversario avversario;
    private final Optional<TecnicaPremio> tecnicaPremio;
    // Conta le risposte normali e il turno di carica fino alla prossima mossa speciale.
    private int faseBoss;

    public DuelloGoldrake(ConfigurazioneDuelli configurazione, Tappa tappa) {
        this(configurazione, tappa, Optional.empty(), 0);
    }

    public DuelloGoldrake(ConfigurazioneDuelli configurazione, Tappa tappa,
                         Optional<TecnicaPremio> tecnicaPremio, int faseBoss) {
        this.configurazione = configurazione;
        if (tappa == Tappa.VALLE && tecnicaPremio.isPresent()) {
            throw new IllegalArgumentException("La tecnica premio si sblocca dopo la valle");
        }
        this.tappa = tappa;
        this.tecnicaPremio = tecnicaPremio;
        goldrake = configurazione.goldrake().creaCombattente();
        avversario = configurazione.avversario(tappa);
        if (faseBoss < 0 || faseBoss > faseSpecialePronto()) {
            throw new IllegalArgumentException("Fase del boss non valida");
        }
        this.faseBoss = faseBoss;
        nemico = avversario.profilo().creaCombattente();
        combattimento = new Combattimento(goldrake, nemico);
    }

    public Tappa tappa() {
        return tappa;
    }

    public Combattente goldrake() {
        return goldrake;
    }

    public Combattente nemico() {
        return nemico;
    }

    public Attacco maglioPerforante() {
        return maglioPerforante;
    }

    public Attacco alabardaSpaziale() {
        return alabardaSpaziale;
    }

    public EsitoTurno giocaTurno(Attacco mossa) {
        boolean usaPremio = tecnicaPremio.map(premio -> premio.attacco() == mossa).orElse(false);
        if (mossa != maglioPerforante && mossa != alabardaSpaziale && !usaPremio) {
            throw new IllegalArgumentException("Mossa non disponibile per Goldrake");
        }
        if (!inAttesaDiMossa() || !combattimento.attacca(mossa)) {
            return new EsitoTurno(false, Optional.empty());
        }
        if (combattimento.isTerminato()) {
            return new EsitoTurno(true, Optional.empty());
        }
        return rispondi(false);
    }

    public EsitoTurno usaGuardia() {
        if (!inAttesaDiMossa()) {
            return new EsitoTurno(false, Optional.empty());
        }
        goldrake.ricaricaEnergia(ENERGIA_GUARDIA);
        combattimento.passaTurno();
        return rispondi(true);
    }

    private EsitoTurno rispondi(boolean guardia) {
        if (faseBoss == avversario.speciale().turniNormali()) {
            faseBoss++;
            combattimento.passaTurno();
            return new EsitoTurno(true, Optional.empty());
        }
        boolean speciale = faseBoss == faseSpecialePronto();
        Attacco mossa = speciale ? avversario.speciale().mossa() : avversario.contrattacco();
        if (guardia) {
            int danno = mossa.getDanno() - mossa.getDanno() / 2;
            mossa = new Attacco(mossa.getNome(), danno, 0);
        }
        combattimento.attacca(mossa);
        faseBoss = speciale ? 0 : faseBoss + 1;
        return new EsitoTurno(true, Optional.of(mossa));
    }

    public int faseBoss() {
        return faseBoss;
    }

    public Optional<String> avvisoSpeciale() {
        if (!inAttesaDiMossa() || faseBoss != faseSpecialePronto()) {
            return Optional.empty();
        }
        return Optional.of("%s Al prossimo turno: %s (%d danni).".formatted(
                avversario.speciale().avviso(), avversario.speciale().mossa().getNome(),
                avversario.speciale().mossa().getDanno()));
    }

    private int faseSpecialePronto() {
        return avversario.speciale().turniNormali() + 1;
    }

    public boolean isTerminato() {
        return combattimento.isTerminato();
    }

    public boolean inAttesaDiMossa() {
        return !isTerminato() && combattimento.getDiTurno() == goldrake;
    }

    public Optional<Tappa> tappaDaGiocare() {
        if (goldrake.isSconfitto()) {
            return Optional.of(tappa);
        }
        if (nemico.isSconfitto() && tappa != Tappa.LUNA) {
            return Optional.of(tappa.successiva());
        }
        return Optional.empty();
    }

    public Optional<TecnicaPremio> tecnicaPremio() {
        return tecnicaPremio;
    }

    public boolean deveSceglierePremio() {
        return tappa == Tappa.VALLE && nemico.isSconfitto() && !goldrake.isSconfitto();
    }

    public DuelloGoldrake prosegui(Optional<TecnicaPremio> scelta) {
        Tappa destinazione = tappaDaGiocare()
                .orElseThrow(() -> new IllegalStateException("Nessun duello da iniziare"));
        if (scelta.isPresent() != deveSceglierePremio()) {
            throw new IllegalArgumentException("La scelta è richiesta solo dopo la vittoria nella valle");
        }
        return new DuelloGoldrake(configurazione, destinazione, scelta.or(() -> tecnicaPremio), 0);
    }

    public record EsitoTurno(boolean eseguito, Optional<Attacco> rispostaBoss) {
    }
}
