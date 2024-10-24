package org.example.security;

import org.example.enums.Permisos;
import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.service.UsuarioService;
import org.example.model.Usuario;

/**
 * Clase de seguridad de la app
 * Se encarga del inicio de Sesion y de validar permisos
 */
public class Seguridad {
    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Metodo para iniciar sesion
     * @param username nombre de usuario
     * @param password contraseña
     * @return Usuario que inicia sesion
     * @throws AutenticacionException si el username o password son incorrectos
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Usuario autenticar(String username, String password) throws AutenticacionException, JsonNotFoundException {
        //Verificamos si existe un usuario con ese username y password a traves de un stream
        return usuarioService.listar().stream()
                .filter(usuario -> usuario.getUsername().equals(username) &&
                        usuario.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new AutenticacionException("Usuario o contraseña incorrectos"));
    }


    /**
     * Metodo para validar permisos del usuario
     * @param usuario usuario que quiere realizar una accion
     * @param permisos enum que indica que es lo que quiere hacer el usuario
     * @return boolean que indica si tiene permisos o no
     */
    public boolean verificarPermiso(Usuario usuario, Permisos permisos) {
        //Llamamos al metodo que devuelve si el usuario tiene permiso segun su Rol
        return usuario.getRol().tienePermiso(permisos);
    }
}

