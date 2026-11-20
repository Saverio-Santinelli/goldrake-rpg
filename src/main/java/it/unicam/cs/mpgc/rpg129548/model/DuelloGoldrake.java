package it.unicam.cs.mpgc.rpg129548.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/** Coordina il duello e il passaggio di tappa per le due interfacce. */
public final class DuelloGoldrake {

    public static final int ENERGIA_GUARDIA = 10;
    private final ConfigurazioneDuelli configurazione;

    private final Tappa tappa;
    private final Personaggio goldrake;
    private final Personaggio nemico;
    private final Combattimento combattimento;
    private final Mossa maglioPerforante = new Attacco("Maglio Perforante", 10, 0);
    private final Mossa alabardaSpaziale = new Attacco("Alabarda Spaziale", 30, 20);
    private final ConfigurazioneDuelli.Avversario avversario;
    private final Optional<TecnicaPremio> tecnicaPremio;
    // Conta le risposte normali e il turno di carica fino alla prossima mossa speciale.
    private int faseBoss;
    private final Map<Tappa, StatisticheDuello> duelliVinti = new EnumMap<>(Tappa.class);
    private int magli;
    private int alabarde;
    private int tecniche;
    private int guardie;
    private boolean conteggioCompleto = true;

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

    public Personaggio goldrake() {
        return goldrake;
    }

    public Personaggio nemico() {
        return nemico;
    }

    public Mossa maglioPerforante() {
        return maglioPerforante;
    }

    public Mossa alabardaSpaziale() {
        return alabardaSpaziale;
    }

    public EsitoTurno giocaTurno(Mossa mossa) {
        boolean usaPremio = tecnicaPremio.map(premio -> premio.attacco() == mossa).orElse(false);
        if (mossa != maglioPerforante && mossa != alabardaSpaziale && !usaPremio) {
            throw new IllegalArgumentException("Mossa non disponibile per Goldrake");
        }
        if (!inAttesaDiMossa() || !combattimento.attacca(mossa)) {
            return new EsitoTurno(false, Optional.empty());
        }
        if (mossa == maglioPerforante) {
            magli++;
        } else if (mossa == alabardaSpaziale) {
            alabarde++;
        } else {
            tecniche++;
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
        guardie++;
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
        Mossa mossa = speciale ? avversario.speciale().mossa() : avversario.contrattacco();
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
        var prossimo = new DuelloGoldrake(configurazione, destinazione, scelta.or(() -> tecnicaPremio), 0);
        prossimo.duelliVinti.putAll(duelliVinti);
        if (nemico.isSconfitto()) {
            prossimo.duelliVinti.put(tappa, statisticheCorrenti());
        }
        return prossimo;
    }

    private StatisticheDuello statisticheCorrenti() {
        return new StatisticheDuello(nemico.getPuntiVitaMassimi() - nemico.getPuntiVita(),
                goldrake.getPuntiVitaMassimi() - goldrake.getPuntiVita(),
                magli, alabarde, tecniche, guardie, conteggioCompleto);
    }

    public Map<Tappa, StatisticheDuello> statisticheCampagna() {
        Map<Tappa, StatisticheDuello> riepilogo = new EnumMap<>(Tappa.class);
        riepilogo.putAll(duelliVinti);
        riepilogo.put(tappa, statisticheCorrenti());
        return Map.copyOf(riepilogo);
    }

    public void ripristinaStatistiche(Map<Tappa, StatisticheDuello> statistiche) {
        statistiche.forEach((tappaSalvata, dati) -> {
            if (tappaSalvata.ordinal() < tappa.ordinal()) {
                duelliVinti.put(tappaSalvata, dati);
            }
        });
        StatisticheDuello correnti = statistiche.get(tappa);
        conteggioCompleto = correnti != null && correnti.conteggioCompleto();
        if (correnti != null) {
            magli = correnti.magli();
            alabarde = correnti.alabarde();
            tecniche = correnti.tecniche();
            guardie = correnti.guardie();
        }
    }

    public record EsitoTurno(boolean eseguito, Optional<Mossa> rispostaBoss) {
    }
}
