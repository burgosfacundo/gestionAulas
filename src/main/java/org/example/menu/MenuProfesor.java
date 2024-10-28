package org.example.menu;

import org.example.enums.Permisos;
import org.example.model.Usuario;
import org.example.security.Seguridad;

import java.util.Scanner;

public class MenuProfesor {

    public void iniciarMenuProfesor(Usuario usuario) {
        boolean salir = false;
        Scanner scanner = new Scanner(System.in);
        int opcion;

        Seguridad seguridad = new Seguridad();

        while (!salir) {
            System.out.println("Elija una opcion:");
            System.out.println("1.Listar todas las aulas.");
            System.out.println("2.Listar aulas disponibles.");
            System.out.println("3.Listar aulas segun capacidad.");
            System.out.println("4.Listar laboratorios.");
            System.out.println("5.Solicitar reserva.");
            System.out.println("6.Listar mis reservas.");
            System.out.println("7.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        //listar
                    }else {
                        System.out.println("No posees el permiso para ver aulas");
                    }
                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                case 5:

                    break;
                case 6:

                    break;
                case 7:
                    salir = true;
                    break;
            }
        }
    }

}
