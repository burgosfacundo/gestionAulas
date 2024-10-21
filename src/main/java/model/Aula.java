package model;

public class Aula {

    private int numero;
    private int capacidad;
    boolean tieneProyector;
    boolean tieneTV;


    /// CONSTRUCTOR:
    public Aula(int numero, int capacidad, boolean tieneProyector, boolean tieneTV) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.tieneProyector = tieneProyector;
        this.tieneTV = tieneTV;
    }

    /// CONSTRUCTOR VACIO POR SI SE NECESITA PARA PRUEBAS O ALGUN USO:
    public Aula() {}




    /// GETTERS AND SETTERS:
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public boolean isTieneProyector() { return tieneProyector; }
    public void setTieneProyector(boolean tieneProyector) { this.tieneProyector = tieneProyector; }

    public boolean isTieneTV() { return tieneTV; }
    public void setTieneTV(boolean tieneTV) { this.tieneTV = tieneTV; }


}
