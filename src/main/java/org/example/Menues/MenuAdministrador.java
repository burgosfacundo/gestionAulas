package org.example.Menues;

import org.example.enums.Permisos;
import org.example.model.Usuario;
import org.example.security.Seguridad;

import java.util.Scanner;

public class MenuAdministrador {

    Seguridad seguridad = new Seguridad();

    public void iniciarMenuAdmin(Usuario usuario)
    {
        boolean salir=false;
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;



        while(!salir)
        {
            ///Crud de aulas
            ///Crud de reservas

            System.out.println("Elija una opcion:");
            System.out.println("1.Gestionar aulas.");
            System.out.println("2.Gestionar reservas.");
            System.out.println("3.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion)
            {
                case 1:
                    menuAula(usuario);
                    break;
                case 2:
                    menuReserva(usuario);
                    break;
                case 3:
                    salir = true;
                    break;
            }
        }
    }

    private void menuAula(Usuario usuario)
    {
        boolean salir=false;
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;


        while(!salir)
        {
            System.out.println("Elija una opcion:");
            System.out.println("1.Crear aula.");
            System.out.println("2.Listar todas las aulas.");
            System.out.println("3.Listar aulas disponibles.");
            System.out.println("4.Listar aulas segun capacidad.");
            System.out.println("5.Listar laboratorios.");
            System.out.println("6.Modificar aula.");
            System.out.println("7.Eliminar aula.");
            System.out.println("8.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion)
            {
                case 1:
                    if(seguridad.verificarPermiso(usuario, Permisos.CREAR_AULA))
                    {
                        //gestorAula.crearAula
                    }else {
                        System.out.println("No posees el permiso para crear un aula");
                    }

                    break;
                case 2:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        //gestorAula.listar();
                    }else {
                        System.out.println("No posees el permiso para ver las aulas.");
                    }

                    break;
                case 3:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        //gestorAula.listarDisponibles();
                    }else {System.out.println("No posees el permiso para ver las aulas.");}

                    break;
                case 4:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        System.out.println("Ingrese la capacidad:");
                        int capacidad = scanner.nextInt();
                        scanner.nextLine();
                        //listar segun capacidad
                    }else {
                        System.out.println("No posees el permiso para ver las aulas.");
                    }

                    break;
                case 5:
                    //listar laboratorios
                    break;
                case 6:
                    //gestorAula.modificar();
                    break;
                case 7:
                    //gestorAula.eliminar();
                    break;
                case 8:
                    salir = true;
                    break;
            }
        }
    }

    private void menuReserva(Usuario usuario)
    {
        boolean salir=false;
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;


        while(!salir)
        {
            System.out.println("Elija una opcion:");
            System.out.println("1.Crear reserva.");
            System.out.println("2.Listar todas las reservas.");
            System.out.println("3.Listar reservas por profesor.");
            System.out.println("4.Listar reservas segun comision");
            System.out.println("5.Listar reservas segun asignatura");
            System.out.println("6.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion)
            {
                case 1:

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
                    salir = true;
                    break;
            }
        }
    }

}
