package it.unicam.cs.mpgc.rpg129548;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;

/** Legge e scrive lo stato del duello all'inizio di un turno di Goldrake. */
final class SalvataggioPartita {

    private final Path fileXml;
    private final Path filePrecedente;

    SalvataggioPartita(Path cartella) {
        this.fileXml = cartella.toAbsolutePath().resolve("partita.xml");
        this.filePrecedente = fileXml.resolveSibling("partita.properties");
    }

    void salva(DuelloGoldrake partita) throws IOException {
        if (!partita.inAttesaDiMossa()) {
            throw new IllegalStateException("Il salvataggio richiede un turno di Goldrake in corso");
        }

        Properties dati = new Properties();
        dati.setProperty("versione", "3");
        dati.setProperty("tappa", partita.tappa.name());
        dati.setProperty("vitaGoldrake", Integer.toString(partita.goldrake.getPuntiVita()));
        dati.setProperty("energiaGoldrake", Integer.toString(partita.goldrake.getEnergia()));
        dati.setProperty("vitaNemico", Integer.toString(partita.nemico.getPuntiVita()));
        partita.tecnicaPremio().ifPresent(tecnica -> dati.setProperty("tecnicaPremio", tecnica.name()));

        Path cartella = Files.createDirectories(fileXml.getParent());
        Path temporaneo = Files.createTempFile(cartella, "partita-", ".tmp");
        try {
            try (var uscita = Files.newOutputStream(temporaneo)) {
                dati.storeToXML(uscita, "Goldrake RPG");
            }
            Files.move(temporaneo, fileXml, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaneo);
        }
    }

    DuelloGoldrake carica() throws IOException {
        try {
            Properties dati = leggiSalvataggio();
            String versione = dati.getProperty("versione", "");
            Tappa tappa = switch (versione) {
                case "1" -> Tappa.LUNA;
                case "2", "3" -> Tappa.valueOf(dati.getProperty("tappa", ""));
                default -> throw new IllegalArgumentException("Versione del salvataggio non supportata");
            };
            Optional<TecnicaPremio> tecnica = versione.equals("3")
                    ? Optional.ofNullable(dati.getProperty("tecnicaPremio")).map(TecnicaPremio::valueOf)
                    : Optional.empty();
            DuelloGoldrake partita = new DuelloGoldrake(tappa, tecnica);
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

    private Properties leggiSalvataggio() throws IOException {
        Properties dati = new Properties();
        try (var ingresso = Files.newInputStream(fileXml)) {
            dati.loadFromXML(ingresso);
        } catch (NoSuchFileException assente) {
            try (var ingresso = Files.newInputStream(filePrecedente)) {
                dati.load(ingresso);
            }
        }
        return dati;
    }

    private static int leggi(Properties dati, String chiave, int minimo, int massimo) {
        int valore = Integer.parseInt(dati.getProperty(chiave));
        if (valore < minimo || valore > massimo) {
            throw new IllegalArgumentException("Valore fuori intervallo: " + chiave);
        }
        return valore;
    }
}
