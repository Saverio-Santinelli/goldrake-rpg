package it.unicam.cs.mpgc.rpg129548;

public class Combattimento {

    private final Combattente primo;
    private final Combattente secondo;
    private Combattente diTurno;

    public Combattimento(Combattente primo, Combattente secondo) {
        if (primo == null || secondo == null || primo == secondo) {
            throw new IllegalArgumentException("Servono due combattenti distinti");
        }
        if (primo.isSconfitto() || secondo.isSconfitto()) {
            throw new IllegalArgumentException("Entrambi i combattenti devono essere in piedi");
        }
        this.primo = primo;
        this.secondo = secondo;
        this.diTurno = primo;
    }

    public Combattente getDiTurno() {
        return diTurno;
    }

    public boolean isTerminato() {
        return primo.isSconfitto() || secondo.isSconfitto();
    }

    public boolean attacca(Attacco attacco) {
        if (attacco == null) {
            throw new IllegalArgumentException("Serve un attacco");
        }
        if (isTerminato()) {
            return false;
        }

        Combattente bersaglio = diTurno == primo ? secondo : primo;
        if (!attacco.esegui(diTurno, bersaglio)) {
            return false;
        }
        if (!isTerminato()) {
            diTurno = bersaglio;
        }
        return true;
    }
}