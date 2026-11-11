package it.unicam.cs.mpgc.rpg129548;

/** Luoghi e ordine dei duelli della campagna. */
enum Tappa {
    VALLE("Terra / Valle montana"),
    CITTA("Terra / Città di notte"),
    LUNA("Luna / Base di Vega");

    final String luogo;

    Tappa(String luogo) {
        this.luogo = luogo;
    }

    Tappa successiva() {
        return switch (this) {
            case VALLE -> CITTA;
            case CITTA -> LUNA;
            case LUNA -> throw new IllegalStateException("La Luna è l'ultima tappa");
        };
    }
}
