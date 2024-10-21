package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import enums.BloqueHorario;

import java.time.LocalDate;

public class Reserva {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BloqueHorario bloque;
    private Aula aula;
    private Inscripcion inscripcion;


    public Reserva(LocalDate fechaInicio, LocalDate fechaFin, BloqueHorario bloque, Aula aula, Inscripcion inscripcion) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.bloque = bloque;
        this.aula = aula;
        this.inscripcion = inscripcion;
    }



    /// GETTERS AND SETTERS:
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

}
