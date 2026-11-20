package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.controller.AzioniGioco;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;

/** Operazioni di presentazione richieste dal controller, senza dipendenza da Swing. */
public interface VistaGioco {
    void collega(AzioniGioco azioni, DuelloGoldrake partita);
    void mostra();
    void chiudi();
    boolean confermaAbbandono(String azione);
    void mostraSchermata(Schermata schermata);
    void aggiorna(DuelloGoldrake partita);
    void impostaCronaca(String testo);
    void scrivi(String messaggio);
    void mostraErroreCaricamento(String messaggio);
    void mostraErroreTecnica(String messaggio);
}
