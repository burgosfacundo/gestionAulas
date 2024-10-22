package org.example.model;

import java.time.LocalDate;

public class Inscripcion {

    private int cantidadAlumnos;
    private int margenAlumnos;
    private LocalDate fechaFinInscripcion;
    private Asignatura asignatura;
    private String comision;


    public Inscripcion(int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion, Asignatura asignatura, String comision) {
        this.cantidadAlumnos = cantidadAlumnos;
        this.margenAlumnos = margenAlumnos;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.asignatura = asignatura;
        this.comision = comision;
    }

    /// GETTERS AND SETTERS:
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
}
