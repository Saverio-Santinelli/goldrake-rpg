package it.unicam.cs.mpgc.rpg129548;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/** Disegna l'arena e gli sprite dei due combattenti. */
final class Arena extends JComponent {

    private static final int LARGHEZZA = 920;
    private static final int ALTEZZA = 300;
    private static final BufferedImage TAVOLA_SPRITE = caricaImmagine("combattenti.png");
    private static final BufferedImage GOLDRAKE = TAVOLA_SPRITE.getSubimage(55, 0, 665, 887);
    private static final Map<Tappa, Scena> SCENE = Map.of(
            Tappa.VALLE, new Scena(caricaImmagine("valle-montana.png"), caricaImmagine("esploratore.png")),
            Tappa.CITTA, new Scena(caricaImmagine("citta-notturna.png"), caricaImmagine("guardiano.png")),
            Tappa.LUNA, new Scena(caricaImmagine("base-lunare-vega.png"),
                    TAVOLA_SPRITE.getSubimage(870, 0, 904, 887)));
    private Tappa tappa = Tappa.VALLE;
    private boolean goldrakeSconfitto;
    private boolean nemicoSconfitto;

    Arena() {
        setPreferredSize(new Dimension(LARGHEZZA, ALTEZZA));
        setMinimumSize(new Dimension(200, 150));
    }

    void aggiorna(Tappa tappa, boolean goldrakeSconfitto, boolean nemicoSconfitto) {
        this.tappa = tappa;
        this.goldrakeSconfitto = goldrakeSconfitto;
        this.nemicoSconfitto = nemicoSconfitto;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grafica) {
        Graphics2D g = (Graphics2D) grafica.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setColor(new Color(0x0B0E1B));
            g.fillRect(0, 0, getWidth(), getHeight());
            double scala = Math.min((double) getWidth() / LARGHEZZA, (double) getHeight() / ALTEZZA);
            g.translate((getWidth() - LARGHEZZA * scala) / 2, (getHeight() - ALTEZZA * scala) / 2);
            g.scale(scala, scala);
            Scena scena = SCENE.get(tappa);
            g.drawImage(scena.sfondo(), 0, 0, LARGHEZZA, ALTEZZA, null);
            disegnaCombattente(g, GOLDRAKE, 245, goldrakeSconfitto);
            disegnaCombattente(g, scena.nemico(), 675, nemicoSconfitto);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            scriviAlCentro(g, tappa.luogo.toUpperCase(Locale.ROOT), 30);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            scriviAlCentro(g, "VS", 145);
        } finally {
            g.dispose();
        }
    }

    private static void scriviAlCentro(Graphics2D g, String testo, int y) {
        var misure = g.getFontMetrics();
        int larghezza = misure.stringWidth(testo);
        int x = (LARGHEZZA - larghezza) / 2;
        g.setColor(new Color(8, 12, 24, 200));
        g.fillRoundRect(x - 10, y - misure.getAscent() - 4,
                larghezza + 20, misure.getHeight() + 8, 6, 6);
        g.setColor(new Color(0xEEF4FF));
        g.drawString(testo, x, y);
    }

    private static void disegnaCombattente(Graphics2D sfondo, BufferedImage sprite,
                                          int centro, boolean sconfitto) {
        Graphics2D g = (Graphics2D) sfondo.create();
        try {
            g.translate(centro, 0);
            int altezza = 250;
            int larghezza = sprite.getWidth() * altezza / sprite.getHeight();
            disegnaOmbra(g, larghezza / 2);
            if (sconfitto) {
                g.rotate(centro > LARGHEZZA / 2 ? 0.18 : -0.18, 0, 263);
                g.setComposite(AlphaComposite.SrcOver.derive(0.45f));
            }
            g.drawImage(sprite, -larghezza / 2, 263 - altezza, larghezza, altezza, null);
        } finally {
            g.dispose();
        }
    }

    private static void disegnaOmbra(Graphics2D grafica, int raggio) {
        Graphics2D g = (Graphics2D) grafica.create();
        try {
            g.translate(0, 262);
            g.scale(1, 12.0 / raggio);
            g.setPaint(new RadialGradientPaint(0, 0, raggio, new float[]{0, 1},
                    new Color[]{new Color(8, 11, 22, 180), new Color(8, 11, 22, 0)}));
            g.fillOval(-raggio, -raggio, raggio * 2, raggio * 2);
        } finally {
            g.dispose();
        }
    }

    private static BufferedImage caricaImmagine(String nome) {
        String percorso = "/arena/" + nome;
        try (var risorsa = Arena.class.getResourceAsStream(percorso)) {
            if (risorsa == null) {
                throw new IOException("Risorsa mancante: " + percorso);
            }
            BufferedImage immagine = ImageIO.read(risorsa);
            if (immagine == null) {
                throw new IOException("Formato immagine non valido: " + percorso);
            }
            return immagine;
        } catch (IOException errore) {
            throw new UncheckedIOException("Impossibile caricare l'immagine dell'arena", errore);
        }
    }

    private record Scena(BufferedImage sfondo, BufferedImage nemico) {
    }
}
