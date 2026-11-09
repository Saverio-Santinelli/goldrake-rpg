package it.unicam.cs.mpgc.rpg129548;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicProgressBarUI;

public final class DuelloGrafico {

    private static final Color SFONDO = new Color(0x0D1424);
    private static final Color SUPERFICIE = new Color(0x17233A);
    private static final Color TESTO = new Color(0xEEF4FF);
    private static final Color TESTO_SECONDARIO = new Color(0xA9BBDC);
    private static final Color AZZURRO = new Color(0x65C8FF);
    private static final Color VIOLA = new Color(0xB397F8);

    private DuelloGoldrake partita = new DuelloGoldrake();
    private final Arena arena = new Arena();
    private final JLabel esito = etichetta("Il tuo turno", 14, AZZURRO);
    private final JProgressBar vitaGoldrake = barra(AZZURRO);
    private final JProgressBar energiaGoldrake = barra(new Color(0xF5C66B));
    private final JProgressBar vitaNemico = barra(VIOLA);
    private final JTextArea cronaca = new JTextArea(4, 44);
    private final JButton pugno = new JButton(descrivi(partita.pugno));
    private final JButton raggio = new JButton(descrivi(partita.raggio));
    private final JButton salva = new JButton("Salva partita");
    private final SalvataggioPartita archivio = new SalvataggioPartita(
            Path.of(System.getProperty("user.home"), ".goldrake-rpg", "partita.properties"));

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DuelloGrafico().mostra());
    }

    private void mostra() {
        JFrame finestra = new JFrame("Goldrake RPG");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contenuto = pannello(new BorderLayout(0, 14));
        contenuto.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JPanel intestazione = pannello(new BorderLayout(0, 12));
        JPanel titolo = pannello(new BorderLayout());
        titolo.add(etichetta("Goldrake RPG", 26, TESTO), BorderLayout.WEST);
        titolo.add(esito, BorderLayout.EAST);
        intestazione.add(titolo, BorderLayout.NORTH);
        JPanel stato = pannello(new GridLayout(1, 2, 14, 0));
        stato.add(scheda("Goldrake", AZZURRO, vitaGoldrake, energiaGoldrake));
        stato.add(scheda("Mostro di Vega", VIOLA, vitaNemico));
        intestazione.add(stato, BorderLayout.CENTER);
        contenuto.add(intestazione, BorderLayout.NORTH);
        contenuto.add(arena, BorderLayout.CENTER);

        JButton nuovaPartita = new JButton("Nuova partita");
        JButton carica = new JButton("Carica partita");
        pugno.addActionListener(evento -> attacca(partita.pugno));
        raggio.addActionListener(evento -> attacca(partita.raggio));
        nuovaPartita.addActionListener(evento -> iniziaPartita());
        salva.addActionListener(evento -> salvaPartita());
        carica.addActionListener(evento -> caricaPartita());
        JPanel comandi = pannello(new FlowLayout(FlowLayout.LEFT, 6, 0));
        comandi.add(pugno);
        comandi.add(raggio);
        JPanel gestionePartita = pannello(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        gestionePartita.add(nuovaPartita);
        gestionePartita.add(salva);
        gestionePartita.add(carica);
        JPanel azioni = pannello(new BorderLayout());
        azioni.add(comandi, BorderLayout.WEST);
        azioni.add(gestionePartita, BorderLayout.EAST);

        JPanel inferiore = pannello(new BorderLayout(0, 10));
        inferiore.add(pannelloCronaca(), BorderLayout.CENTER);
        inferiore.add(azioni, BorderLayout.SOUTH);
        contenuto.add(inferiore, BorderLayout.SOUTH);

        finestra.setContentPane(contenuto);
        cronaca.setText("Il Mostro di Vega ti sfida. Scegli una mossa.\n");
        aggiornaStato();
        finestra.pack();
        finestra.setMinimumSize(new Dimension(800, 545));
        finestra.setLocationRelativeTo(null);
        finestra.setVisible(true);
    }

    private void iniziaPartita() {
        partita = new DuelloGoldrake();
        cronaca.setText("Nuovo duello. Scegli una mossa.\n");
        aggiornaStato();
    }

    private void salvaPartita() {
        try {
            archivio.salva(partita);
            scrivi("Partita salvata.");
        } catch (IOException errore) {
            scrivi("Impossibile salvare la partita. " + errore.getMessage());
        }
    }

    private void caricaPartita() {
        try {
            partita = archivio.carica();
            cronaca.setText("Partita caricata. Scegli una mossa.\n");
            aggiornaStato();
        } catch (NoSuchFileException errore) {
            scrivi("Nessuna partita salvata.");
        } catch (IOException errore) {
            scrivi("Impossibile caricare la partita. " + errore.getMessage());
        }
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
        aggiornaBarra(vitaGoldrake, "Vita", partita.goldrake.getPuntiVita(),
                partita.goldrake.getPuntiVitaMassimi());
        aggiornaBarra(energiaGoldrake, "Energia", partita.goldrake.getEnergia(),
                partita.goldrake.getEnergiaMassima());
        aggiornaBarra(vitaNemico, "Vita", partita.nemico.getPuntiVita(),
                partita.nemico.getPuntiVitaMassimi());
        arena.aggiorna(partita.goldrake.isSconfitto(), partita.nemico.isSconfitto());
        boolean inCorso = !partita.combattimento.isTerminato();
        if (inCorso) {
            esito.setText("Il tuo turno");
        } else {
            esito.setText(partita.goldrake.isSconfitto() ? "Sconfitta" : "Vittoria");
        }
        pugno.setEnabled(inCorso);
        raggio.setEnabled(inCorso);
        salva.setEnabled(inCorso);
    }

    private JPanel pannelloCronaca() {
        JPanel pannello = pannello(new BorderLayout(0, 5));
        pannello.add(etichetta("Cronaca del duello", 12, TESTO_SECONDARIO), BorderLayout.NORTH);
        cronaca.setEditable(false);
        cronaca.setLineWrap(true);
        cronaca.setWrapStyleWord(true);
        cronaca.setBackground(SUPERFICIE);
        cronaca.setForeground(TESTO);
        cronaca.setMargin(new java.awt.Insets(8, 10, 8, 10));
        JScrollPane scorrimento = new JScrollPane(cronaca);
        scorrimento.setBorder(BorderFactory.createEmptyBorder());
        pannello.add(scorrimento, BorderLayout.CENTER);
        return pannello;
    }

    private static JPanel scheda(String nome, Color colore, JProgressBar... barre) {
        JPanel scheda = new JPanel(new BorderLayout(0, 8));
        scheda.setBackground(SUPERFICIE);
        scheda.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        scheda.add(etichetta(nome, 16, colore), BorderLayout.NORTH);
        JPanel indicatori = new JPanel(new GridLayout(2, 1, 0, 5));
        indicatori.setOpaque(false);
        for (JProgressBar barra : barre) {
            indicatori.add(barra);
        }
        if (barre.length == 1) {
            indicatori.add(new JLabel());
        }
        scheda.add(indicatori, BorderLayout.CENTER);
        return scheda;
    }

    private static JPanel pannello(java.awt.LayoutManager layout) {
        JPanel pannello = new JPanel(layout);
        pannello.setBackground(SFONDO);
        return pannello;
    }

    private static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel etichetta = new JLabel(testo);
        etichetta.setFont(etichetta.getFont().deriveFont(Font.BOLD, (float) dimensione));
        etichetta.setForeground(colore);
        return etichetta;
    }

    private static JProgressBar barra(Color colore) {
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

    private static void aggiornaBarra(JProgressBar barra, String nome, int valore, int massimo) {
        barra.setMaximum(massimo);
        barra.setValue(valore);
        barra.setString("%s  %d / %d".formatted(nome, valore, massimo));
        barra.getAccessibleContext().setAccessibleName(nome);
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
