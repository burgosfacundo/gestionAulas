package org.example.menu;

import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.model.Usuario;
import org.example.security.Seguridad;

import java.util.Scanner;

import java.util.InputMismatchException;

public class Menu {

    private final MenuAdministrador menuAdministrador = new MenuAdministrador();
    private final MenuProfesor menuProfesor = new MenuProfesor();
    private final Seguridad seguridad = new Seguridad();

    /**
     * Método para iniciar el menu principal de la aplicación
     */
    public void iniciarMenu() {
        System.out.println("Bienvenido al Sistema de Gestión de Aulas");

        boolean salir = false;
        Scanner scanner = new Scanner(System.in);

        while (!salir) {
            mostrarOpciones();

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> iniciarSesion(scanner);
                    case 2 -> {
                        salir = true;
                        System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un número.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Método para printear opciones del menu
     */
    private void mostrarOpciones() {
        System.out.println("====================================");
        System.out.println("Elija una opción:");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Salir");
        System.out.println("====================================");
    }

    /**
     * Método para manejar la autenticación de usuario
     * y derivar al menu que le corresponde
     */
    private void iniciarSesion(Scanner scanner) {
        Usuario usuario = autenticarUsuario(scanner);

        if (usuario != null) {
            String rol = usuario.getRol().getNombre().toLowerCase();
            switch (rol) {
                case "administrador" -> menuAdministrador.iniciarMenuAdmin(usuario);
                case "profesor" -> menuProfesor.iniciarMenuProfesor(usuario);
                default -> System.out.println("Error: Rol desconocido.");
            }
        } else {
            System.out.println("Autenticación fallida. Intente nuevamente.");
        }
    }

    /**
     * Método para autenticar al Usuario
     * @param scanner para recibir información de la consola
     * @return Usuario autenticado o null
     */
    private Usuario autenticarUsuario(Scanner scanner) {
        System.out.print("Ingrese su nombre de usuario: ");
        String username = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contrasenia = scanner.nextLine();

        try {
            return seguridad.autenticar(username, contrasenia);
        } catch (AutenticacionException | JsonNotFoundException e) {
            System.out.println(STR."Error: \{e.getMessage()}");
            return null;
        }
    }
}

