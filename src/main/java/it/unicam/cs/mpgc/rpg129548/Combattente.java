package it.unicam.cs.mpgc.rpg129548;

/**
 * Rappresenta un partecipante al combattimento e ne conserva i punti vita.
 */
public class Combattente {

    private final String nome;
    private final int puntiVitaMassimi;
    private int puntiVita;
    private final int energiaMassima;
    private int energia;

    /**
     * Crea un combattente con tutti i punti vita disponibili.
     *
     * @param nome nome del combattente, non nullo né vuoto
     * @param puntiVitaMassimi punti vita iniziali, maggiori di zero
     * @param energiaMassima energia iniziale, maggiore di zero
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public Combattente(String nome, int puntiVitaMassimi, int energiaMassima) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "Il nome non può essere nullo o vuoto");
        }
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException(
                    "I punti vita massimi devono essere maggiori di zero");
        }
        if (energiaMassima <= 0) {
            throw new IllegalArgumentException("L'energia massima deve essere maggiore di zero");
        }
        this.nome = nome;
        this.puntiVitaMassimi = puntiVitaMassimi;
        this.puntiVita = puntiVitaMassimi;
        this.energiaMassima = energiaMassima;
        this.energia = energiaMassima;
    }

    public String getNome() {
        return nome;
    }

    public int getPuntiVitaMassimi() {
        return puntiVitaMassimi;
    }

    public int getPuntiVita() {
        return puntiVita;
    }
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
        puntiVita = Math.max(0, puntiVita - danno);
    }

    public boolean isSconfitto() {
        return puntiVita == 0;
    }
    public int getEnergia() {
        return energia;
    }

    public boolean consumaEnergia(int costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("Il costo non può essere negativo");
        }
        if (costo > energia) {
            return false;
        }
        energia -= costo;
        return true;
    }
    public void ricaricaEnergia(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La ricarica non può essere negativa");
        }
        energia += Math.min(quantita, energiaMassima - energia);
    }
}