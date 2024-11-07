package org.example.model.dto;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record SolicitudCambioAulaDTO(int id, int idProfesor, int idReserva, int idAula, EstadoSolicitud estadoSolicitud,
                                     TipoSolicitud tipoSolicitud, LocalDate fechaInicio, LocalDate fechaFin,
                                     Set<DayOfWeek> diasSemana, BloqueHorario bloqueHorario, String comentarioEstado,
                                     String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
}
