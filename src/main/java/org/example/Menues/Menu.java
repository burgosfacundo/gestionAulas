package org.example.Menues;

import org.example.exception.AutenticacionException;
import org.example.exception.JsonNotFoundException;
import org.example.model.Usuario;
import org.example.security.Seguridad;

import java.util.Scanner;

public class Menu {

    MenuAdministrador menuAdministrador = new MenuAdministrador();
    MenuProfesor menuProfesor = new MenuProfesor();
    Seguridad seguridad = new Seguridad();

    public void iniciarMenu()
    {
        System.out.println("Bienvenido");

        boolean salir=false;
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        Usuario usuario = null;

        while(!salir)
        {
            System.out.println("Elija una opcion:");
            System.out.println("1.Iniciar sesion.");
            System.out.println("2.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion)
            {
                case 1:
                    System.out.println("Ingrese su nombre de usuario:");
                    String username = scanner.nextLine();
                    System.out.println("Ingrese su contraseña:");
                    String contrasenia = scanner.nextLine();

                    try{
                        usuario = seguridad.autenticar(username, contrasenia);
                    }catch(AutenticacionException | JsonNotFoundException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    if(usuario != null) {
                        if (usuario.getRol().getNombre().equalsIgnoreCase("Administrador")) {
                            menuAdministrador.iniciarMenuAdmin(usuario);
                        } else if (usuario.getRol().getNombre().equalsIgnoreCase("Profesor")) {
                            menuProfesor.iniciarMenuProfesor(usuario);
                        }else{
                            System.out.println("Rol desconocido.");
                        }
                    }

                    break;
                case 2:
                    salir = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        }
    }

}
