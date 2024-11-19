package org.example.model;


import java.util.Objects;

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
        return Objects.equals(id, aula.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Aula (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Número: %d
                ║ Capacidad: %d
                ║ Tiene Proyector: %s
                ║ Tiene TV: %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                numero,
                capacidad,
                tieneProyector ? "Sí" : "No",
                tieneTV ? "Sí" : "No"
        ).trim();
    }

}
