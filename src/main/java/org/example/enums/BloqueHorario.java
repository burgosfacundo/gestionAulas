package org.example.enums;

import java.time.LocalTime;

public enum BloqueHorario {

    MANIANA_PRIMER_BLOQUE(LocalTime.of(8,0),LocalTime.of(10,0)),
    MANIANA_SEGUNDO_BLOQUE(LocalTime.of(10,30),LocalTime.of(12,30)),
    TARDE_PRIMER_BLOQUE(LocalTime.of(13,0),LocalTime.of(15,0)),
    TARDE_SEGUNDO_BLOQUE(LocalTime.of(15,30),LocalTime.of(17,30)),
    NOCHE_PRIMER_BLOQUE(LocalTime.of(18,0),LocalTime.of(20,0)),
    NOCHE_SEGUNDO_BLOQUE(LocalTime.of(20,30),LocalTime.of(22,30));

    private final LocalTime inicio;
    private final LocalTime fin;

    BloqueHorario(LocalTime inicio, LocalTime fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    // Getters
    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    @Override
    public String toString() {
        return String.format("%s (%s hs - %s hs)",
                name().replace("_", " ").toLowerCase(),
                inicio,
                fin);
    }


}

