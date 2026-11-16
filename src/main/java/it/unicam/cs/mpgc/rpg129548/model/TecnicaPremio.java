package it.unicam.cs.mpgc.rpg129548.model;

/** Tecniche tra cui scegliere dopo la vittoria nella valle. */
public enum TecnicaPremio {
    TUONO_SPAZIALE(new Attacco("Tuono Spaziale", 45, 30)),
    RAGGIO_ANTIGRAVITA(new Attacco("Raggio Antigravità", 18, 10));

    private final Attacco attacco;

    TecnicaPremio(Attacco attacco) {
        this.attacco = attacco;
    }

    public Attacco attacco() {
        return attacco;
    }
}
