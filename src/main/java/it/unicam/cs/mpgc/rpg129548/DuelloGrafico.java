package it.unicam.cs.mpgc.rpg129548;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public final class DuelloGrafico {

    private DuelloGoldrake partita = new DuelloGoldrake();
    private final JLabel statoGoldrake = new JLabel();
    private final JLabel statoNemico = new JLabel();
    private final JTextArea cronaca = new JTextArea(9, 44);
    private final JButton pugno = new JButton(descrivi(partita.pugno));
    private final JButton raggio = new JButton(descrivi(partita.raggio));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DuelloGrafico().mostra());
    }

    private void mostra() {
        JFrame finestra = new JFrame("Goldrake RPG");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contenuto = new JPanel(new BorderLayout(0, 12));
        contenuto.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JPanel stato = new JPanel(new GridLayout(2, 1, 0, 4));
        stato.add(statoGoldrake);
        stato.add(statoNemico);
        contenuto.add(stato, BorderLayout.NORTH);

        cronaca.setEditable(false);
        cronaca.setLineWrap(true);
        cronaca.setWrapStyleWord(true);
        contenuto.add(new JScrollPane(cronaca), BorderLayout.CENTER);

        JButton nuovaPartita = new JButton("Nuova partita");
        pugno.addActionListener(evento -> attacca(partita.pugno));
        raggio.addActionListener(evento -> attacca(partita.raggio));
        nuovaPartita.addActionListener(evento -> iniziaPartita());
        JPanel comandi = new JPanel();
        comandi.add(pugno);
        comandi.add(raggio);
        comandi.add(nuovaPartita);
        contenuto.add(comandi, BorderLayout.SOUTH);

        finestra.setContentPane(contenuto);
        cronaca.setText("Il Mostro di Vega ti sfida. Scegli una mossa.\n");
        aggiornaStato();
        finestra.pack();
        finestra.setLocationRelativeTo(null);
        finestra.setVisible(true);
    }

    private void iniziaPartita() {
        partita = new DuelloGoldrake();
        cronaca.setText("Nuovo duello. Scegli una mossa.\n");
        aggiornaStato();
    }

    private void attacca(Attacco mossa) {
        Combattimento duello = partita.combattimento;
        if (!duello.attacca(mossa)) {
            scrivi("Energia insufficiente. Scegli un'altra mossa.");
            return;
        }
        scrivi("Goldrake usa " + mossa.getNome() + ".");
        if (!duello.isTerminato()) {
            duello.attacca(partita.artiglio);
            scrivi("Il Mostro di Vega contrattacca con Artiglio.");
        }
        if (duello.isTerminato()) {
            scrivi(partita.goldrake.isSconfitto() ? "Sconfitta!" : "Vittoria!");
        }
        aggiornaStato();
    }

    private void aggiornaStato() {
        statoGoldrake.setText("Goldrake: %d PV | Energia: %d".formatted(
                partita.goldrake.getPuntiVita(), partita.goldrake.getEnergia()));
        statoNemico.setText("Mostro di Vega: %d PV".formatted(partita.nemico.getPuntiVita()));
        boolean inCorso = !partita.combattimento.isTerminato();
        pugno.setEnabled(inCorso);
        raggio.setEnabled(inCorso);
    }

    private void scrivi(String messaggio) {
        cronaca.append(messaggio + "\n");
        cronaca.setCaretPosition(cronaca.getDocument().getLength());
    }

    private static String descrivi(Attacco attacco) {
        return "%s (%d danni, %d energia)".formatted(
                attacco.getNome(), attacco.getDanno(), attacco.getCostoEnergia());
    }
}
