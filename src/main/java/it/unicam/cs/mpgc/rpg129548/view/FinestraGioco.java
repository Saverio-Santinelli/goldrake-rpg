package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.controller.AzioniGioco;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Ospita e collega le schermate Swing senza eseguire regole o accessi ai file. */
public final class FinestraGioco implements VistaGioco {
    private final JFrame finestra = new JFrame("Goldrake RPG");
    private final CardLayout schermate = new CardLayout();
    private final JPanel radice = new JPanel(schermate);
    private final RiepilogoCampagna riepilogo = new RiepilogoCampagna(
            () -> mostraSchermata(Schermata.FINALE));
    private PannelloDuello duello;
    private MenuPrincipale menu;
    private SceltaTecnica tecniche;
    private Schermata corrente = Schermata.MENU;

    @Override
    public void collega(AzioniGioco azioni, DuelloGoldrake partita) {
        finestra.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        finestra.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                azioni.esci();
            }
        });
        menu = new MenuPrincipale(azioni::nuovaPartita, azioni::caricaPartita,
                () -> mostraSchermata(Schermata.DUELLO),
                () -> mostraSchermata(Schermata.BRIEFING), azioni::esci);
        duello = new PannelloDuello(partita, azioni, () -> {
            menu.impostaRitornoAlDuello(true);
            mostraSchermata(Schermata.MENU);
        });
        tecniche = new SceltaTecnica(azioni::scegliTecnica,
                () -> mostraSchermata(Schermata.DUELLO));
        aggiungi(menu, Schermata.MENU);
        aggiungi(duello, Schermata.DUELLO);
        aggiungi(tecniche, Schermata.TECNICHE);
        aggiungi(riepilogo, Schermata.STATISTICHE);
        aggiungi(new Briefing(() -> mostraSchermata(Schermata.MENU)), Schermata.BRIEFING);
        aggiungi(new FinaleCampagna(() -> {
            menu.impostaRitornoAlDuello(false);
            mostraSchermata(Schermata.MENU);
        }, () -> mostraSchermata(Schermata.STATISTICHE)), Schermata.FINALE);
        finestra.setContentPane(radice);
        finestra.pack();
        int larghezza = duello.larghezzaComandi() + 44 + finestra.getInsets().left
                + finestra.getInsets().right;
        finestra.setMinimumSize(new Dimension(Math.max(800, larghezza), 595));
        finestra.setLocationRelativeTo(null);
        mostraSchermata(Schermata.MENU);
        aggiorna(partita);
    }

    private void aggiungi(JPanel pannello, Schermata schermata) {
        radice.add(pannello, schermata.name());
    }

    @Override
    public void mostra() {
        finestra.setVisible(true);
    }

    @Override
    public void chiudi() {
        finestra.dispose();
    }

    @Override
    public boolean confermaAbbandono(String azione) {
        Object[] opzioni = {"Annulla", azione};
        return JOptionPane.showOptionDialog(finestra,
                "Hai progressi non salvati. Vuoi continuare senza salvarli?",
                "Progressi non salvati", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opzioni, opzioni[0]) == 1;
    }

    @Override
    public void mostraSchermata(Schermata schermata) {
        corrente = schermata;
        schermate.show(radice, schermata.name());
    }

    @Override
    public void aggiorna(DuelloGoldrake partita) {
        duello.aggiorna(partita);
        riepilogo.aggiorna(partita.statisticheCampagna());
    }

    @Override
    public void impostaCronaca(String testo) {
        duello.impostaCronaca(testo);
    }

    @Override
    public void scrivi(String messaggio) {
        duello.scrivi(messaggio);
    }

    @Override
    public void mostraErroreCaricamento(String messaggio) {
        if (corrente == Schermata.MENU) {
            menu.mostraMessaggio(messaggio);
        } else {
            duello.scrivi(messaggio);
        }
    }

    @Override
    public void mostraErroreTecnica(String messaggio) {
        tecniche.mostraErrore(messaggio);
    }
}
