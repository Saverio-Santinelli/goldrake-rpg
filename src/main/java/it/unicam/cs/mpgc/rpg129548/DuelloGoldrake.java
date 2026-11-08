package it.unicam.cs.mpgc.rpg129548;

/** Configurazione di una partita, condivisa dalle due interfacce. */
final class DuelloGoldrake {

    final Combattente goldrake = new Combattente("Goldrake", 100, 30);
    final Combattente nemico = new Combattente("Mostro di Vega", 80, 10);
    final Combattimento combattimento = new Combattimento(goldrake, nemico);
    final Attacco pugno = new Attacco("Pugno", 10, 0);
    final Attacco raggio = new Attacco("Raggio", 30, 20);
    final Attacco artiglio = new Attacco("Artiglio", 15, 0);
}
