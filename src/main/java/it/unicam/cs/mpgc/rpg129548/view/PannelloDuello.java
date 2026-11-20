package it.unicam.cs.mpgc.rpg129548.view;

import it.unicam.cs.mpgc.rpg129548.controller.AzioniGioco;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import static it.unicam.cs.mpgc.rpg129548.view.StileGioco.*;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** Compone i componenti del duello senza eseguire turni o salvataggi. */
public final class PannelloDuello extends JPanel {
    private final Arena arena = new Arena();
    private final CronacaDuello cronaca = new CronacaDuello();
    private final ComandiDuello comandi;
    private final StatoDuello stato;

    public PannelloDuello(DuelloGoldrake partita, AzioniGioco azioni, Runnable mostraMenu) {
        super(new BorderLayout(0, 14));
        setBackground(SFONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        comandi = new ComandiDuello(partita, azioni);
        stato = new StatoDuello(comandi.pulsanteProsegui(), mostraMenu);
        add(stato, BorderLayout.NORTH);
        add(arena, BorderLayout.CENTER);
        JPanel inferiore = pannello(new BorderLayout(0, 10));
        inferiore.add(cronaca, BorderLayout.CENTER);
        inferiore.add(comandi, BorderLayout.SOUTH);
        add(inferiore, BorderLayout.SOUTH);
        aggiorna(partita);
    }

    public int larghezzaComandi() {
        return comandi.larghezzaComandi();
    }

    public void impostaCronaca(String testo) {
        cronaca.impostaTesto(testo);
    }

    public void scrivi(String messaggio) {
        cronaca.scrivi(messaggio);
    }

    public void aggiorna(DuelloGoldrake partita) {
        stato.aggiorna(partita);
        comandi.aggiorna(partita);
        arena.aggiorna(partita.tappa(), partita.goldrake().isSconfitto(), partita.nemico().isSconfitto());
    }
}
