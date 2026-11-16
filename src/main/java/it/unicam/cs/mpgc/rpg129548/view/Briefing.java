package it.unicam.cs.mpgc.rpg129548.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/** Manuale del pilota, consultabile per capitoli dal menu principale. */
final class Briefing extends JPanel {

    private static final Color SFONDO = new Color(0x0D1424);
    private static final Color CARTA = new Color(0x17243A);
    private static final Color AZZURRO = new Color(0x79DCFF);
    private static final Color ORO = new Color(0xF5C66B);
    private static final Color CHIARO = new Color(0xEEF4FF);

    private static final Capitolo[] CAPITOLI = {
        new Capitolo("Primi passi", "La missione di Goldrake", """
                <p>Il tuo obiettivo è sconfiggere i tre boss di Vega. Seleziona <b>Nuova partita</b>
                per partire dalla valle montana con <b>100 punti vita</b> e <b>30 energia</b>.</p>
                <h2>Come leggere la schermata</h2>
                <p><b>In alto</b> trovi la tappa, la vita dei due combattenti e l’energia di Goldrake.
                Un avviso compare quando il boss carica una mossa speciale.</p>
                <p><b>In basso</b> scegli una mossa e leggi nella cronaca cosa è successo.
                Il boss risponde automaticamente, a meno che stia caricando o sia stato sconfitto.</p>
                <h2>Prova il primo turno</h2>
                <p>Usa <b>Maglio Perforante</b>. L’Esploratore scende da 55 a 45 punti vita,
                Goldrake subisce 9 danni e conserva tutta l’energia.</p>
                """),
        new Capitolo("Mosse e Guardia", "Scegli la mossa del turno", """
                <p><b>Maglio Perforante</b> · 10 danni · 0 energia<br>
                Usalo per conservare energia o finire un boss con poca vita.</p>
                <p><b>Alabarda Spaziale</b> · 30 danni · 20 energia<br>
                È adatta per colpire forte o chiudere il duello prima di una risposta del boss.</p>
                <p><b>Guardia</b> · recupera fino a 10 energia<br>
                Consuma il turno e dimezza il danno in arrivo, arrotondando il danno subìto per eccesso.
                Se il boss sta caricando, puoi recuperare energia senza subire un colpo.</p>
                <h2>Quando manca energia</h2>
                <p>Un attacco troppo costoso non parte e non spreca il turno.
                Scegli Maglio Perforante oppure Guardia.</p>
                """),
        new Capitolo("Boss e allarmi", "Riconosci l’attacco in arrivo", """
                <p>Quando il boss carica, lascia passare il suo turno. L’avviso indica il colpo
                che sferrerà dopo la tua prossima mossa.</p>
                <p><b>Valle montana</b> · Esploratore di Vega<br>
                Vortice di Lame: 20 danni, 10 con Guardia.</p>
                <p><b>Città di notte</b> · Guardiano di Vega<br>
                Bombardamento Protonico: 26 danni, 13 con Guardia.</p>
                <p><b>Base lunare</b> · Mostro di Vega<br>
                Schianto Selenico: 32 danni, 16 con Guardia.</p>
                <h2>Difenditi o chiudi il duello</h2>
                <p>Se puoi azzerare subito la vita del boss, attacca e interrompi la sua mossa.
                Altrimenti usa Guardia per limitare il danno.</p>
                """),
        new Capitolo("Campagna e tecniche", "Avanza dopo ogni vittoria", """
                <p><b>Dopo la valle</b> premi <b>Scegli tecnica</b>. La scelta apre il duello in città
                e salva automaticamente la partita, sostituendo il salvataggio precedente.</p>
                <p><b>Tuono Spaziale</b> · 45 danni · 30 energia<br>
                Un colpo decisivo che richiede tutta l’energia iniziale.</p>
                <p><b>Raggio Antigravità</b> · 18 danni · 10 energia<br>
                Un attacco meno costoso, più facile da usare più volte.</p>
                <p><b>Dopo la città</b> premi <b>Prossimo duello</b> per andare sulla Luna.
                A ogni nuova tappa recuperi tutta la vita e l’energia e conservi la tecnica scelta.</p>
                <p><b>Se perdi</b>, <b>Riprova duello</b> riavvia la stessa tappa a risorse piene.
                Vincere sulla Luna conclude la campagna.</p>
                """),
        new Capitolo("Salva e riprendi", "Riprendi da dove vuoi", """
                <p><b>Salva partita</b> registra la tappa, la vita dei due combattenti, l’energia
                di Goldrake, la tecnica scelta e l’eventuale attacco annunciato. Puoi salvare tra
                un turno e l’altro mentre il duello è in corso.</p>
                <p><b>Menu → Torna al duello</b> riprende il duello aperto, con tutte le mosse fatte
                da quando hai avviato il gioco.</p>
                <p><b>Carica salvataggio</b> ripristina l’ultimo punto registrato sul disco.
                Puoi usarlo anche dopo aver chiuso e riaperto il gioco.</p>
                <p>Se esci, inizi una nuova partita o carichi con progressi non salvati,
                il gioco chiede conferma. Premi <b>Annulla</b> per tenere il duello corrente.</p>
                """)
    };

    private final JEditorPane pagina = new JEditorPane("text/html", "");
    private final JToggleButton[] sezioni = new JToggleButton[CAPITOLI.length];

    Briefing(Runnable tornaAlMenu) {
        super(new BorderLayout(18, 16));
        setBackground(SFONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        add(intestazione(), BorderLayout.NORTH);
        add(indice(), BorderLayout.WEST);

        pagina.setEditable(false);
        pagina.setBackground(CARTA);
        pagina.getAccessibleContext().setAccessibleName("Capitolo della guida");
        JScrollPane lettura = new JScrollPane(pagina);
        lettura.setBorder(BorderFactory.createLineBorder(new Color(0x334765)));
        lettura.setPreferredSize(new Dimension(500, 310));
        lettura.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lettura.getVerticalScrollBar().setUnitIncrement(20);
        add(lettura, BorderLayout.CENTER);

        JButton indietro = new PulsanteArcade("Torna al menu", AZZURRO);
        indietro.addActionListener(evento -> tornaAlMenu.run());
        JPanel comandi = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comandi.setOpaque(false);
        comandi.add(indietro);
        add(comandi, BorderLayout.SOUTH);
        mostraCapitolo(0);
    }

    private static JPanel intestazione() {
        Image immagine = new ImageIcon(Briefing.class.getResource("/menu/goldrake-in-volo.png")).getImage();
        JPanel fascia = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics grafica) {
                super.paintComponent(grafica);
                Graphics2D g = (Graphics2D) grafica.create();
                try {
                    int larghezza = Math.min(getWidth(), 330);
                    int inizio = getWidth() - larghezza;
                    int altezza = larghezza * immagine.getHeight(this) / immagine.getWidth(this);
                    g.drawImage(immagine, inizio, (getHeight() - altezza) / 2, larghezza, altezza, this);
                    g.setPaint(new GradientPaint(inizio, 0, CARTA, getWidth() - 1, 0, new Color(0x2017243A, true)));
                    g.fillRect(inizio, 0, larghezza, getHeight());
                } finally {
                    g.dispose();
                }
            }
        };
        fascia.setBackground(CARTA);
        fascia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 1, 0, ORO),
                BorderFactory.createEmptyBorder(10, 20, 12, 20)));
        fascia.setPreferredSize(new Dimension(0, 96));
        fascia.add(etichetta("GOLDRAKE RPG  /  MANUALE DEL PILOTA", 13, ORO));
        fascia.add(etichetta("Come si gioca", 27, CHIARO));
        return fascia;
    }

    private JPanel indice() {
        JPanel pannello = new JPanel(new BorderLayout(0, 10));
        pannello.setOpaque(false);
        pannello.add(etichetta("CAPITOLI", 12, AZZURRO), BorderLayout.NORTH);
        JPanel pulsanti = new JPanel(new GridLayout(0, 1, 0, 6));
        pulsanti.setOpaque(false);
        ButtonGroup gruppo = new ButtonGroup();
        for (int i = 0; i < sezioni.length; i++) {
            int indice = i;
            JToggleButton pulsante = new JToggleButton(CAPITOLI[i].voce());
            pulsante.setHorizontalAlignment(SwingConstants.LEFT);
            pulsante.setPreferredSize(new Dimension(188, 36));
            pulsante.setContentAreaFilled(false);
            pulsante.setOpaque(true);
            pulsante.setFont(pulsante.getFont().deriveFont(Font.BOLD, 14f));
            pulsante.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x334765)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 8)));
            pulsante.addActionListener(evento -> mostraCapitolo(indice));
            sezioni[i] = pulsante;
            gruppo.add(pulsante);
            pulsanti.add(pulsante);
        }
        JPanel elenco = new JPanel(new BorderLayout());
        elenco.setOpaque(false);
        elenco.add(pulsanti, BorderLayout.NORTH);
        pannello.add(elenco, BorderLayout.CENTER);
        return pannello;
    }

    private void mostraCapitolo(int indice) {
        sezioni[indice].setSelected(true);
        for (JToggleButton sezione : sezioni) {
            sezione.setBackground(sezione.isSelected() ? AZZURRO : CARTA);
            sezione.setForeground(sezione.isSelected() ? SFONDO : CHIARO);
        }
        Capitolo capitolo = CAPITOLI[indice];
        pagina.setText("""
                <html><head><style>
                body { background-color:#17243a; color:#eef4ff; font-family:sans-serif;
                       font-size:14pt; margin:18px 22px; }
                h1 { color:#79dcff; font-size:22pt; margin-top:0; margin-bottom:12px; }
                h2 { color:#f5c66b; font-size:15pt; margin-top:14px; margin-bottom:6px; }
                p { margin-top:0; margin-bottom:10px; }
                </style></head><body><h1>%s</h1>%s</body></html>
                """.formatted(capitolo.titolo(), capitolo.testo()));
        pagina.setCaretPosition(0);
    }

    private static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(Font.BOLD, (float) dimensione));
        etichetta.setForeground(colore);
        return etichetta;
    }

    private record Capitolo(String voce, String titolo, String testo) {
    }
}
