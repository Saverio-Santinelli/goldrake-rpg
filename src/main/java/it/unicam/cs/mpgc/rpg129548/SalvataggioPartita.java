package it.unicam.cs.mpgc.rpg129548;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Legge e scrive lo stato del duello all'inizio di un turno di Goldrake. */
final class SalvataggioPartita {

    private final Path file;

    SalvataggioPartita(Path file) {
        this.file = file.toAbsolutePath();
    }

    void salva(DuelloGoldrake partita) throws IOException {
        if (partita.combattimento.isTerminato()
                || partita.combattimento.getDiTurno() != partita.goldrake) {
            throw new IllegalStateException("Il salvataggio richiede un turno di Goldrake in corso");
        }

        Properties dati = new Properties();
        dati.setProperty("versione", "1");
        dati.setProperty("vitaGoldrake", Integer.toString(partita.goldrake.getPuntiVita()));
        dati.setProperty("energiaGoldrake", Integer.toString(partita.goldrake.getEnergia()));
        dati.setProperty("vitaNemico", Integer.toString(partita.nemico.getPuntiVita()));

        Path cartella = Files.createDirectories(file.getParent());
        Path temporaneo = Files.createTempFile(cartella, "partita-", ".tmp");
        try {
            try (var uscita = Files.newOutputStream(temporaneo)) {
                dati.store(uscita, "Goldrake RPG");
            }
            Files.move(temporaneo, file, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaneo);
        }
    }

    DuelloGoldrake carica() throws IOException {
        Properties dati = new Properties();
        try (var ingresso = Files.newInputStream(file)) {
            dati.load(ingresso);
            if (!"1".equals(dati.getProperty("versione"))) {
                throw new IllegalArgumentException("Versione del salvataggio non supportata");
            }

            DuelloGoldrake partita = new DuelloGoldrake();
            int vitaGoldrake = leggi(dati, "vitaGoldrake", 1, partita.goldrake.getPuntiVitaMassimi());
            int energiaGoldrake = leggi(dati, "energiaGoldrake", 0, partita.goldrake.getEnergia());
            int vitaNemico = leggi(dati, "vitaNemico", 1, partita.nemico.getPuntiVitaMassimi());

            partita.goldrake.subisciDanno(partita.goldrake.getPuntiVita() - vitaGoldrake);
            partita.goldrake.consumaEnergia(partita.goldrake.getEnergia() - energiaGoldrake);
            partita.nemico.subisciDanno(partita.nemico.getPuntiVita() - vitaNemico);
            return partita;
        } catch (IllegalArgumentException errore) {
            throw new IOException("File di salvataggio non valido", errore);
        }
    }

    private static int leggi(Properties dati, String chiave, int minimo, int massimo) {
        int valore = Integer.parseInt(dati.getProperty(chiave));
        if (valore < minimo || valore > massimo) {
            throw new IllegalArgumentException("Valore fuori intervallo: " + chiave);
        }
        return valore;
    }
}
