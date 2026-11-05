package it.unicam.cs.mpgc.rpg129548;

/**
 * Descrive un attacco con danno e costo energetico fissi.
 */
public class Attacco {

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

    public String getNome() {
        return nome;
    }

    public int getDanno() {
        return danno;
    }

    public int getCostoEnergia() {
        return costoEnergia;
    }
}