package org.example.model;

import com.google.gson.Gson;
import org.example.enums.Permisos;

import java.util.List;

// Clase que representa la entidad Rol
public class Rol {
    private Integer id;
    private String nombre;
    private List<Permisos> permisos;

    public Rol(Integer id,String nombre, List<Permisos> permisos) {
        this.id = id;
        this.nombre = nombre;
        this.permisos = permisos;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id){ this.id = id;}
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Permisos> getPermisos() { return permisos; }
    public void setPermisos(List<Permisos> permisos) { this.permisos = permisos; }

    /**
     * Método para convertir un rol a JSON
     * @return String que representa la cadena JSON de esta clase
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Método para saber si el rol tiene un Permiso
     * @param permiso enum Permiso que hay que verificar en el Rol
     * @return boolean si tiene permiso o no
     */
    public boolean tienePermiso(Permisos permiso) {
        return this.permisos.contains(permiso);
    }

    @Override
    public String toString() {
        return STR."Rol{id=\{id}, nombre='\{nombre}\{'\''}, permisos=\{permisos}\{'}'}";
    }
}
