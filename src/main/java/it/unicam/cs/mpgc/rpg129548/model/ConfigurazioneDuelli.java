package it.unicam.cs.mpgc.rpg129548.model;

import java.util.Map;

/** Profili iniziali della campagna, indipendenti dal formato del catalogo. */
public record ConfigurazioneDuelli(Profilo goldrake, Map<Tappa, Avversario> avversari) {
    public ConfigurazioneDuelli {
        avversari = Map.copyOf(avversari);
    }

    Avversario avversario(Tappa tappa) {
        return avversari.get(tappa);
    }

    public record Profilo(String nome, int puntiVita, int energia) {
        public Profilo {
            if (nome.isBlank() || puntiVita <= 0 || energia <= 0) {
                throw new IllegalArgumentException("Profilo non valido: " + nome);
            }
        }

        Personaggio creaCombattente() {
            return new Combattente(nome, puntiVita, energia);
        }
    }

    public record Avversario(Profilo profilo, Mossa contrattacco, AttaccoSpeciale speciale) {
    }

    public record AttaccoSpeciale(Mossa mossa, int turniNormali, String avviso) {
        public AttaccoSpeciale {
            if (turniNormali < 1 || turniNormali == Integer.MAX_VALUE || avviso.isBlank()) {
                throw new IllegalArgumentException("Configurazione della mossa speciale non valida");
            }
        }
    }
}
