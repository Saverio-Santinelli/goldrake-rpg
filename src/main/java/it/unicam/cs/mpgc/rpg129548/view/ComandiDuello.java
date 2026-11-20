package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.controller.AzioniGioco;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Mossa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import static it.unicam.cs.mpgc.rpg129548.view.StileGioco.*;

/** Pulsanti del duello: inoltrano richieste e riflettono le azioni disponibili. */
final class ComandiDuello extends JPanel {
    private final JButton maglioPerforante = new PulsanteArcade("", ROSSO);
    private final JButton alabardaSpaziale = new PulsanteArcade("", ORO);
    private final JButton tecnicaPremio = new PulsanteArcade("", VIOLA);
    private final JButton guardia = new PulsanteArcade("Guardia", AZZURRO);
    private final JButton salva = new PulsanteArcade("Salva partita", ACCIAIO);
    private final JButton prosegui = new PulsanteArcade("", ORO);
    private final int larghezzaComandi;

    ComandiDuello(DuelloGoldrake partita, AzioniGioco azioni) {
        JButton nuovaPartita = new PulsanteArcade("Nuova partita", ACCIAIO);
        JButton carica = new PulsanteArcade("Carica salvataggio", AZZURRO);
        salva.setToolTipText("Registra la tappa, i punti vita e l'energia attuali.");
        carica.setToolTipText("Riprendi dall'ultima volta che hai premuto Salva partita.");
        maglioPerforante.addActionListener(evento -> azioni.usaMaglio());
        alabardaSpaziale.addActionListener(evento -> azioni.usaAlabarda());
        tecnicaPremio.addActionListener(evento -> azioni.usaTecnicaPremio());
        guardia.setToolTipText("Dimezza il danno in arrivo e recupera fino a %d energia."
                .formatted(DuelloGoldrake.ENERGIA_GUARDIA));
        guardia.addActionListener(evento -> azioni.usaGuardia());
        nuovaPartita.addActionListener(evento -> azioni.nuovaPartita());
        prosegui.addActionListener(evento -> azioni.prosegui());
        salva.addActionListener(evento -> azioni.salvaPartita());
        carica.addActionListener(evento -> azioni.caricaPartita());
        JPanel comandi = pannello(new FlowLayout(FlowLayout.LEFT, 6, 0));
        comandi.add(maglioPerforante);
        comandi.add(alabardaSpaziale);
        comandi.add(tecnicaPremio);
        comandi.add(guardia);
        JPanel gestionePartita = pannello(new FlowLayout(FlowLayout.LEFT, 6, 0));
        gestionePartita.add(nuovaPartita);
        gestionePartita.add(salva);
        gestionePartita.add(carica);
        setLayout(new GridLayout(2, 1, 0, 4));
        setBackground(SFONDO);
        add(comandi);
        add(gestionePartita);

        maglioPerforante.setText(descrivi(partita.maglioPerforante()));
        alabardaSpaziale.setText(descrivi(partita.alabardaSpaziale()));
        tecnicaPremio.setText(descrivi(TecnicaPremio.RAGGIO_ANTIGRAVITA.attacco()));
        larghezzaComandi = getPreferredSize().width;
        tecnicaPremio.setVisible(false);
    }

    JButton pulsanteProsegui() {
        return prosegui;
    }

    int larghezzaComandi() {
        return larghezzaComandi;
    }

    void aggiorna(DuelloGoldrake partita) {
        boolean inCorso = !partita.isTerminato();
        prosegui.setVisible(partita.tappaDaGiocare().isPresent());
        maglioPerforante.setEnabled(inCorso);
        alabardaSpaziale.setEnabled(inCorso);
        guardia.setEnabled(inCorso);
        partita.tecnicaPremio().ifPresentOrElse(premio -> {
            Mossa attacco = premio.attacco();
            tecnicaPremio.setText(descrivi(attacco));
            tecnicaPremio.setEnabled(inCorso);
            tecnicaPremio.setVisible(true);
        }, () -> tecnicaPremio.setVisible(false));
        salva.setEnabled(partita.inAttesaDiMossa());
    }

}