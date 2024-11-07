package org.example.model;


// Clase que representa la entidad Usuario
public class Usuario {
    private Integer id;
    private String username;
    private String password;
    private Rol rol;

    public Usuario(Integer id,String username, String password, Rol rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
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

    @Override
    public String toString() {
        return STR."Usuario{id=\{id}, username='\{username}\{'\''}, password='\{password}\{'\''}, rol=\{rol}\{'}'}";
    }
}
