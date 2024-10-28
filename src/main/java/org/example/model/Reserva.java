package org.example.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Inscripcion;
import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public class Reserva {
    private int id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BloqueHorario bloque;
    private Aula aula;
    private Inscripcion inscripcion;
    Set<DayOfWeek> diasSemana;


    public Reserva(int id, LocalDate fechaInicio, LocalDate fechaFin, BloqueHorario bloque, Aula aula, Inscripcion inscripcion, Set<DayOfWeek> diasSemana) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.bloque = bloque;
        this.aula = aula;
        this.inscripcion = inscripcion;
        this.diasSemana = diasSemana;
    }



    /// GETTERS AND SETTERS:
    public int getId() {return id; }
    public void setId(int id) {
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

    // REVISAR
    public JsonObject reservaToJson(){
        JsonObject reservaEnJson = new JsonObject();
        reservaEnJson.addProperty("fecha_inicio", this.fechaInicio.toString());
        reservaEnJson.addProperty("fecha_fin", this.fechaFin.toString());
        reservaEnJson.addProperty("bloque", this.bloque.toString());
        reservaEnJson.addProperty("aula", this.aula.toString());
        reservaEnJson.addProperty("inscripcion", this.inscripcion.toString());

        return reservaEnJson;
    }

    // revisar cual es mas efectivo
    public JsonObject reservaAJson(){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(this);
        return jsonElement.getAsJsonObject();
    }


    @Override
    public String toString() {
        return STR."Reserva{id=\{id}, fechaInicio=\{fechaInicio}, fechaFin=\{fechaFin}, bloque=\{bloque}, aula=\{aula}, inscripcion=\{inscripcion}, diasSemana=\{diasSemana}\{'}'}";
    }
}
