package org.example.model;

import com.google.gson.Gson;

// Clase que representa la entidad Rol
public class Profesor {
    private int id;
    private String nombre;
    private String apellido;
    private String matricula;

    public Profesor(int id,String nombre, String apellido, String matricula) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){this.id = id;}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return STR."Profesor{id=\{id}, nombre='\{nombre}\{'\''}, apellido='\{apellido}\{'\''}, matricula='\{matricula}\{'\''}\{'}'}";
    }

    /**
     * Método para convertir un profesor a JSON
     * @return String que representa la cadena JSON de esta clase
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
