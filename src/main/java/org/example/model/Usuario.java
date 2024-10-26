package org.example.model;


// Clase que representa la entidad Usuario
public class Usuario {
    private int id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(int id,String username, String password, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }
    public void setId(int id){ this.id = id;}
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

    @Override
    public String toString() {
        return STR."Usuario{id=\{id}, username='\{username}\{'\''}, password='\{password}\{'\''}, rol=\{rol}\{'}'}";
    }
}
