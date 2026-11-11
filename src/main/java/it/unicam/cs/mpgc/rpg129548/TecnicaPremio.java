package it.unicam.cs.mpgc.rpg129548;

/** Tecniche tra cui scegliere dopo la vittoria nella valle. */
enum TecnicaPremio {
    TUONO_SPAZIALE(new Attacco("Tuono Spaziale", 45, 30)),
    RAGGIO_ANTIGRAVITA(new Attacco("Raggio Antigravità", 18, 10));

    private final Attacco attacco;

    TecnicaPremio(Attacco attacco) {
        this.attacco = attacco;
    }

    Attacco attacco() {
        return attacco;
    }
}
