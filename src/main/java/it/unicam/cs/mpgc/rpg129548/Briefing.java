package it.unicam.cs.mpgc.rpg129548;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

final class Briefing extends JPanel {
    private static final Color AZZURRO = new Color(120, 222, 255);

    Briefing(Runnable tornaAlMenu) {
        super(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(34, 40, 30, 40));

        JPanel intestazione = new JPanel(new GridLayout(0, 1, 0, 7));
        intestazione.setOpaque(false);
        intestazione.add(etichetta("BRIEFING DEL DOTTOR PROCTON", 28, Color.WHITE));
        intestazione.add(etichetta("«Actarus, quando Vega prepara il colpo, mettiti in guardia.»",
                16, AZZURRO));
        add(intestazione, BorderLayout.NORTH);

        JPanel schede = new JPanel(new GridLayout(0, 1, 0, 10));
        schede.setOpaque(false);
        schede.add(scheda("Armi ed energia",
                "Maglio Perforante: 10 danni, 0 energia. Alabarda Spaziale: 30 danni, 20 energia."));
        schede.add(scheda("Allarme Vega",
                "Guardia usa il turno, dimezza il danno (arrotondato per eccesso) e recupera fino a 10 energia.<br>"
                + "Contro lo Schianto Selenico passi da 32 a 16 danni."));
        schede.add(scheda("Riprendi la missione",
                "Torna al duello riprende la partita aperta. Carica salvataggio riparte dall’ultimo salvataggio."));
        add(schede, BorderLayout.CENTER);

        JButton indietro = new PulsanteArcade("Torna al menu", AZZURRO);
        indietro.addActionListener(evento -> tornaAlMenu.run());
        JPanel comandi = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        comandi.setOpaque(false);
        comandi.add(indietro);
        add(comandi, BorderLayout.SOUTH);
    }

    private static JPanel scheda(String titolo, String testo) {
        JPanel scheda = new JPanel(new BorderLayout(0, 4));
        scheda.setBackground(new Color(12, 28, 51, 234));
        scheda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 3, 1, 1, new Color(50, 105, 149)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        scheda.add(etichetta(titolo, 17, AZZURRO), BorderLayout.NORTH);
        scheda.add(etichetta("<html>" + testo + "</html>", 14, Color.WHITE), BorderLayout.CENTER);
        return scheda;
    }

    private static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(dimensione >= 17 ? Font.BOLD : Font.PLAIN,
                (float) dimensione));
        etichetta.setForeground(colore);
        return etichetta;
    }

    @Override
    protected void paintComponent(Graphics grafica) {
        super.paintComponent(grafica);
        Graphics2D g = (Graphics2D) grafica.create();
        int w = getWidth();
        int h = getHeight();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, new Color(3, 8, 23), 0, h, new Color(14, 39, 70)));
        g.fillRect(0, 0, w, h);
        Random stelle = new Random(48);
        g.setColor(new Color(165, 201, 232));
        for (int i = 0; i < 125; i++) {
            g.fillOval((int) (stelle.nextDouble() * w), (int) (stelle.nextDouble() * h), 2, 2);
        }
        g.setColor(new Color(163, 184, 205));
        g.fillOval(w - 91, 18, 42, 42);
        g.setPaint(new GradientPaint(0, h - 90, new Color(63, 132, 187), 0, h, new Color(7, 27, 50)));
        g.fillOval(-w / 5, h - 86, w * 7 / 5, 240);
        g.setColor(new Color(32, 63, 91));
        g.fillPolygon(new int[]{0, 25, 64, 0}, new int[]{0, 0, h, h}, 4);
        g.fillPolygon(new int[]{w, w - 25, w - 64, w}, new int[]{0, 0, h, h}, 4);
        g.setColor(new Color(225, 68, 77));
        g.setStroke(new BasicStroke(3));
        g.drawPolyline(new int[]{w / 2 - 16, w / 2, w / 2 + 16},
                new int[]{h - 18, h - 10, h - 18}, 3);
        g.dispose();
    }
}
