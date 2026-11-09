package it.unicam.cs.mpgc.rpg129548;

/** Configurazione di una partita, condivisa dalle due interfacce. */
final class DuelloGoldrake {

    final Tappa tappa;
    final Combattente goldrake = new Combattente("Goldrake", 100, 30);
    final Combattente nemico;
    final Combattimento combattimento;
    final Attacco pugno = new Attacco("Pugno", 10, 0);
    final Attacco raggio = new Attacco("Raggio", 30, 20);
    final Attacco artiglio;

    DuelloGoldrake(Tappa tappa) {
        this.tappa = tappa;
        nemico = new Combattente(tappa.nemico, tappa.vitaNemico, 10);
        combattimento = new Combattimento(goldrake, nemico);
        artiglio = new Attacco("Artiglio", tappa.dannoNemico, 0);
    }
}
