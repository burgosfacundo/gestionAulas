package org.example.model;

import org.example.utils.Utils;

import java.time.LocalDate;
import java.util.Objects;

public class Inscripcion {
    private Integer id;
    private int cantidadAlumnos;
    private int margenAlumnos;
    private LocalDate fechaFinInscripcion;
    private Asignatura asignatura;
    private String comision;
    private Profesor profesor;


    public Inscripcion(Integer id, int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion, Asignatura asignatura, String comision, Profesor profesor) {
        this.id = id;
        this.cantidadAlumnos = cantidadAlumnos;
        this.margenAlumnos = margenAlumnos;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.asignatura = asignatura;
        this.comision = comision;
        this.profesor = profesor;
    }

    public Inscripcion(Integer id){
        this.id = id;
    }

    /// GETTERS AND SETTERS:


    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public int getCantidadAlumnos() { return cantidadAlumnos; }
    public void setCantidadAlumnos(int cantidadAlumnos) { this.cantidadAlumnos = cantidadAlumnos; }

    public int getMargenAlumnos() { return margenAlumnos; }
    public void setMargenAlumnos(int margenAlumnos) { this.margenAlumnos = margenAlumnos; }

    public LocalDate getFechaFinInscripcion() { return fechaFinInscripcion; }
    public void setFechaFinInscripcion(LocalDate fechaFinInscripcion) { this.fechaFinInscripcion = fechaFinInscripcion; }

    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }

    public String getComision() { return comision; }
    public void setComision(String comision) { this.comision = comision; }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscripcion that = (Inscripcion) o;
        return Objects.equals(id, that.id) && Objects.equals(asignatura, that.asignatura) && Objects.equals(comision, that.comision) && Objects.equals(profesor, that.profesor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asignatura, comision, profesor);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Inscripción (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Cantidad de Alumnos: %d
                ║ Margen de Alumnos: %d
                ║ Fecha Fin Inscripción: %s
                ║ Asignatura:
                %s
                ║ Comisión: %s
                ║ Profesor:
                %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                cantidadAlumnos,
                margenAlumnos,
                fechaFinInscripcion,
                Utils.indentString(asignatura.toString(), 6),
                comision,
                Utils.indentString(profesor.toString(), 6)
        ).trim();
    }
}
