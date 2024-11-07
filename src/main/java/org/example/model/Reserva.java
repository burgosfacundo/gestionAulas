package org.example.model;

import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public class Reserva {
    private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BloqueHorario bloque;
    private Aula aula;
    private Inscripcion inscripcion;
    Set<DayOfWeek> diasSemana;


    public Reserva(Integer id, LocalDate fechaInicio, LocalDate fechaFin, BloqueHorario bloque, Aula aula, Inscripcion inscripcion, Set<DayOfWeek> diasSemana) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.bloque = bloque;
        this.aula = aula;
        this.inscripcion = inscripcion;
        this.diasSemana = diasSemana;
    }

    public Reserva(Integer id){
        this.id = id;
    }



    /// GETTERS AND SETTERS:
    public Integer getId() {return id; }
    public void setId(Integer id) {
        this.id = id;
    }

    public Set<DayOfWeek> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<DayOfWeek> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BloqueHorario getBloque() { return bloque; }
    public void setBloque(BloqueHorario bloque) { this.bloque = bloque; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }

    @Override
    public String toString() {
        return STR."Reserva{id=\{id}, fechaInicio=\{fechaInicio}, fechaFin=\{fechaFin}, bloque=\{bloque}, aula=\{aula}, inscripcion=\{inscripcion}, diasSemana=\{diasSemana}\{'}'}";
    }
}
