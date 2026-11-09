package it.unicam.cs.mpgc.rpg129548;

/** Luogo e avversario di ciascun duello della campagna. */
enum Tappa {
    VALLE("Terra / Valle montana", "Esploratore di Vega", 55, "Lame gemelle", 9),
    CITTA("Terra / Città di notte", "Guardiano di Vega", 70, "Raffica protonica", 12),
    LUNA("Luna / Base di Vega", "Mostro di Vega", 80, "Artiglio lacerante", 15);

    final String luogo;
    final String nemico;
    final int vitaNemico;
    final String mossaNemico;
    final int dannoNemico;

    Tappa(String luogo, String nemico, int vitaNemico, String mossaNemico, int dannoNemico) {
        this.luogo = luogo;
        this.nemico = nemico;
        this.vitaNemico = vitaNemico;
        this.mossaNemico = mossaNemico;
        this.dannoNemico = dannoNemico;
    }

    Tappa successiva() {
        return switch (this) {
            case VALLE -> CITTA;
            case CITTA -> LUNA;
            case LUNA -> throw new IllegalStateException("La Luna è l'ultima tappa");
        };
    }
}
