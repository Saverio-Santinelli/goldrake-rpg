package it.unicam.cs.mpgc.rpg129548.view;

import static it.unicam.cs.mpgc.rpg129548.view.StileGioco.*;
import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Visualizza i messaggi del duello mantenendo visibile l'ultima azione. */
final class CronacaDuello extends JPanel {
    private final JTextArea testo = new JTextArea(4, 44);

    CronacaDuello() {
        super(new BorderLayout(0, 5));
        setBackground(SFONDO);
        add(etichetta("Cronaca del duello", 12, TESTO_SECONDARIO), BorderLayout.NORTH);
        testo.setEditable(false);
        testo.setLineWrap(true);
        testo.setWrapStyleWord(true);
        testo.setBackground(SUPERFICIE);
        testo.setForeground(TESTO);
        testo.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane scorrimento = new JScrollPane(testo);
        scorrimento.setBorder(BorderFactory.createEmptyBorder());
        add(scorrimento, BorderLayout.CENTER);
    }

    void impostaTesto(String messaggio) {
        testo.setText(messaggio);
    }

    void scrivi(String messaggio) {
        testo.append(messaggio + "\n");
        testo.setCaretPosition(testo.getDocument().getLength());
    }
}
