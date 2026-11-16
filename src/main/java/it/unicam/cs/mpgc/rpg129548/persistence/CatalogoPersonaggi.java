package it.unicam.cs.mpgc.rpg129548.persistence;

import it.unicam.cs.mpgc.rpg129548.model.Attacco;
import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli;
import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli.AttaccoSpeciale;
import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli.Avversario;
import it.unicam.cs.mpgc.rpg129548.model.ConfigurazioneDuelli.Profilo;
import it.unicam.cs.mpgc.rpg129548.model.Tappa;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Carica le statistiche iniziali e i contrattacchi dal catalogo XML del gioco. */
public final class CatalogoPersonaggi {

    private static final String RISORSA = "/personaggi.xml";

    private CatalogoPersonaggi() {
    }

    public static ConfigurazioneDuelli carica() {
        try (InputStream xml = CatalogoPersonaggi.class.getResourceAsStream(RISORSA)) {
            if (xml == null) {
                throw new IOException("Risorsa mancante: " + RISORSA);
            }
            DocumentBuilderFactory parser = DocumentBuilderFactory.newInstance();
            parser.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            parser.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            parser.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Element radice = parser.newDocumentBuilder().parse(xml).getDocumentElement();
            if (!radice.getTagName().equals("personaggi")) {
                throw new IllegalArgumentException("Elemento principale atteso: personaggi");
            }
            return new ConfigurazioneDuelli(leggiProfilo(elementoUnico(radice, "goldrake")),
                    leggiAvversari(radice));
        } catch (IOException | ParserConfigurationException | SAXException | IllegalArgumentException errore) {
            throw new IllegalStateException("Impossibile caricare " + RISORSA + ": " + errore.getMessage(),
                    errore);
        }
    }

    private static Map<Tappa, Avversario> leggiAvversari(Element radice) {
        Map<Tappa, Avversario> avversari = new EnumMap<>(Tappa.class);
        NodeList boss = radice.getElementsByTagName("boss");
        for (int indice = 0; indice < boss.getLength(); indice++) {
            Element elemento = (Element) boss.item(indice);
            Tappa tappa = Tappa.valueOf(elemento.getAttribute("tappa"));
            Element mossa = elementoUnico(elemento, "contrattacco");
            Attacco contrattacco = new Attacco(mossa.getAttribute("nome"),
                    leggiIntero(mossa, "danno"), 0);
            Element speciale = elementoUnico(elemento, "speciale");
            AttaccoSpeciale attaccoSpeciale = new AttaccoSpeciale(
                    new Attacco(speciale.getAttribute("nome"), leggiIntero(speciale, "danno"), 0),
                    leggiIntero(speciale, "dopo"), speciale.getAttribute("avviso"));
            if (avversari.putIfAbsent(tappa, new Avversario(leggiProfilo(elemento), contrattacco, attaccoSpeciale)) != null) {
                throw new IllegalArgumentException("Boss duplicato per la tappa " + tappa);
            }
        }
        for (Tappa tappa : Tappa.values()) {
            if (!avversari.containsKey(tappa)) {
                throw new IllegalArgumentException("Manca il boss della tappa " + tappa);
            }
        }
        return avversari;
    }

    private static Element elementoUnico(Element contenitore, String nome) {
        NodeList elementi = contenitore.getElementsByTagName(nome);
        if (elementi.getLength() != 1) {
            throw new IllegalArgumentException("È richiesto un solo elemento " + nome);
        }
        return (Element) elementi.item(0);
    }

    private static Profilo leggiProfilo(Element elemento) {
        return new Profilo(elemento.getAttribute("nome"),
                leggiIntero(elemento, "puntiVita"), leggiIntero(elemento, "energia"));
    }

    private static int leggiIntero(Element elemento, String attributo) {
        try {
            return Integer.parseInt(elemento.getAttribute(attributo));
        } catch (NumberFormatException errore) {
            throw new IllegalArgumentException("Valore non valido per " + attributo
                    + " in " + elemento.getTagName(), errore);
        }
    }

}
