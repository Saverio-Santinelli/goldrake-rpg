package it.unicam.cs.mpgc.rpg129548.controller;

import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import it.unicam.cs.mpgc.rpg129548.persistence.ArchivioPartita;
import it.unicam.cs.mpgc.rpg129548.persistence.CatalogoPersonaggi;
import it.unicam.cs.mpgc.rpg129548.view.Schermata;
import it.unicam.cs.mpgc.rpg129548.view.VistaGioco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DuelloGraficoTest {
    private final VistaFinta vista = new VistaFinta();
    private final ArchivioFinto archivio = new ArchivioFinto();
    private SessionePartita sessione;
    private AzioniGioco controller;

    @BeforeEach
    void prepara() {
        sessione = new SessionePartita(CatalogoPersonaggi.carica(), archivio);
        controller = new DuelloGrafico(sessione, vista);
    }

    @Test
    void annullareNuovaPartitaConservaIProgressi() {
        controller.usaMaglio();
        var partita = sessione.partita();
        controller.nuovaPartita();
        assertSame(partita, sessione.partita());
        assertEquals(1, vista.conferme);
        vista.conferma = true;
        controller.nuovaPartita();
        assertNotSame(partita, sessione.partita());
        assertEquals(100, sessione.partita().goldrake().getPuntiVita());
        assertEquals(Schermata.DUELLO, vista.schermata);
    }

    @Test
    void annullareCaricamentoNonInterrogaArchivio() {
        controller.usaMaglio();
        controller.caricaPartita();
        assertEquals(0, archivio.caricamenti);
        assertEquals(1, vista.conferme);
    }

    @Test
    void uscitaRichiedeConfermaSoloConProgressiNonSalvati() {
        controller.usaMaglio();
        controller.esci();
        assertFalse(vista.chiusa);
        controller.salvaPartita();
        controller.esci();
        assertTrue(vista.chiusa);
        assertEquals(1, vista.conferme);
    }

    @Test
    void erroreCaricamentoNonCambiaSchermataOPartita() {
        var partita = sessione.partita();
        controller.caricaPartita();
        assertSame(partita, sessione.partita());
        assertEquals(Schermata.MENU, vista.schermata);
        assertTrue(vista.erroreCaricamento.startsWith("Nessun salvataggio"));
    }

    @Test
    void vittoriaMostraTecnichePoiDuelloSuccessivo() {
        SessionePartitaTest.vinci(sessione);
        controller.prosegui();
        assertEquals(Schermata.TECNICHE, vista.schermata);
        controller.scegliTecnica(TecnicaPremio.RAGGIO_ANTIGRAVITA);
        assertEquals(Schermata.DUELLO, vista.schermata);
        assertEquals(Tappa.CITTA, vista.partita.tappa());
        assertTrue(vista.messaggi.stream().anyMatch(testo -> testo.contains("Tecnica sbloccata")));
    }

    @Test
    void salvataggioTecnicaFallitoRestaSullaScelta() {
        SessionePartitaTest.vinci(sessione);
        controller.prosegui();
        archivio.erroreScrittura = true;
        controller.scegliTecnica(TecnicaPremio.TUONO_SPAZIALE);
        assertEquals(Schermata.TECNICHE, vista.schermata);
        assertEquals(Tappa.VALLE, sessione.partita().tappa());
        assertTrue(vista.erroreTecnica.startsWith("Impossibile salvare"));
    }

    @Test
    void ultimoColpoApreIlFinaleConLeTreStatistiche() throws IOException {
        SessionePartitaTest.vinci(sessione);
        sessione.scegliTecnica(TecnicaPremio.TUONO_SPAZIALE);
        SessionePartitaTest.vinci(sessione);
        sessione.prosegui();
        sessione.partita().nemico().subisciDanno(79);
        controller.usaMaglio();
        assertEquals(Schermata.FINALE, vista.schermata);
        assertEquals(3, vista.partita.statisticheCampagna().size());
    }

    private static final class ArchivioFinto implements ArchivioPartita {
        private int caricamenti;
        private boolean erroreScrittura;
        public void salva(DuelloGoldrake partita) throws IOException {
            if (erroreScrittura) { throw new IOException("disco pieno"); }
        }
        public DuelloGoldrake carica() throws IOException {
            caricamenti++;
            throw new NoSuchFileException("partita.xml");
        }
    }

    private static final class VistaFinta implements VistaGioco {
        private Schermata schermata = Schermata.MENU;
        private DuelloGoldrake partita;
        private boolean conferma;
        private boolean chiusa;
        private int conferme;
        private String erroreCaricamento = "";
        private String erroreTecnica = "";
        private final List<String> messaggi = new ArrayList<>();
        public void collega(AzioniGioco azioni, DuelloGoldrake partita) { this.partita = partita; }
        public void mostra() { }
        public void chiudi() { chiusa = true; }
        public boolean confermaAbbandono(String azione) { conferme++; return conferma; }
        public void mostraSchermata(Schermata schermata) { this.schermata = schermata; }
        public void aggiorna(DuelloGoldrake partita) { this.partita = partita; }
        public void impostaCronaca(String testo) { messaggi.clear(); messaggi.add(testo); }
        public void scrivi(String messaggio) { messaggi.add(messaggio); }
        public void mostraErroreCaricamento(String messaggio) { erroreCaricamento = messaggio; }
        public void mostraErroreTecnica(String messaggio) { erroreTecnica = messaggio; }
    }
}
