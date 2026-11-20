package it.unicam.cs.mpgc.rpg129548.controller;

import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;
import it.unicam.cs.mpgc.rpg129548.persistence.ArchivioPartita;
import it.unicam.cs.mpgc.rpg129548.persistence.CatalogoPersonaggi;
import it.unicam.cs.mpgc.rpg129548.persistence.SalvataggioPartita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SessionePartitaTest {
    @TempDir
    Path cartella;

    private SessionePartita sessione() {
        var configurazione = CatalogoPersonaggi.carica();
        return new SessionePartita(configurazione, new SalvataggioPartita(configurazione, cartella));
    }

    @Test
    void energiaInsufficienteNonModificaStatoOSalvataggio() throws IOException {
        var sessione = sessione();
        assertTrue(sessione.attacca(sessione.partita().alabardaSpaziale()).eseguito());
        sessione.salva();
        int vita = sessione.partita().nemico().getPuntiVita();
        int fase = sessione.partita().faseBoss();
        assertFalse(sessione.attacca(sessione.partita().alabardaSpaziale()).eseguito());
        assertEquals(vita, sessione.partita().nemico().getPuntiVita());
        assertEquals(fase, sessione.partita().faseBoss());
        assertFalse(sessione.haProgressiDaSalvare());
    }

    @Test
    void caricaRipristinaAvvisoDelBossEStatistiche() throws IOException {
        var sessione = sessione();
        sessione.attacca(sessione.partita().maglioPerforante());
        sessione.attacca(sessione.partita().maglioPerforante());
        sessione.usaGuardia();
        assertTrue(sessione.partita().avvisoSpeciale().isPresent());
        var statistiche = sessione.partita().statisticheCampagna();
        int vita = sessione.partita().goldrake().getPuntiVita();
        sessione.salva();
        sessione.attacca(sessione.partita().maglioPerforante());
        assertTrue(sessione.haProgressiDaSalvare());
        sessione.carica();
        assertTrue(sessione.partita().avvisoSpeciale().isPresent());
        assertEquals(vita, sessione.partita().goldrake().getPuntiVita());
        assertEquals(statistiche, sessione.partita().statisticheCampagna());
        assertFalse(sessione.haProgressiDaSalvare());
    }

    @ParameterizedTest
    @EnumSource(TecnicaPremio.class)
    void completaTreTappeConEntrambeLeTecniche(TecnicaPremio premio) throws IOException {
        var sessione = sessione();
        vinci(sessione);
        sessione.scegliTecnica(premio);
        assertEquals(Tappa.CITTA, sessione.partita().tappa());
        assertEquals(premio, sessione.partita().tecnicaPremio().orElseThrow());
        assertFalse(sessione.haProgressiDaSalvare());
        sessione.carica();
        vinci(sessione);
        sessione.prosegui();
        assertEquals(Tappa.LUNA, sessione.partita().tappa());
        assertTrue(sessione.haProgressiDaSalvare());
        vinci(sessione);
        assertTrue(sessione.partita().tappaDaGiocare().isEmpty());
        assertEquals(3, sessione.partita().statisticheCampagna().size());
        assertFalse(sessione.partita().goldrake().isSconfitto());
    }

    @Test
    void erroreSalvataggioTecnicaConservaVittoriaEScelta() {
        var sessione = new SessionePartita(CatalogoPersonaggi.carica(), new ArchivioPartita() {
            public void salva(DuelloGoldrake partita) throws IOException { throw new IOException("disco pieno"); }
            public DuelloGoldrake carica() throws IOException { throw new IOException("assente"); }
        });
        vinci(sessione);
        var vittoria = sessione.partita();
        assertThrows(IOException.class, () -> sessione.scegliTecnica(TecnicaPremio.TUONO_SPAZIALE));
        assertSame(vittoria, sessione.partita());
        assertTrue(sessione.partita().deveSceglierePremio());
        assertTrue(sessione.haProgressiDaSalvare());
    }

    @Test
    void caricamentoCorrottoConservaPartitaCorrente() throws IOException {
        var sessione = sessione();
        sessione.attacca(sessione.partita().maglioPerforante());
        var corrente = sessione.partita();
        Files.writeString(cartella.resolve("partita.xml"), "XML non valido");
        assertThrows(IOException.class, sessione::carica);
        assertSame(corrente, sessione.partita());
        assertTrue(sessione.haProgressiDaSalvare());
    }

    @Test
    void sconfittaPermetteDiRiprovareLaStessaTappa() {
        var sessione = sessione();
        sessione.partita().goldrake().subisciDanno(100);
        sessione.prosegui();
        assertEquals(Tappa.VALLE, sessione.partita().tappa());
        assertEquals(100, sessione.partita().goldrake().getPuntiVita());
        assertTrue(sessione.haProgressiDaSalvare());
    }

    @Test
    void leggeAncoraIlVecchioSalvataggioProperties() throws IOException {
        var dati = new Properties();
        dati.setProperty("versione", "1");
        dati.setProperty("vitaGoldrake", "70");
        dati.setProperty("energiaGoldrake", "10");
        dati.setProperty("vitaNemico", "40");
        try (var uscita = Files.newOutputStream(cartella.resolve("partita.properties"))) {
            dati.store(uscita, "Versione precedente");
        }
        var sessione = sessione();
        sessione.carica();
        assertEquals(Tappa.LUNA, sessione.partita().tappa());
        assertEquals(70, sessione.partita().goldrake().getPuntiVita());
        assertEquals(40, sessione.partita().nemico().getPuntiVita());
    }

    static void vinci(SessionePartita sessione) {
        for (int turni = 0; !sessione.partita().isTerminato() && turni < 100; turni++) {
            var partita = sessione.partita();
            if (partita.avvisoSpeciale().isPresent()) {
                sessione.usaGuardia();
            } else if (partita.goldrake().getEnergia() >= partita.alabardaSpaziale().getCostoEnergia()) {
                sessione.attacca(partita.alabardaSpaziale());
            } else {
                sessione.attacca(partita.maglioPerforante());
            }
        }
        assertTrue(sessione.partita().nemico().isSconfitto());
        assertFalse(sessione.partita().goldrake().isSconfitto());
    }
}
