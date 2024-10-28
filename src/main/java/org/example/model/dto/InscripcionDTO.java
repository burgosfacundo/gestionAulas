package org.example.model.dto;

import java.time.LocalDate;

public record InscripcionDTO(int id, int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion,
                             int idAsignatura, int idComision, int idProfesor) {
}
