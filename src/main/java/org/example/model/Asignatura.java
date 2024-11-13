package org.example.model;


import java.util.Objects;

public class Asignatura {
    private Integer id;
    private String nombre;
    private int codigo;
    boolean requiereLaboratorio;


    /// CONSTRUCTOR:
    public Asignatura(Integer id,String nombre, int codigo, boolean requiereLaboratorio) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.requiereLaboratorio = requiereLaboratorio;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public boolean isRequiereLaboratorio() { return requiereLaboratorio; }
    public void setRequiereLaboratorio(boolean requiereLaboratorio) { this.requiereLaboratorio = requiereLaboratorio;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asignatura that = (Asignatura) o;
        return codigo == that.codigo && Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, codigo);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Asignatura (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Nombre: %s
                ║ Código: %s
                ║ Requiere Laboratorio: %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                nombre,
                codigo,
                requiereLaboratorio ? "Sí" : "No"
        ).trim();
    }


}
