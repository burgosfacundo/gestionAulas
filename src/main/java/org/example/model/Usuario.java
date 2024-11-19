package org.example.model;


import org.example.utils.Utils;

import java.util.Objects;

// Clase que representa la entidad Usuario
public class Usuario {
    private Integer id;
    private String username;
    private String password;
    private Rol rol;
    private Profesor profesor;

    public Usuario(Integer id,String username, String password, Rol rol,Profesor profesor) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.profesor = profesor;
    }
    public Usuario(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id){ this.id = id;}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public Profesor getProfesor() {
        return profesor;
    }
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return String.format(
                """
                ╔═══════════════════════════════════════════════╗
                ║ Usuario (ID: %d)
                ╟───────────────────────────────────────────────╢
                ║ Username: %s
                ║ Password: %s
                ║ Rol:
                %s
                ║ Profesor:
                %s
                ╚═══════════════════════════════════════════════╝
                """,
                id,
                username,
                password,
                Utils.indentString(rol.toString(), 6),
                Utils.indentString(profesor.toString(), 6)
        ).trim();
    }


}
