package org.example.model;

import com.google.gson.Gson;

public class Aula {
    private Integer id;
    private int numero;
    private int capacidad;
    private boolean tieneProyector;
    private boolean tieneTV;

    /// CONSTRUCTORES:
    public Aula(Integer id,int numero, int capacidad, boolean tieneProyector, boolean tieneTV) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.tieneProyector = tieneProyector;
        this.tieneTV = tieneTV;
    }

    public Aula(Integer id){
        this.id = id;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    /**
     * Método para actualizar los campos del Aula
     * @param aula con la nueva información
     */
    public void actualizar(Aula aula) {
        this.numero = aula.getNumero();
        this.capacidad = aula.getCapacidad();
        this.tieneProyector = aula.isTieneProyector();
        this.tieneTV = aula.isTieneTV();
    }
}
