package org.example.model;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Set<DayOfWeek> diasSemana;  // Días de la semana solicitados
    private BloqueHorario bloqueHorario;  // Bloque horario solicitado
    private String comentarioEstado;  // Comentario del estado (motivo de rechazo, etc.)
    private String comentarioProfesor;  // Motivo del profesor para solicitar el cambio
    private LocalDateTime fechaHoraSolicitud;  // Fecha y hora en que se realizó la solicitud


    // Constructor, getters y setters
    public SolicitudCambioAula(Integer id, Profesor profesor, Reserva reservaOriginal, Aula nuevaAula,
                               EstadoSolicitud estado, TipoSolicitud tipoSolicitud,
                               LocalDate fechaInicio, LocalDate fechaFin, Set<DayOfWeek> diasSemana,
                               BloqueHorario bloqueHorario, String comentarioEstado,
                               String comentarioProfesor, LocalDateTime fechaHoraSolicitud) {
        this.id = id;
        this.profesor = profesor;
        this.reservaOriginal = reservaOriginal;
        this.nuevaAula = nuevaAula;
        this.estado = estado;
        this.tipoSolicitud = tipoSolicitud;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.diasSemana = diasSemana;
        this.bloqueHorario = bloqueHorario;
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

    public Set<DayOfWeek> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<DayOfWeek> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public BloqueHorario getBloqueHorario() {
        return bloqueHorario;
    }

    public void setBloqueHorario(BloqueHorario bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
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
    public String toString() {
        return "SolicitudCambioAula{" +
                "id=" + id +
                ", profesor=" + profesor +
                ", reservaOriginal=" + reservaOriginal +
                ", nuevaAula=" + nuevaAula +
                ", estado=" + estado +
                ", tipoSolicitud=" + tipoSolicitud +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", diasSemana=" + diasSemana +
                ", bloqueHorario=" + bloqueHorario +
                ", comentarioEstado='" + comentarioEstado + '\'' +
                ", comentarioProfesor='" + comentarioProfesor + '\'' +
                ", fechaHoraSolicitud=" + fechaHoraSolicitud +
                '}';
    }
}


