package it.unicam.cs.mpgc.rpg129548.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class FinaleCampagna extends JPanel {

    private static final Color ORO = new Color(0xF5C66B);
    private static final Color TESTO = new Color(0xEEF4FF);

    private final Image sfondo = new ImageIcon(Objects.requireNonNull(
            FinaleCampagna.class.getResource("/menu/goldrake-in-volo.png"))).getImage();

    FinaleCampagna(Runnable tornaAlMenu, Runnable mostraStatistiche) {
        super(new GridBagLayout());
        setBackground(new Color(0x0D1424));
        setBorder(BorderFactory.createEmptyBorder(32, 42, 32, 42));

        JPanel colonna = new JPanel();
        colonna.setOpaque(false);
        colonna.setLayout(new BoxLayout(colonna, BoxLayout.Y_AXIS));
        colonna.add(testo("GOLDRAKE RPG  /  CAMPAGNA COMPLETATA", 14, ORO));
        colonna.add(Box.createVerticalStrut(10));
        colonna.add(testo("La Terra è salva", 46, TESTO));
        colonna.add(Box.createVerticalStrut(16));
        colonna.add(testo("Tre duelli vinti. L’ultimo mostro di Vega è caduto.", 17, TESTO));
        colonna.add(Box.createVerticalStrut(30));

        var menu = new PulsanteArcade("Torna al menu", ORO);
        menu.addActionListener(evento -> tornaAlMenu.run());
        JPanel pulsanti = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pulsanti.setOpaque(false);
        var statistiche = new PulsanteArcade("Statistiche della campagna", new Color(0x79DCFF));
        statistiche.addActionListener(evento -> mostraStatistiche.run());
        pulsanti.add(statistiche);
        pulsanti.add(Box.createHorizontalStrut(12));
        pulsanti.add(menu);
        JPanel contenuto = new JPanel(new BorderLayout(0, 22));
        contenuto.setOpaque(false);
        contenuto.add(colonna, BorderLayout.NORTH);
        contenuto.add(pulsanti, BorderLayout.SOUTH);

        GridBagConstraints posizione = new GridBagConstraints();
        posizione.weightx = 1;
        posizione.anchor = GridBagConstraints.WEST;
        add(contenuto, posizione);
    }

    private static JLabel testo(String valore, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(valore);
        etichetta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, dimensione));
        etichetta.setForeground(colore);
        return etichetta;
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
            g.drawImage(sfondo, getWidth() - larghezza, (getHeight() - altezza) / 2, larghezza, altezza, this);
            g.setPaint(new GradientPaint(0, 0, new Color(8, 12, 25, 250),
                    Math.max(1, getWidth() * 0.8f), 0, new Color(8, 12, 25, 45)));
            g.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g.dispose();
        }
    }
}
