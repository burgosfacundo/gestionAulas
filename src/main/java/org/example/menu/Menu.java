package org.example.menu;

import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.security.Seguridad;
import org.example.utils.Utils;

public class Menu {

    private final MenuAdministrador menuAdministrador = new MenuAdministrador();
    private final MenuProfesor menuProfesor = new MenuProfesor();
    private final Seguridad seguridad = new Seguridad();

    /**
     * Método para iniciar el menu principal de la aplicación
     */
    public void iniciarMenu() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Sistema de Gestión de Aulas");
            System.out.println("======================================");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            System.out.println("======================================");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> iniciarSesion();
                case 2 -> {
                    salir = true;
                    System.out.println("Saliendo del sistema. ¡Hasta luego!");
                }
                default -> System.out.println("Opción inválida.");
            }
        }
        Utils.cerrarScanner();
    }

    /**
     * Método para manejar la autenticación de usuario
     * y derivar al menu que le corresponde
     */
    private void iniciarSesion() {
        Usuario usuario = autenticarUsuario();

        if (usuario != null) {
            redireccionarSegunRol(usuario);
        } else {
            System.out.println("Autenticación fallida. Intente nuevamente.");
        }
    }

    /**
     * Método para manejar la redirección al menú correspondiente según el rol del usuario
     * @param usuario Usuario autenticado
     */
    private void redireccionarSegunRol(Usuario usuario) {
        String rol = usuario.getRol().getNombre().toLowerCase();
        switch (rol) {
            case "administrador" -> menuAdministrador.iniciarMenuAdmin(usuario);
            case "profesor" -> menuProfesor.iniciarMenuProfesor(usuario);
            default -> System.out.println("Error: Rol desconocido.");
        }
    }


    /**
     * Método para autenticar al Usuario
     * @return Usuario autenticado o null
     */
    private Usuario autenticarUsuario() {
        Usuario usuario = null;
        boolean intentarDeNuevo = true;

        while (usuario == null && intentarDeNuevo) {
            String username = Utils.leerTexto("Ingrese su nombre de usuario: ");
            String contrasenia = Utils.leerTexto("Ingrese su contraseña: ");


            try {
                usuario = seguridad.autenticar(username, contrasenia);
            } catch (AutenticacionException | JsonNotFoundException | NotFoundException e) {
                System.out.println(STR."Error: \{e.getMessage()}");
                intentarDeNuevo = Utils.leerConfirmacion("¿Desea intentar nuevamente?");
            }
        }
        return usuario;
    }
}

