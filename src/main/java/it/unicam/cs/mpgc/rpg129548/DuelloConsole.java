package it.unicam.cs.mpgc.rpg129548;

import java.util.Scanner;

public class DuelloConsole {

    public static void main(String[] args) {
        DuelloGoldrake partita = new DuelloGoldrake(Tappa.LUNA);
        Combattente goldrake = partita.goldrake;
        Combattente nemico = partita.nemico;
        Attacco maglioPerforante = partita.maglioPerforante;
        Attacco alabardaSpaziale = partita.alabardaSpaziale;

        System.out.println("Goldrake RPG - Duello contro il " + nemico.getNome());
        try (Scanner terminale = new Scanner(System.in)) {
            while (!partita.isTerminato()) {
                System.out.printf("%nGoldrake: %d PV, %d energia | %s: %d PV%n",
                        goldrake.getPuntiVita(), goldrake.getEnergia(), nemico.getNome(), nemico.getPuntiVita());
                System.out.printf("1. %s (%d danni, %d energia)%n",
                        maglioPerforante.getNome(), maglioPerforante.getDanno(), maglioPerforante.getCostoEnergia());
                System.out.printf("2. %s (%d danni, %d energia)%n",
                        alabardaSpaziale.getNome(), alabardaSpaziale.getDanno(), alabardaSpaziale.getCostoEnergia());
                System.out.println("3. Guardia (riduce il danno e recupera energia)");
                partita.avvisoSpeciale().ifPresent(System.out::println);
                System.out.print("Scegli una mossa (0 per uscire): ");

                if (!terminale.hasNextLine()) {
                    return;
                }
                String scelta = terminale.nextLine().strip();
                if (scelta.equals("3")) {
                    System.out.println("Goldrake si mette in guardia.");
                    mostraRisposta(partita.usaGuardia(), goldrake);
                    continue;
                }
                Attacco mossa;
                switch (scelta) {
                    case "0" -> {
                        return;
                    }
                    case "1" -> mossa = maglioPerforante;
                    case "2" -> mossa = alabardaSpaziale;
                    default -> {
                        System.out.println("Scelta non valida.");
                        continue;
                    }
                }

                DuelloGoldrake.EsitoTurno turno = partita.giocaTurno(mossa);
                if (!turno.eseguito()) {
                    System.out.println("Energia insufficiente. Scegli un'altra mossa.");
                    continue;
                }
                System.out.printf("Goldrake usa %s. Al nemico restano %d PV.%n",
                        mossa.getNome(), nemico.getPuntiVita());
                mostraRisposta(turno, goldrake);
            }
        }

        if (goldrake.isSconfitto()) {
            System.out.println("Sconfitta! Goldrake è stato battuto.");
        } else {
            System.out.println("Vittoria! Goldrake ha sconfitto il " + nemico.getNome() + ".");
        }
    }

    private static void mostraRisposta(DuelloGoldrake.EsitoTurno turno, Combattente goldrake) {
        turno.rispostaBoss().ifPresent(mossa -> System.out.printf(
                "Il nemico usa %s (%d danni). A Goldrake restano %d PV.%n",
                mossa.getNome(), mossa.getDanno(), goldrake.getPuntiVita()));
    }
}
