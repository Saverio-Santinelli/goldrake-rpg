package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.model.Mossa;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;



/** Colori e componenti comuni delle schermate del duello. */
public final class StileGioco {
    private StileGioco() { }
    public static final Color SFONDO = new Color(0x0D1424);
    public static final Color SUPERFICIE = new Color(0x17233A);
    public static final Color TESTO = new Color(0xEEF4FF);
    public static final Color TESTO_SECONDARIO = new Color(0xA9BBDC);
    public static final Color AZZURRO = new Color(0x65C8FF);
    public static final Color VIOLA = new Color(0xB397F8);
    public static final Color ROSSO = new Color(0xFF727C);
    public static final Color ORO = new Color(0xF5C66B);
    public static final Color ACCIAIO = new Color(0x8499B6);



    public static JPanel pannello(java.awt.LayoutManager layout) {
        JPanel pannello = new JPanel(layout);
        pannello.setBackground(SFONDO);
        return pannello;
    }

    public static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(Font.BOLD, (float) dimensione));
        etichetta.setForeground(colore);
        return etichetta;
    }

    public static JProgressBar barra(Color colore) {
        JProgressBar barra = new JProgressBar();
        barra.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return SFONDO;
            }

            @Override
            protected Color getSelectionBackground() {
                return TESTO;
            }
        });
        barra.setPreferredSize(new Dimension(200, 19));
        barra.setBackground(new Color(0x263652));
        barra.setForeground(colore);
        barra.setBorderPainted(false);
        barra.setStringPainted(true);
        return barra;
    }

    public static void aggiornaBarra(JProgressBar barra, String nome, int valore, int massimo) {
        barra.setMaximum(massimo);
        barra.setValue(valore);
        barra.setString("%s  %d / %d".formatted(nome, valore, massimo));
        barra.getAccessibleContext().setAccessibleName(nome);
    }

    public static String descrivi(Mossa attacco) {
        return "%s · %d danni / %d EN".formatted(
                attacco.getNome(), attacco.getDanno(), attacco.getCostoEnergia());
    }
}