package org.example.model;


import java.util.Objects;

// Clase que representa la entidad Rol
public class Profesor {
    private Integer id;
    private String nombre;
    private String apellido;
    private String matricula;

    public Profesor(Integer id,String nombre, String apellido, String matricula) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }
    public Profesor(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){this.id = id;}

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profesor profesor = (Profesor) o;
        return Objects.equals(id, profesor.id) && Objects.equals(matricula, profesor.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, matricula);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Profesor (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Nombre: %s
                ║ Apellido: %s
                ║ Matrícula: %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                nombre,
                apellido,
                matricula
        ).trim();
    }
}
