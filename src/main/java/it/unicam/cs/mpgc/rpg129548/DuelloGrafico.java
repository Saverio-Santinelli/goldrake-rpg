package it.unicam.cs.mpgc.rpg129548;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private static final Color ROSSO = new Color(0xFF727C);
    private static final Color ORO = new Color(0xF5C66B);
    private static final Color ACCIAIO = new Color(0x8499B6);

    private DuelloGoldrake partita = new DuelloGoldrake(Tappa.VALLE);
    private final Arena arena = new Arena();
    private final JLabel esito = etichetta("Il tuo turno", 14, AZZURRO);
    private final JLabel avvisoSpeciale = etichetta("", 13, ORO);
    private final JLabel nomeNemico = etichetta("", 16, VIOLA);
    private final JProgressBar vitaGoldrake = barra(AZZURRO);
    private final JProgressBar energiaGoldrake = barra(ORO);
    private final JProgressBar vitaNemico = barra(VIOLA);
    private final JTextArea cronaca = new JTextArea(4, 44);
    private final JButton maglioPerforante = new PulsanteArcade(descrivi(partita.maglioPerforante), ROSSO);
    private final JButton alabardaSpaziale = new PulsanteArcade(descrivi(partita.alabardaSpaziale), ORO);
    private final JButton tecnicaPremio = new PulsanteArcade("", VIOLA);
    private final JButton guardia = new PulsanteArcade("Guardia", AZZURRO);
    private final JButton salva = new PulsanteArcade("Salva partita", ACCIAIO);
    private final JButton prosegui = new PulsanteArcade("", ORO);
    private final SalvataggioPartita archivio = new SalvataggioPartita(
            Path.of(System.getProperty("user.home"), ".goldrake-rpg"));
    private MenuPrincipale menu;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DuelloGrafico().mostra());
    }

    private void mostra() {
        JFrame finestra = new JFrame("Goldrake RPG");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout schermate = new CardLayout();
        JPanel radice = new JPanel(schermate);
        Runnable mostraDuello = () -> schermate.show(radice, "duello");
        menu = new MenuPrincipale(() -> {
            iniziaDuello(Tappa.VALLE);
            mostraDuello.run();
        }, () -> {
            if (caricaPartita(menu::mostraMessaggio)) {
                mostraDuello.run();
            }
        }, mostraDuello, finestra::dispose);

        JPanel contenuto = pannello(new BorderLayout(0, 14));
        contenuto.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JPanel intestazione = pannello(new BorderLayout(0, 12));
        JPanel titolo = pannello(new BorderLayout());
        titolo.add(etichetta("Goldrake RPG", 26, TESTO), BorderLayout.WEST);
        JPanel avanzamento = pannello(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        avanzamento.add(esito);
        avanzamento.add(prosegui);
        JButton apriMenu = new PulsanteArcade("Menu", ACCIAIO);
        apriMenu.addActionListener(evento -> {
            menu.abilitaRitornoAlDuello();
            schermate.show(radice, "menu");
        });
        avanzamento.add(apriMenu);
        titolo.add(avanzamento, BorderLayout.EAST);
        intestazione.add(titolo, BorderLayout.NORTH);
        JPanel stato = pannello(new GridLayout(1, 2, 14, 0));
        stato.add(scheda(etichetta("Goldrake", 16, AZZURRO), vitaGoldrake, energiaGoldrake));
        stato.add(scheda(nomeNemico, vitaNemico));
        intestazione.add(stato, BorderLayout.CENTER);
        avvisoSpeciale.setVisible(false);
        intestazione.add(avvisoSpeciale, BorderLayout.SOUTH);
        contenuto.add(intestazione, BorderLayout.NORTH);
        contenuto.add(arena, BorderLayout.CENTER);

        JButton nuovaPartita = new PulsanteArcade("Nuova partita", ACCIAIO);
        JButton carica = new PulsanteArcade("Carica salvataggio", AZZURRO);
        salva.setToolTipText("Registra la tappa, i punti vita e l'energia attuali.");
        carica.setToolTipText("Riprendi dall'ultima volta che hai premuto Salva partita.");
        maglioPerforante.addActionListener(evento -> attacca(partita.maglioPerforante));
        alabardaSpaziale.addActionListener(evento -> attacca(partita.alabardaSpaziale));
        tecnicaPremio.addActionListener(evento -> partita.tecnicaPremio()
                .map(TecnicaPremio::attacco).ifPresent(this::attacca));
        guardia.setToolTipText("Dimezza il danno in arrivo e recupera fino a %d energia."
                .formatted(DuelloGoldrake.ENERGIA_GUARDIA));
        guardia.addActionListener(evento -> mostraTurno(partita.usaGuardia(), "Goldrake si mette in guardia."));
        nuovaPartita.addActionListener(evento -> iniziaDuello(Tappa.VALLE));
        prosegui.addActionListener(evento -> proseguiDopoDuello());
        salva.addActionListener(evento -> salvaPartita());
        carica.addActionListener(evento -> caricaPartita(this::scrivi));
        JPanel comandi = pannello(new FlowLayout(FlowLayout.LEFT, 6, 0));
        comandi.add(maglioPerforante);
        comandi.add(alabardaSpaziale);
        comandi.add(tecnicaPremio);
        comandi.add(guardia);
        JPanel gestionePartita = pannello(new FlowLayout(FlowLayout.LEFT, 6, 0));
        gestionePartita.add(nuovaPartita);
        gestionePartita.add(salva);
        gestionePartita.add(carica);
        JPanel azioni = pannello(new GridLayout(2, 1, 0, 4));
        azioni.add(comandi);
        azioni.add(gestionePartita);

        JPanel inferiore = pannello(new BorderLayout(0, 10));
        inferiore.add(pannelloCronaca(), BorderLayout.CENTER);
        inferiore.add(azioni, BorderLayout.SOUTH);
        contenuto.add(inferiore, BorderLayout.SOUTH);

        radice.add(menu, "menu");
        radice.add(contenuto, "duello");
        finestra.setContentPane(radice);
        tecnicaPremio.setText(descrivi(TecnicaPremio.RAGGIO_ANTIGRAVITA.attacco()));
        finestra.pack();
        int larghezzaMinima = azioni.getPreferredSize().width + 44 + finestra.getInsets().left
                + finestra.getInsets().right;
        tecnicaPremio.setVisible(false);
        finestra.setMinimumSize(new Dimension(Math.max(800, larghezzaMinima), 595));
        finestra.setLocationRelativeTo(null);
        finestra.setVisible(true);
    }

    private void iniziaDuello(Tappa tappa) {
        mostraNuovoDuello(new DuelloGoldrake(tappa));
    }

    private void mostraNuovoDuello(DuelloGoldrake nuovoDuello) {
        partita = nuovoDuello;
        cronaca.setText("%s%nTi sfida: %s. Scegli una mossa.%n"
                .formatted(partita.tappa.luogo, partita.nemico.getNome()));
        aggiornaStato();
    }

    private void proseguiDopoDuello() {
        if (!partita.deveSceglierePremio()) {
            mostraNuovoDuello(partita.prosegui(Optional.empty()));
            return;
        }

        TecnicaPremio[] premi = TecnicaPremio.values();
        String[] opzioni = Arrays.stream(premi)
                .map(premio -> descrivi(premio.attacco())).toArray(String[]::new);
        int scelta = JOptionPane.showOptionDialog(prosegui,
                "La tecnica scelta resterà con Goldrake.\n"
                        + "Il duello in città sostituirà il salvataggio precedente.",
                "Scegli una tecnica", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, opzioni, null);
        if (scelta < 0) {
            return;
        }

        TecnicaPremio premio = premi[scelta];
        DuelloGoldrake prossimo = partita.prosegui(Optional.of(premio));
        try {
            archivio.salva(prossimo);
            mostraNuovoDuello(prossimo);
            scrivi("Tecnica sbloccata: " + premio.attacco().getNome() + ". Partita salvata.");
        } catch (IOException errore) {
            scrivi("Impossibile salvare la tecnica. " + errore.getMessage());
        }
    }

    private void salvaPartita() {
        try {
            archivio.salva(partita);
            scrivi("Partita salvata.");
        } catch (IOException errore) {
            scrivi("Impossibile salvare la partita. " + errore.getMessage());
        }
    }

    private boolean caricaPartita(Consumer<String> mostraErrore) {
        try {
            partita = archivio.carica();
            cronaca.setText("Partita caricata: %s. Scegli una mossa.%n".formatted(partita.tappa.luogo));
            aggiornaStato();
            return true;
        } catch (NoSuchFileException errore) {
            mostraErrore.accept("Nessun salvataggio. Premi Salva partita durante un duello.");
        } catch (IOException errore) {
            mostraErrore.accept("Impossibile caricare la partita. " + errore.getMessage());
        }
        return false;
    }

    private void attacca(Attacco mossa) {
        DuelloGoldrake.EsitoTurno turno = partita.giocaTurno(mossa);
        if (!turno.eseguito()) {
            scrivi("Energia insufficiente. Scegli un'altra mossa.");
            return;
        }
        mostraTurno(turno, "Goldrake usa " + mossa.getNome() + ".");
    }

    private void mostraTurno(DuelloGoldrake.EsitoTurno turno, String azione) {
        if (!turno.eseguito()) {
            return;
        }
        scrivi(azione);
        turno.rispostaBoss().ifPresent(mossa -> scrivi("%s usa %s (%d danni).".formatted(
                partita.nemico.getNome(), mossa.getNome(), mossa.getDanno())));
        if (partita.isTerminato()) {
            if (partita.goldrake.isSconfitto()) {
                scrivi("Sconfitta. Puoi riprovare questo duello.");
            } else if (partita.deveSceglierePremio()) {
                scrivi("Vittoria! Scegli una nuova tecnica per il prossimo duello.");
            } else if (partita.tappaDaGiocare().isEmpty()) {
                scrivi("Campagna completata! La Terra è salva.");
            } else {
                scrivi("Vittoria! Hai sbloccato il prossimo duello.");
            }
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
        nomeNemico.setText(partita.nemico.getNome());
        arena.aggiorna(partita.tappa, partita.goldrake.isSconfitto(), partita.nemico.isSconfitto());
        boolean inCorso = !partita.isTerminato();
        String avviso = partita.avvisoSpeciale().orElse("");
        avvisoSpeciale.setText(avviso);
        avvisoSpeciale.setVisible(!avviso.isEmpty());
        boolean sconfitto = partita.goldrake.isSconfitto();
        String duello = "Duello %d/%d".formatted(partita.tappa.ordinal() + 1, Tappa.values().length);
        if (inCorso) {
            esito.setText(duello + "  /  Il tuo turno");
        } else {
            esito.setText(duello + (sconfitto ? "  /  Sconfitta" : "  /  Vittoria"));
        }
        if (partita.deveSceglierePremio()) {
            prosegui.setText("Scegli tecnica");
        } else {
            prosegui.setText(sconfitto ? "Riprova duello" : "Prossimo duello");
        }
        prosegui.setVisible(partita.tappaDaGiocare().isPresent());
        maglioPerforante.setEnabled(inCorso);
        alabardaSpaziale.setEnabled(inCorso);
        guardia.setEnabled(inCorso);
        partita.tecnicaPremio().ifPresentOrElse(premio -> {
            Attacco attacco = premio.attacco();
            tecnicaPremio.setText(descrivi(attacco));
            tecnicaPremio.setEnabled(inCorso);
            tecnicaPremio.setVisible(true);
        }, () -> tecnicaPremio.setVisible(false));
        salva.setEnabled(partita.inAttesaDiMossa());
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

    private static JPanel scheda(JLabel nome, JProgressBar... barre) {
        JPanel scheda = new JPanel(new BorderLayout(0, 8));
        scheda.setBackground(SUPERFICIE);
        scheda.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        scheda.add(nome, BorderLayout.NORTH);
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
        return "%s · %d danni / %d EN".formatted(
                attacco.getNome(), attacco.getDanno(), attacco.getCostoEnergia());
    }
}
