package it.unicam.cs.mpgc.rpg129548.model;

/** Contratto di un partecipante al combattimento, indipendente dalla sua implementazione. */
public interface Personaggio {
    String getNome();
    int getPuntiVitaMassimi();
    int getPuntiVita();
    int getEnergiaMassima();
    int getEnergia();
    boolean isSconfitto();
    void subisciDanno(int danno);
    boolean consumaEnergia(int costo);
    void ricaricaEnergia(int quantita);
}
