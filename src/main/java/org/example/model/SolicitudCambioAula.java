package org.example.model;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;
import org.example.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SolicitudCambioAula {
    private Integer id;  // Id único de la solicitud
    private Profesor profesor;  // Profesor que hace la solicitud
    private Reserva reservaOriginal;  // Reserva que se desea cambiar
    private Aula nuevaAula;  // Aula solicitada
    private EstadoSolicitud estado;  // Estado de la solicitud (Pendiente, Aprobada, Rechazada, etc.)
    private TipoSolicitud tipoSolicitud;  // Tipo de cambio solicitado (temporal o cuatrimestral)
    private LocalDate fechaInicio;  // Fecha de inicio de la nueva reserva
    private LocalDate fechaFin;  // Fecha de fin de la nueva reserva
    private Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;  // Días de la semana y bloques solicitados
    private String comentarioEstado;  // Comentario del estado (motivo de rechazo, etc.)
    private String comentarioProfesor;  // Motivo del profesor para solicitar el cambio
    private LocalDateTime fechaHoraSolicitud;  // Fecha y hora en que se realizó la solicitud


    // Constructor, getters y setters
    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Aula nuevaAula,
                               TipoSolicitud tipoSolicitud, LocalDate fechaInicio, LocalDate fechaFin,
                               Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                               String comentarioProfesor) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevaAula = nuevaAula;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasYBloques = diasYBloques;
        this.comentarioProfesor = comentarioProfesor;
        this.fechaHoraSolicitud = LocalDateTime.now();
    }

    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Aula nuevaAula,
                               EstadoSolicitud estadoSolicitud, TipoSolicitud tipoSolicitud,
                               LocalDate fechaInicio, LocalDate fechaFin, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques,
                               String comentarioEstado, String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevaAula = nuevaAula;
        this.estado = estadoSolicitud;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasYBloques = diasYBloques;
        this.comentarioEstado = comentarioEstado;
        this.comentarioProfesor = comentarioProfesor;
        this.fechaHoraSolicitud = fechaHoraSolicitud;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Reserva getReservaOriginal() {
        return reservaOriginal;
    }

    public void setReservaOriginal(Reserva reservaOriginal) {
        this.reservaOriginal = reservaOriginal;
    }

    public Aula getNuevaAula() {
        return nuevaAula;
    }

    public void setNuevaAula(Aula nuevaAula) {
        this.nuevaAula = nuevaAula;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Map<DayOfWeek, Set<BloqueHorario>> getDiasYBloques() {
        return diasYBloques;
    }

    public void setDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
        this.diasYBloques = diasYBloques;
    }

    public String getComentarioEstado() {
        return comentarioEstado;
    }

    public void setComentarioEstado(String comentarioEstado) {
        this.comentarioEstado = comentarioEstado;
    }

    public String getComentarioProfesor() {
        return comentarioProfesor;
    }

    public void setComentarioProfesor(String comentarioProfesor) {
        this.comentarioProfesor = comentarioProfesor;
    }

    public LocalDateTime getFechaHoraSolicitud() {
        return fechaHoraSolicitud;
    }

    public void setFechaHoraSolicitud(LocalDateTime fechaHoraSolicitud) {
        this.fechaHoraSolicitud = fechaHoraSolicitud;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitudCambioAula that = (SolicitudCambioAula) o;
        return Objects.equals(id, that.id) && Objects.equals(profesor, that.profesor) && Objects.equals(reservaOriginal, that.reservaOriginal) && Objects.equals(nuevaAula, that.nuevaAula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, profesor, reservaOriginal, nuevaAula);
    }

    @Override
    public String toString() {
        return String.format(
                """
                        ╔═══════════════════════════════════════════════╗
                        ║ Solicitud de Cambio de Aula (ID: %d)
                        ╟───────────────────────────────────────────────╢
                        ║ Profesor:
                        %s
                        ║ Reserva Original:
                        %s
                        ║ Nueva Aula:
                        %s
                        ║ Estado: %s
                        ║ Tipo de Solicitud: %s
                        ║ Fecha Inicio: %s
                        ║ Fecha Fin: %s
                        ║ Días y Bloques:
                        %s
                        ║ Comentario Estado: %s
                        ║ Comentario Profesor: %s
                        ║ Fecha y Hora Solicitud: %s
                        ╚═══════════════════════════════════════════════╝
                        """,
                id,
                Utils.indentString(profesor.toString(), 6),
                Utils.indentString(reservaOriginal.toString(), 6),
                Utils.indentString(nuevaAula.toString(), 6),
                estado,
                tipoSolicitud,
                fechaInicio,
                fechaFin,
                Utils.indentString(Utils.formatDiasYBloques(diasYBloques), 6),
                comentarioEstado.isEmpty() ? "Sin comentario" : comentarioEstado,
                comentarioProfesor.isEmpty() ? "Sin comentario" : comentarioProfesor,
                fechaHoraSolicitud
        ).trim();
    }
}


