package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import static it.unicam.cs.mpgc.rpg129548.view.StileGioco.*;

/** Presenta indicatori, avviso del boss ed esito del duello. */
final class StatoDuello extends JPanel {
    private final JLabel esito = etichetta("Il tuo turno", 14, AZZURRO);
    private final JLabel avvisoSpeciale = etichetta("", 13, ORO);
    private final JLabel nomeNemico = etichetta("", 16, VIOLA);
    private final JProgressBar vitaGoldrake = barra(AZZURRO);
    private final JProgressBar energiaGoldrake = barra(ORO);
    private final JProgressBar vitaNemico = barra(VIOLA);
    private final JButton prosegui;

    StatoDuello(JButton prosegui, Runnable mostraMenu) {
        this.prosegui = prosegui;
        setLayout(new BorderLayout(0, 12));
        setBackground(SFONDO);
        JPanel titolo = pannello(new BorderLayout());
        titolo.add(etichetta("Goldrake RPG", 26, TESTO), BorderLayout.WEST);
        JPanel avanzamento = pannello(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        avanzamento.add(esito);
        avanzamento.add(prosegui);
        JButton apriMenu = new PulsanteArcade("Menu", ACCIAIO);
        apriMenu.addActionListener(evento -> mostraMenu.run());
        avanzamento.add(apriMenu);
        titolo.add(avanzamento, BorderLayout.EAST);
        add(titolo, BorderLayout.NORTH);
        JPanel stato = pannello(new GridLayout(1, 2, 14, 0));
        stato.add(scheda(etichetta("Goldrake", 16, AZZURRO), vitaGoldrake, energiaGoldrake));
        stato.add(scheda(nomeNemico, vitaNemico));
        add(stato, BorderLayout.CENTER);
        avvisoSpeciale.setVisible(false);
        add(avvisoSpeciale, BorderLayout.SOUTH);
    }

    public void aggiorna(DuelloGoldrake partita) {
        aggiornaBarra(vitaGoldrake, "Vita", partita.goldrake().getPuntiVita(),
                partita.goldrake().getPuntiVitaMassimi());
        aggiornaBarra(energiaGoldrake, "Energia", partita.goldrake().getEnergia(),
                partita.goldrake().getEnergiaMassima());
        aggiornaBarra(vitaNemico, "Vita", partita.nemico().getPuntiVita(),
                partita.nemico().getPuntiVitaMassimi());
        nomeNemico.setText(partita.nemico().getNome());
        boolean inCorso = !partita.isTerminato();
        String avviso = partita.avvisoSpeciale().orElse("");
        avvisoSpeciale.setText(avviso);
        avvisoSpeciale.setVisible(!avviso.isEmpty());
        boolean sconfitto = partita.goldrake().isSconfitto();
        String duello = "Duello %d/%d".formatted(partita.tappa().ordinal() + 1, Tappa.values().length);
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

}