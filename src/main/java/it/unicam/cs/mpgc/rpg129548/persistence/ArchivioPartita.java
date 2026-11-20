package it.unicam.cs.mpgc.rpg129548.persistence;

import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import java.io.IOException;

/** Conservazione di una partita, indipendente dal formato e dalla destinazione. */
public interface ArchivioPartita {
    void salva(DuelloGoldrake partita) throws IOException;
    DuelloGoldrake carica() throws IOException;
}
