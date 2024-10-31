package org.example.model.dto;

import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ReservaDTO (int id, LocalDate fechaInicio, LocalDate fechaFin, BloqueHorario bloque,
                          int idAula, int idInscripcion, Set<DayOfWeek> diasSemana) {
}
