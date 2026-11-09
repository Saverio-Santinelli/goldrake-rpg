package it.unicam.cs.mpgc.rpg129548;

import java.util.Scanner;

public class DuelloConsole {

    public static void main(String[] args) {
        DuelloGoldrake partita = new DuelloGoldrake(Tappa.LUNA);
        Combattente goldrake = partita.goldrake;
        Combattente nemico = partita.nemico;
        Combattimento duello = partita.combattimento;
        Attacco maglioPerforante = partita.maglioPerforante;
        Attacco alabardaSpaziale = partita.alabardaSpaziale;
        Attacco contrattacco = partita.contrattacco;

        System.out.println("Goldrake RPG - Duello contro il Mostro di Vega");
        try (Scanner terminale = new Scanner(System.in)) {
            while (!duello.isTerminato()) {
                System.out.printf("%nGoldrake: %d PV, %d energia | Mostro di Vega: %d PV%n",
                        goldrake.getPuntiVita(), goldrake.getEnergia(), nemico.getPuntiVita());
                System.out.printf("1. %s (%d danni, %d energia)%n",
                        maglioPerforante.getNome(), maglioPerforante.getDanno(), maglioPerforante.getCostoEnergia());
                System.out.printf("2. %s (%d danni, %d energia)%n",
                        alabardaSpaziale.getNome(), alabardaSpaziale.getDanno(), alabardaSpaziale.getCostoEnergia());
                System.out.print("Scegli una mossa (0 per uscire): ");

                if (!terminale.hasNextLine()) {
                    return;
                }
                Attacco mossa;
                switch (terminale.nextLine().strip()) {
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

                if (!duello.attacca(mossa)) {
                    System.out.println("Energia insufficiente. Scegli un'altra mossa.");
                    continue;
                }
                System.out.printf("Goldrake usa %s. Al nemico restano %d PV.%n",
                        mossa.getNome(), nemico.getPuntiVita());
                if (!duello.isTerminato()) {
                    duello.attacca(contrattacco);
                    System.out.printf("Il nemico usa %s. A Goldrake restano %d PV.%n",
                            contrattacco.getNome(), goldrake.getPuntiVita());
                }
            }
        }

        if (goldrake.isSconfitto()) {
            System.out.println("Sconfitta! Goldrake è stato battuto.");
        } else {
            System.out.println("Vittoria! Goldrake ha sconfitto il Mostro di Vega.");
        }
    }
}
