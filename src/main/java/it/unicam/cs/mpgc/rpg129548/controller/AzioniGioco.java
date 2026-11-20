package it.unicam.cs.mpgc.rpg129548.controller;

import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;

/** Comandi che una vista può richiedere al controller, senza accedere alle regole. */
public interface AzioniGioco {
    void usaMaglio();
    void usaAlabarda();
    void usaTecnicaPremio();
    void usaGuardia();
    void nuovaPartita();
    void salvaPartita();
    void caricaPartita();
    void prosegui();
    void scegliTecnica(TecnicaPremio premio);
    void esci();
}
