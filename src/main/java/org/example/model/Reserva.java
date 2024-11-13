package org.example.model;

import org.example.enums.BloqueHorario;
import org.example.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Reserva {
    private Integer id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Aula aula;
    private Inscripcion inscripcion;
    private Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;


    public Reserva(Integer id, LocalDate fechaInicio, LocalDate fechaFin, Aula aula,
                   Inscripcion inscripcion, Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.aula = aula;
        this.inscripcion = inscripcion;
        this.diasYBloques = diasYBloques;
    }

    public Reserva(Integer id){
        this.id = id;
    }



    /// GETTERS AND SETTERS:
    public Integer getId() {return id; }
    public void setId(Integer id) {
        this.id = id;
    }

    public Map<DayOfWeek, Set<BloqueHorario>> getDiasYBloques() {
        return diasYBloques;
    }

    public void setDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
        this.diasYBloques = diasYBloques;
    }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id) && Objects.equals(aula, reserva.aula) && Objects.equals(inscripcion, reserva.inscripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aula, inscripcion);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Reserva (ID: %d)                             \s
                ╟───────────────────────────────────────────────╢
                ║ Fecha Inicio: %s
                ║ Fecha Fin: %s
                ║ Aula:
                %s
                ║ Inscripción:
                %s
                ║ Días y Bloques:
                %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                fechaInicio,
                fechaFin,
                Utils.indentString(aula.toString(), 6),
                Utils.indentString(inscripcion.toString(), 6),
                Utils.indentString(Utils.formatDiasYBloques(diasYBloques), 6)
        );
    }
}
