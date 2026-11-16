package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.model.StatisticheDuello;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class RiepilogoCampagna extends JPanel {

    private static final Color AZZURRO = new Color(0x79DCFF);
    private static final Color ORO = new Color(0xF5C66B);
    private static final Color TESTO = new Color(0xEEF4FF);
    private final JPanel schede = new JPanel(new GridLayout(0, 1, 0, 10));

    RiepilogoCampagna(Runnable tornaAlFinale) {
        super(new BorderLayout(0, 14));
        setBackground(new Color(0x0D1424));
        setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel intestazione = new JPanel(new GridLayout(0, 1, 0, 4));
        intestazione.setOpaque(false);
        intestazione.add(etichetta("Statistiche della campagna", 28, AZZURRO, Font.BOLD));
        intestazione.add(etichetta("Per ogni tappa sono mostrati i dati del tentativo vinto.", 14, TESTO, Font.PLAIN));
        add(intestazione, BorderLayout.NORTH);

        schede.setOpaque(false);
        add(schede, BorderLayout.CENTER);

        var indietro = new PulsanteArcade("Torna al finale", ORO);
        indietro.addActionListener(evento -> tornaAlFinale.run());
        JPanel comandi = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comandi.setOpaque(false);
        comandi.add(indietro);
        add(comandi, BorderLayout.SOUTH);
    }

    void aggiorna(Map<Tappa, StatisticheDuello> statistiche) {
        schede.removeAll();
        for (Tappa tappa : Tappa.values()) {
            StatisticheDuello dati = statistiche.get(tappa);
            JPanel scheda = new JPanel(new GridLayout(0, 1, 0, 2));
            scheda.setBackground(new Color(0x17243A));
            scheda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, AZZURRO),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            scheda.add(etichetta(tappa.luogo(), 17, ORO, Font.BOLD));
            if (dati == null) {
                scheda.add(etichetta("Statistiche assenti nel salvataggio precedente.", 14, TESTO, Font.PLAIN));
            } else {
                scheda.add(etichetta("Danni inflitti: %d   /   Danni subiti: %d".formatted(dati.inflitti(),
                        dati.subiti()), 15, TESTO, Font.BOLD));
                String prefisso = dati.conteggioCompleto() ? "" : "Dal caricamento: ";
                scheda.add(etichetta(prefisso + "Maglio ×%d   ·   Alabarda ×%d   ·   Tecnica premio ×%d   ·   Guardia ×%d"
                        .formatted(dati.magli(), dati.alabarde(), dati.tecniche(), dati.guardie()),
                        14, TESTO, Font.PLAIN));
            }
            schede.add(scheda);
        }
        schede.revalidate();
        schede.repaint();
    }

    private static JLabel etichetta(String testo, int dimensione, Color colore, int stile) {
        var etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(stile, (float) dimensione));
        etichetta.setForeground(colore);
        return etichetta;
    }
}
