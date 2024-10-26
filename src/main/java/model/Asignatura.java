package model;

public class Asignatura {

    private String nombre;
    private int codigo; // LAS ASIGNATURAS NO POSEEN UN CODIGO? PODRIA SER P/LOGICA DE DB
    boolean requiereLaboratorio;


    /// CONSTRUCTOR:
    public Asignatura(String nombre, int codigo, boolean requiereLaboratorio) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.requiereLaboratorio = requiereLaboratorio;
    }


    /// GETTERS AND SETTERS:
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public boolean isRequiereLaboratorio() { return requiereLaboratorio; }
    public void setRequiereLaboratorio(boolean requiereLaboratorio) { this.requiereLaboratorio = requiereLaboratorio;}
}
