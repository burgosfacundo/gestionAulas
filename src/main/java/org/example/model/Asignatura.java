package org.example.model;

import com.google.gson.Gson;

public class Asignatura {
    private int id;
    private String nombre;
    private int codigo;
    boolean requiereLaboratorio;


    /// CONSTRUCTOR:
    public Asignatura(int id,String nombre, int codigo, boolean requiereLaboratorio) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.requiereLaboratorio = requiereLaboratorio;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
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
        return STR."Asignatura{id=\{id}, nombre='\{nombre}\{'\''}, codigo=\{codigo}, requiereLaboratorio=\{requiereLaboratorio}\{'}'}";
    }

    /**
     * Método para convertir una asignatura a JSON
     * @return String que representa la cadena JSON de esta clase
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
