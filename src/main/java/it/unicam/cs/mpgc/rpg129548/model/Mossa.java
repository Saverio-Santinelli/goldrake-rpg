package it.unicam.cs.mpgc.rpg129548.model;

/** Azione offensiva sostituibile nel motore dei combattimenti. */
public interface Mossa {
    String getNome();
    int getDanno();
    int getCostoEnergia();

    /**
     * Esegue la mossa; restituisce false senza modifiche se un partecipante è
     * sconfitto o l'energia è insufficiente.
     * @throws IllegalArgumentException se i partecipanti sono nulli o coincidono
     */
    boolean esegui(Personaggio attaccante, Personaggio bersaglio);
}
