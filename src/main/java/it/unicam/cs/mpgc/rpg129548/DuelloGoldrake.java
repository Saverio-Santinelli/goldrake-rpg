package it.unicam.cs.mpgc.rpg129548;

/** Configurazione di una partita, condivisa dalle due interfacce. */
final class DuelloGoldrake {

    final Tappa tappa;
    final Combattente goldrake = new Combattente("Goldrake", 100, 30);
    final Combattente nemico;
    final Combattimento combattimento;
    final Attacco maglioPerforante = new Attacco("Maglio Perforante", 10, 0);
    final Attacco alabardaSpaziale = new Attacco("Alabarda Spaziale", 30, 20);
    final Attacco contrattacco;

    DuelloGoldrake(Tappa tappa) {
        this.tappa = tappa;
        nemico = new Combattente(tappa.nemico, tappa.vitaNemico, 10);
        combattimento = new Combattimento(goldrake, nemico);
        contrattacco = new Attacco(tappa.mossaNemico, tappa.dannoNemico, 0);
    }
}
