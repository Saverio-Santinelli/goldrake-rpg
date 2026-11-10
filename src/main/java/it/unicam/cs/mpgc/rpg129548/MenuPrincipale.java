package it.unicam.cs.mpgc.rpg129548;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/** Schermata iniziale della campagna. */
final class MenuPrincipale extends JPanel {

    private static final Color ORO = new Color(0xF5C66B);
    private static final Color AZZURRO = new Color(0x65C8FF);
    private static final Color ACCIAIO = new Color(0x8499B6);
    private static final Color CHIARO = new Color(0xEEF4FF);

    private final Image sfondo = new ImageIcon(Objects.requireNonNull(
            MenuPrincipale.class.getResource("/menu/goldrake-in-volo.png"))).getImage();
    private final JTextArea messaggio = new JTextArea(2, 22);
    private final JButton tornaAlDuello;

    MenuPrincipale(Runnable nuova, Runnable carica, Runnable torna, Runnable esci) {
        super(new GridBagLayout());
        setBackground(new Color(0x0D1424));

        JPanel colonna = new JPanel();
        colonna.setOpaque(false);
        colonna.setLayout(new BoxLayout(colonna, BoxLayout.Y_AXIS));
        colonna.add(etichetta("UFO ROBOT", 15, AZZURRO));
        colonna.add(etichetta("GOLDRAKE", 54, CHIARO));
        colonna.add(etichetta("RPG  /  CAMPAGNA A TURNI", 15, ORO));
        colonna.add(Box.createVerticalStrut(30));
        tornaAlDuello = aggiungiPulsante(colonna, "Torna al duello", ORO, torna);
        tornaAlDuello.setVisible(false);
        aggiungiPulsante(colonna, "Nuova partita", ORO, nuova);
        aggiungiPulsante(colonna, "Carica salvataggio", AZZURRO, carica);
        aggiungiPulsante(colonna, "Esci", ACCIAIO, esci);

        messaggio.setEditable(false);
        messaggio.setFocusable(false);
        messaggio.setOpaque(false);
        messaggio.setForeground(CHIARO);
        messaggio.setLineWrap(true);
        messaggio.setWrapStyleWord(true);
        messaggio.setAlignmentX(LEFT_ALIGNMENT);
        messaggio.getAccessibleContext().setAccessibleName("Messaggio del menu");
        colonna.add(messaggio);

        GridBagConstraints posizione = new GridBagConstraints();
        posizione.weightx = 1;
        posizione.anchor = GridBagConstraints.WEST;
        posizione.insets = new Insets(20, 48, 20, 20);
        add(colonna, posizione);
    }

    void abilitaRitornoAlDuello() {
        tornaAlDuello.setVisible(true);
        mostraMessaggio("");
    }

    void mostraMessaggio(String testo) {
        messaggio.setText(testo);
    }

    @Override
    protected void paintComponent(Graphics grafica) {
        super.paintComponent(grafica);
        Graphics2D g = (Graphics2D) grafica.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            double scala = Math.max((double) getWidth() / sfondo.getWidth(this),
                    (double) getHeight() / sfondo.getHeight(this));
            int larghezza = (int) Math.ceil(sfondo.getWidth(this) * scala);
            int altezza = (int) Math.ceil(sfondo.getHeight(this) * scala);
            g.drawImage(sfondo, getWidth() - larghezza, 0, larghezza, altezza, this);
            g.setPaint(new GradientPaint(0, 0, new Color(8, 12, 25, 225),
                    getWidth() * 0.57f, 0, new Color(8, 12, 25, 0)));
            g.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g.dispose();
        }
    }

    private static JButton aggiungiPulsante(JPanel colonna, String testo, Color accento, Runnable azione) {
        JButton pulsante = new PulsanteArcade(testo, accento);
        pulsante.setFont(pulsante.getFont().deriveFont(15f));
        pulsante.setAlignmentX(LEFT_ALIGNMENT);
        Dimension misura = new Dimension(248, 42);
        pulsante.setPreferredSize(misura);
        pulsante.setMinimumSize(misura);
        pulsante.setMaximumSize(misura);
        pulsante.addActionListener(evento -> azione.run());
        colonna.add(pulsante);
        colonna.add(Box.createVerticalStrut(10));
        return pulsante;
    }

    private static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setForeground(colore);
        etichetta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, dimensione));
        etichetta.setAlignmentX(LEFT_ALIGNMENT);
        return etichetta;
    }
}
