package org.example.model;


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
    public String toString() {
        return String.format(
                "{\n\tID: %d\n\tNombre: '%s'\n\tCódigo: %s\n\tRequiere Laboratorio: %b\n}",
                id, nombre, codigo, requiereLaboratorio
        );
    }

}
