package it.unicam.cs.mpgc.rpg129548.model;

/**
 * Descrive un attacco con danno e costo energetico fissi.
 */
public final class Attacco implements Mossa {

    private final String nome;
    private final int danno;
    private final int costoEnergia;

    /**
     * Crea un attacco.
     *
     * @param nome nome non nullo né vuoto
     * @param danno danno inflitto, maggiore di zero
     * @param costoEnergia energia richiesta, maggiore o uguale a zero
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Attacco(String nome, int danno, int costoEnergia) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "Il nome non può essere nullo o vuoto");
        }
        if (danno <= 0) {
            throw new IllegalArgumentException(
                    "Il danno deve essere maggiore di zero");
        }
        if (costoEnergia < 0) {
            throw new IllegalArgumentException(
                    "Il costo energetico non può essere negativo");
        }

        this.nome = nome;
        this.danno = danno;
        this.costoEnergia = costoEnergia;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getDanno() {
        return danno;
    }

    @Override
    public int getCostoEnergia() {
        return costoEnergia;
    }
    /**
     * Esegue l'attacco consumando energia e danneggiando il bersaglio.
     *
     * @param attaccante combattente che esegue l'attacco
     * @param bersaglio combattente che subisce il danno
     * @return true se l'attacco viene eseguito, false se un combattente
     *         è sconfitto o l'energia è insufficiente
     * @throws IllegalArgumentException se un combattente è nullo
     *         o il bersaglio coincide con l'attaccante
     */
    @Override
    public boolean esegui(Personaggio attaccante, Personaggio bersaglio) {
        if (attaccante == null || bersaglio == null) {
            throw new IllegalArgumentException(
                    "Attaccante e bersaglio devono essere presenti");
        }
        if (attaccante == bersaglio) {
            throw new IllegalArgumentException(
                    "Un combattente non può attaccare se stesso");
        }
        if (attaccante.isSconfitto() || bersaglio.isSconfitto()) {
            return false;
        }
        if (!attaccante.consumaEnergia(costoEnergia)) {
            return false;
        }

        bersaglio.subisciDanno(danno);
        return true;
    }
}
