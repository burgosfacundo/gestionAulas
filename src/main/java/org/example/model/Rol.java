package org.example.model;

import org.example.enums.Permisos;
import org.example.utils.Utils;

import java.util.List;
import java.util.Objects;

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
     * Método para saber si el rol tiene un Permiso
     * @param permiso enum Permiso que hay que verificar en el Rol
     * @return boolean si tiene permiso o no
     */
    public boolean tienePermiso(Permisos permiso) {
        return this.permisos.contains(permiso);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return Objects.equals(id, rol.id) && Objects.equals(nombre, rol.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Rol (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Nombre: %s
                ║ Permisos:
                %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                nombre,
                Utils.indentString(Utils.formatListItems(this.permisos), 6)
        ).trim();
    }
}
