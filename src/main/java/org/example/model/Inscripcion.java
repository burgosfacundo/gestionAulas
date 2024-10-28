package model;

import com.google.gson.Gson;
import org.example.model.Asignatura;
import org.example.model.Profesor;

import java.time.LocalDate;

public class Inscripcion {
    private int id;
    private int cantidadAlumnos;
    private int margenAlumnos;
    private LocalDate fechaFinInscripcion;
    private Asignatura asignatura;
    private String comision;
    private Profesor profesor;


    public Inscripcion(int id, int cantidadAlumnos, int margenAlumnos, LocalDate fechaFinInscripcion, Asignatura asignatura, String comision, Profesor profesor) {
        this.id = id;
        this.cantidadAlumnos = cantidadAlumnos;
        this.margenAlumnos = margenAlumnos;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.asignatura = asignatura;
        this.comision = comision;
        this.profesor = profesor;
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

    /**
     * Método para convertir un inscripcion a JSON
     * @return String que representa la cadena JSON de esta clase
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
