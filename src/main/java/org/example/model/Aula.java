package org.example.model;

import com.google.gson.Gson;

public class Aula {
    private int id;
    private int numero;
    private int capacidad;
    boolean tieneProyector;
    boolean tieneTV;


    /// CONSTRUCTOR:
    public Aula(int id,int numero, int capacidad, boolean tieneProyector, boolean tieneTV) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.tieneProyector = tieneProyector;
        this.tieneTV = tieneTV;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getNumero() { return numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public boolean isTieneProyector() { return tieneProyector; }
    public void setTieneProyector(boolean tieneProyector) { this.tieneProyector = tieneProyector; }

    public boolean isTieneTV() { return tieneTV; }
    public void setTieneTV(boolean tieneTV) { this.tieneTV = tieneTV; }

    @Override
    public String toString() {
        return STR."Aula{id=\{id}, numero=\{numero}, capacidad=\{capacidad}, tieneProyector=\{tieneProyector}, tieneTV=\{tieneTV}\{'}'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aula aula = (Aula) o;
        return numero == aula.numero;
    }

    @Override
    public int hashCode() {
        return numero;
    }

    /**
     * Método para convertir un aula a JSON
     * @return String que representa la cadena JSON de esta clase
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
