package model;

import org.example.model.Asignatura;

import java.time.LocalDate;

public class Inscripcion {

    private int id;
    private int cantidadAlumnos;
    private int margenAlumnos;
    private LocalDate fechaFinInscripcion;
    private Asignatura asignatura;
    private String comision;
    private Perfil perfil; /// A CONFIRMAR


    public Inscripcion(int id, int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion, Asignatura asignatura, String comision, Perfil perfil) {
        this.id = id;
        this.cantidadAlumnos = cantidadAlumnos;
        this.margenAlumnos = margenAlumnos;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.asignatura = asignatura;
        this.comision = comision;
        this.perfil = perfil;
    }

    /// GETTERS AND SETTERS:


    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

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

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }


}
