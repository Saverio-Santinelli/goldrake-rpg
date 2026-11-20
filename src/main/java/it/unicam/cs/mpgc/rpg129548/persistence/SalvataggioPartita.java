package it.unicam.cs.mpgc.rpg129548.persistence;

import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli;
import it.unicam.cs.mpgc.rpg129548.model.DuelloGoldrake;
import it.unicam.cs.mpgc.rpg129548.model.StatisticheDuello;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;
import it.unicam.cs.mpgc.rpg129548.model.TecnicaPremio;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/** Legge e scrive lo stato del duello all'inizio di un turno di Goldrake. */
public final class SalvataggioPartita implements ArchivioPartita {

    private final ConfigurazioneDuelli configurazione;
    private final Path fileXml;
    private final Path filePrecedente;

    public SalvataggioPartita(ConfigurazioneDuelli configurazione, Path cartella) {
        this.configurazione = configurazione;
        this.fileXml = cartella.toAbsolutePath().resolve("partita.xml");
        this.filePrecedente = fileXml.resolveSibling("partita.properties");
    }

    @Override
    public void salva(DuelloGoldrake partita) throws IOException {
        if (!partita.inAttesaDiMossa()) {
            throw new IllegalStateException("Il salvataggio richiede un turno di Goldrake in corso");
        }

        Properties dati = new Properties();
        dati.setProperty("versione", "5");
        dati.setProperty("tappa", partita.tappa().name());
        dati.setProperty("vitaGoldrake", Integer.toString(partita.goldrake().getPuntiVita()));
        dati.setProperty("energiaGoldrake", Integer.toString(partita.goldrake().getEnergia()));
        dati.setProperty("vitaNemico", Integer.toString(partita.nemico().getPuntiVita()));
        dati.setProperty("faseBoss", Integer.toString(partita.faseBoss()));
        partita.tecnicaPremio().ifPresent(tecnica -> dati.setProperty("tecnicaPremio", tecnica.name()));

        partita.statisticheCampagna().forEach((tappa, valori) ->
                dati.setProperty("statistiche." + tappa.name(), serializza(valori)));

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

    @Override
    public DuelloGoldrake carica() throws IOException {
        try {
            Properties dati = leggiSalvataggio();
            String versione = dati.getProperty("versione", "");
            Tappa tappa = switch (versione) {
                case "1" -> Tappa.LUNA;
                case "2", "3", "4", "5" -> Tappa.valueOf(dati.getProperty("tappa", ""));
                default -> throw new IllegalArgumentException("Versione del salvataggio non supportata");
            };
            Optional<TecnicaPremio> tecnica = versione.equals("3") || versione.equals("4") || versione.equals("5")
                    ? Optional.ofNullable(dati.getProperty("tecnicaPremio")).map(TecnicaPremio::valueOf)
                    : Optional.empty();
            int faseBoss = (versione.equals("4") || versione.equals("5")) ? Integer.parseInt(dati.getProperty("faseBoss")) : 0;
            DuelloGoldrake partita = new DuelloGoldrake(configurazione, tappa, tecnica, faseBoss);
            int vitaGoldrake = leggi(dati, "vitaGoldrake", 1, partita.goldrake().getPuntiVitaMassimi());
            int energiaGoldrake = leggi(dati, "energiaGoldrake", 0, partita.goldrake().getEnergia());
            int vitaNemico = leggi(dati, "vitaNemico", 1, partita.nemico().getPuntiVitaMassimi());

            partita.goldrake().subisciDanno(partita.goldrake().getPuntiVita() - vitaGoldrake);
            partita.goldrake().consumaEnergia(partita.goldrake().getEnergia() - energiaGoldrake);
            partita.nemico().subisciDanno(partita.nemico().getPuntiVita() - vitaNemico);
            partita.ripristinaStatistiche(leggiStatistiche(dati));
            return partita;
        } catch (IllegalArgumentException errore) {
            throw new IOException("File di salvataggio non valido", errore);
        }
    }

    private static String serializza(StatisticheDuello dati) {
        return "%d,%d,%d,%d,%d,%d,%s".formatted(dati.inflitti(), dati.subiti(),
                dati.magli(), dati.alabarde(), dati.tecniche(), dati.guardie(), dati.conteggioCompleto());
    }

    private static Map<Tappa, StatisticheDuello> leggiStatistiche(Properties dati) {
        Map<Tappa, StatisticheDuello> statistiche = new EnumMap<>(Tappa.class);
        for (Tappa tappa : Tappa.values()) {
            String valore = dati.getProperty("statistiche." + tappa.name());
            if (valore == null) {
                continue;
            }
            String[] campi = valore.split(",", -1);
            if (campi.length != 7 || !("true".equals(campi[6]) || "false".equals(campi[6]))) {
                throw new IllegalArgumentException("Statistiche non valide per " + tappa);
            }
            statistiche.put(tappa, new StatisticheDuello(Integer.parseInt(campi[0]), Integer.parseInt(campi[1]),
                    Integer.parseInt(campi[2]), Integer.parseInt(campi[3]), Integer.parseInt(campi[4]),
                    Integer.parseInt(campi[5]), Boolean.parseBoolean(campi[6])));
        }
        return statistiche;
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
