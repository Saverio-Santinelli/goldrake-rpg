package it.unicam.cs.mpgc.rpg129548.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContrattiCombattimentoTest {
    @Test
    void motoreAccettaPersonaggiEMosseConImplementazioniAlternative() {
        Personaggio primo = new PersonaggioAlternativo();
        Personaggio secondo = new PersonaggioAlternativo();
        var duello = new Combattimento(primo, secondo);
        Mossa mossa = new Mossa() {
            public String getNome() { return "Impulso"; }
            public int getDanno() { return 7; }
            public int getCostoEnergia() { return 2; }
            public boolean esegui(Personaggio attaccante, Personaggio bersaglio) {
                return new Attacco(getNome(), getDanno(), getCostoEnergia()).esegui(attaccante, bersaglio);
            }
        };
        assertTrue(duello.attacca(mossa));
        assertEquals(93, secondo.getPuntiVita());
        assertEquals(8, primo.getEnergia());
        assertSame(secondo, duello.getDiTurno());
    }

    @Test
    void attaccoRifiutatoNonConsumaEnergiaENonPassaIlTurno() {
        Personaggio primo = new Combattente("Primo", 100, 10);
        Personaggio secondo = new Combattente("Secondo", 100, 10);
        var duello = new Combattimento(primo, secondo);
        assertFalse(duello.attacca(new Attacco("Troppo costoso", 20, 11)));
        assertSame(primo, duello.getDiTurno());
        assertEquals(100, secondo.getPuntiVita());
        assertEquals(10, primo.getEnergia());
        assertThrows(IllegalArgumentException.class, () -> new Attacco("Colpo", 10, 0).esegui(primo, primo));
    }

    /** Un adattatore dimostra che il motore non richiede il tipo concreto Combattente. */
    private static final class PersonaggioAlternativo implements Personaggio {
        private final Personaggio stato = new Combattente("Alternativo", 100, 10);
        public String getNome() { return stato.getNome(); }
        public int getPuntiVitaMassimi() { return stato.getPuntiVitaMassimi(); }
        public int getPuntiVita() { return stato.getPuntiVita(); }
        public int getEnergiaMassima() { return stato.getEnergiaMassima(); }
        public int getEnergia() { return stato.getEnergia(); }
        public boolean isSconfitto() { return stato.isSconfitto(); }
        public void subisciDanno(int danno) { stato.subisciDanno(danno); }
        public boolean consumaEnergia(int costo) { return stato.consumaEnergia(costo); }
        public void ricaricaEnergia(int quantita) { stato.ricaricaEnergia(quantita); }
    }
}
