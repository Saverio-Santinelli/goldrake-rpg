package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.model.Mossa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static it.unicam.cs.mpgc.rpg129548.view.StileGioco.*;

/** Schermata di scelta della ricompensa: descrizione e comandi della vista. */
public final class SceltaTecnica extends JPanel {
    private final JLabel messaggio = etichetta(
            "La scelta avvia la tappa in città e sostituisce il salvataggio precedente.",
            14, TESTO_SECONDARIO);
    public SceltaTecnica(Consumer<TecnicaPremio> scegliTecnica, Runnable tornaAlDuello) {
        setLayout(new BorderLayout(0, 24));
        setBackground(SFONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 24, 32));

        JPanel intestazione = pannello(new GridLayout(0, 1, 0, 8));
        intestazione.add(etichetta("VALLE MONTANA  /  VITTORIA", 14, ORO));
        intestazione.add(etichetta("Scegli una tecnica", 32, TESTO));
        intestazione.add(etichetta("La tecnica scelta resta con Goldrake fino alla fine della campagna.",
                16, TESTO_SECONDARIO));
        add(intestazione, BorderLayout.NORTH);

        JPanel schede = pannello(new GridLayout(1, 0, 18, 0));

        JPanel contenitore = pannello(new BorderLayout());
        contenitore.add(schede, BorderLayout.NORTH);
        add(contenitore, BorderLayout.CENTER);

        for (TecnicaPremio premio : TecnicaPremio.values()) {
            boolean tuono = premio == TecnicaPremio.TUONO_SPAZIALE;
            Color accento = tuono ? ORO : AZZURRO;
            Mossa attacco = premio.attacco();
            JPanel scheda = new JPanel(new BorderLayout(0, 20));
            scheda.setBackground(SUPERFICIE);
            scheda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(3, 1, 1, 1, accento),
                    BorderFactory.createEmptyBorder(22, 20, 20, 20)));

            JPanel dettagli = new JPanel(new GridLayout(0, 1, 0, 12));
            dettagli.setOpaque(false);
            dettagli.add(etichetta(attacco.getNome(), 23, TESTO));
            dettagli.add(etichetta("%d danni  /  %d energia".formatted(
                    attacco.getDanno(), attacco.getCostoEnergia()), 16, accento));
            dettagli.add(etichetta(tuono ? "Un colpo potente, da usare a piena energia."
                    : "Un attacco più economico, da usare più spesso.", 14, TESTO_SECONDARIO));
            scheda.add(dettagli, BorderLayout.CENTER);

            JButton scegli = new PulsanteArcade("Scegli " + attacco.getNome(), accento);
            scegli.addActionListener(evento -> scegliTecnica.accept(premio));
            scheda.add(scegli, BorderLayout.SOUTH);
            schede.add(scheda);
        }

        JButton indietro = new PulsanteArcade("Torna al duello", ACCIAIO);
        indietro.addActionListener(evento -> {
            messaggio.setText("La scelta avvia la tappa in città e sostituisce il salvataggio precedente.");
            messaggio.setForeground(TESTO_SECONDARIO);
            tornaAlDuello.run();
        });
        JPanel inferiore = pannello(new BorderLayout(0, 12));
        inferiore.add(messaggio, BorderLayout.NORTH);
        JPanel comandi = pannello(new FlowLayout(FlowLayout.LEFT, 0, 0));
        comandi.add(indietro);
        inferiore.add(comandi, BorderLayout.SOUTH);
        add(inferiore, BorderLayout.SOUTH);
    }
    public void mostraErrore(String errore) {
        messaggio.setText(errore);
        messaggio.setForeground(ROSSO);
    }
}