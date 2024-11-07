package org.example.model;

import java.time.LocalDate;

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
    public String toString() {
        return STR."Inscripcion{id=\{id}, cantidadAlumnos=\{cantidadAlumnos}, margenAlumnos=\{margenAlumnos}, fechaFinInscripcion=\{fechaFinInscripcion}, asignatura=\{asignatura}, comision='\{comision}\{'\''}, profesor=\{profesor}\{'}'}";
    }
}
