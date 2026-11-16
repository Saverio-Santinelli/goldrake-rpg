package it.unicam.cs.mpgc.rpg129548.model;

/** Risultato di un tentativo, con il danno effettivo e le azioni riuscite. */
public record StatisticheDuello(int inflitti, int subiti, int magli, int alabarde,
                               int tecniche, int guardie, boolean conteggioCompleto) {

    public StatisticheDuello {
        if (inflitti < 0 || subiti < 0 || magli < 0 || alabarde < 0 || tecniche < 0 || guardie < 0) {
            throw new IllegalArgumentException("Statistiche del duello non valide");
        }
    }
}
