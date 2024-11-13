package org.example.menu;

import org.example.enums.BloqueHorario;
import org.example.enums.Permisos;
import org.example.model.Aula;
import org.example.model.Laboratorio;
import org.example.model.Usuario;
import org.example.security.Seguridad;
import org.example.service.AulaService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MenuAdministrador {

    Seguridad seguridad = new Seguridad();
    AulaService aulaService = new AulaService();

    public void iniciarMenuAdmin(Usuario usuario)
    {
        boolean salir=false;
        Scanner scanner = new Scanner(System.in);
        int opcion;



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
        int opcion;


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
                        System.out.print("Ingrese el ID del aula: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Ingrese el número de aula: ");
                        int numero = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Ingrese la capacidad del aula: ");
                        int capacidad = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("El aula tiene proyector? (true/false): ");
                        boolean tieneProyector = Boolean.parseBoolean(scanner.nextLine());

                        System.out.print("El aula tiene televisor? (true/false): ");
                        boolean tieneTV = Boolean.parseBoolean(scanner.nextLine());

                        Aula aula = new Aula(id, numero, capacidad, tieneProyector, tieneTV);
                        try {
                            aulaService.guardar(aula);
                        }catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                        }

                    }else {
                        System.out.println("No posees el permiso para crear un aula");
                    }

                    break;
                case 2:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        try
                        {
                            System.out.println(aulaService.listar());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }else {
                        System.out.println("No posees el permiso para ver las aulas.");
                    }

                    break;
                case 3:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {

                        try
                        {
                            // Pedir fecha de inicio
                            System.out.print("Ingrese la fecha de inicio (AAAA-MM-DD): ");
                            String fechaInicioStr = scanner.nextLine();
                            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);

                            // Pedir fecha de fin
                            System.out.print("Ingrese la fecha de fin (AAAA-MM-DD): ");
                            String fechaFinStr = scanner.nextLine();
                            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

                            // Seleccionar días de la semana
                            System.out.println("Seleccione el día de la semana (1-7, donde 1=Lunes, ..., 7=Domingo): ");
                            int dia = scanner.nextInt();
                            scanner.nextLine();

                            // Seleccionar bloque horario
                            Set<BloqueHorario> bloques = new HashSet<>();
                            System.out.println("Seleccione los bloques horarios (separado por comas): ");
                            int i = 1;
                            for (BloqueHorario bloque : BloqueHorario.values()) {
                                System.out.println(i + ". " + bloque);
                                i++;
                            }
                            String bloquesStr = scanner.nextLine();
                            for (String bloqueIndex : bloquesStr.split(",")) {
                                int opcionBloque = Integer.parseInt(bloqueIndex.trim()) - 1;
                                bloques.add(BloqueHorario.values()[opcionBloque]);
                            }

                            Map<DayOfWeek, Set<BloqueHorario>> diasYBloques = new HashMap<>();
                            diasYBloques.put(DayOfWeek.of(dia), bloques);

                            // Llamar a la función y listar aulas disponibles
                            List<Aula> aulasDisponibles = aulaService.listarAulasDisponibles(fechaInicio, fechaFin, diasYBloques);
                            System.out.println("Aulas disponibles:");
                            aulasDisponibles.forEach(System.out::println);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }else {System.out.println("No posees el permiso para ver las aulas.");}

                    break;
                case 4:
                    if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                    {
                        System.out.println("Ingrese la capacidad:");
                        int capacidad = scanner.nextInt();
                        scanner.nextLine();
                        try {
                            List<Aula> aulasDisponibles = aulaService.filtrarPorCapacidad(capacidad);
                            System.out.println("Aulas disponibles:");
                            aulasDisponibles.forEach(System.out::println);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }else {
                        System.out.println("No posees el permiso para ver las aulas.");
                    }

                    break;
                case 5:
                    try{
                        if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
                        {
                            List<Laboratorio> laboratoriosDisponibles = aulaService.filtrarPorLaboratorio();
                            System.out.println("Laboratorios disponibles:");
                            laboratoriosDisponibles.forEach(System.out::println);
                        }
                    }catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 6:
                    if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_AULA)) {
                        try {
                            // Solicitar el número del aula a modificar
                            System.out.print("Ingrese el número del aula que desea modificar: ");
                            int numeroAula = Integer.parseInt(scanner.nextLine());

                            // Obtener el aula por número
                            Aula aula = aulaService.obtener(numeroAula);

                            boolean continuar = true;
                            while (continuar) {
                                // Mostrar información actual del aula
                                System.out.println("\nInformación actual del aula:");
                                System.out.println("1. Número: " + aula.getNumero());
                                System.out.println("2. Capacidad: " + aula.getCapacidad());
                                System.out.println("3. Tiene proyector: " + aula.isTieneProyector());
                                System.out.println("4. Tiene TV: " + aula.isTieneTV());
                                System.out.println("5. Guardar cambios y salir");

                                // Preguntar qué atributo desea modificar
                                System.out.print("Seleccione el atributo que desea modificar (1-5): ");
                                opcion = Integer.parseInt(scanner.nextLine());

                                switch (opcion) {
                                    case 1:
                                        System.out.print("Ingrese el nuevo número del aula: ");
                                        aula.setNumero(Integer.parseInt(scanner.nextLine()));
                                        break;
                                    case 2:
                                        System.out.print("Ingrese la nueva capacidad del aula: ");
                                        aula.setCapacidad(Integer.parseInt(scanner.nextLine()));
                                        break;
                                    case 3:
                                        System.out.print("¿Tiene proyector? (true/false): ");
                                        aula.setTieneProyector(Boolean.parseBoolean(scanner.nextLine()));
                                        break;
                                    case 4:
                                        System.out.print("¿Tiene TV? (true/false): ");
                                        aula.setTieneTV(Boolean.parseBoolean(scanner.nextLine()));
                                        break;
                                    case 5:
                                        // Guardar cambios
                                        aulaService.modificar(aula);
                                        System.out.println("El aula se ha modificado exitosamente.");
                                        continuar = false;
                                        break;
                                    default:
                                        System.out.println("Opción no válida. Intente de nuevo.");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("No posees el permiso para modificar aulas.");
                    }
                break;
                case 7:
                    try {
                        if(seguridad.verificarPermiso(usuario, Permisos.ELIMINAR_AULA))
                        {
                            System.out.println("Ingrese el numero del aula a eliminar");
                            int numero = scanner.nextInt();
                            scanner.nextLine();
                            aulaService.eliminar(numero);
                        }
                    }catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }

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
        int opcion;


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
