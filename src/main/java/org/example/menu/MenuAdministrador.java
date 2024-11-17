package org.example.menu;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.Permisos;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.security.Seguridad;
import org.example.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MenuAdministrador {
    private final Seguridad seguridad = new Seguridad();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final AsignaturaService asignaturaService = new AsignaturaService();
    private final InscripcionService inscripcionService = new InscripcionService();
    private final SolicitudCambioAulaService solicitudCambioAulaService = new SolicitudCambioAulaService();
    private final ProfesorService profesorService = new ProfesorService();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Método para iniciar menu principal de admin
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    public void iniciarMenuAdmin(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("\nElija una opción:");
            System.out.println("1. Gestionar espacios");
            System.out.println("2. Gestionar reservas");
            System.out.println("3. Gestionar solicitudes");
            System.out.println("4. Salir");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> menuEspacios(usuario);
                case 2 -> menuReserva(usuario);
                case 3 -> menuSolicitudes(usuario);
                case 4 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para el submenu espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuEspacios(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("\nElija una opción:");
            System.out.println("1. Crear espacio");
            System.out.println("2. Listar espacios y filtros");
            System.out.println("3. Modificar espacio");
            System.out.println("4. Eliminar espacio");
            System.out.println("5. Salir");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> crearEspacio(usuario);
                case 2 -> menuListarEspacios(usuario);
                case 3 -> modificarEspacio(usuario);
                case 4 -> eliminarEspacio(usuario);
                case 5 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para crear espacio del submenu espacio
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearEspacio(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_ESPACIO)) {
            System.out.println("No posees el permiso para crear un aula.");
            return;
        }
        Aula aula;

        System.out.println("Elija el tipo de Espacio:");
        System.out.println("1. Aula");
        System.out.println("2. Laboratorio");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese el número de aula: ");
        int numero = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese la capacidad del aula: ");
        int capacidad = scanner.nextInt();
        scanner.nextLine();

        System.out.print("¿El aula tiene proyector? (Si/No): ");
        boolean tieneProyector = scanner.nextLine().equalsIgnoreCase("si");

        System.out.print("¿El aula tiene televisor? (Si/No): ");
        boolean tieneTV = scanner.nextLine().equalsIgnoreCase("si");


        if (tipo == 2) {
            System.out.print("Ingrese la cantidad de computadoras: ");
            int computadoras = scanner.nextInt();
            aula = new Laboratorio(null, numero, capacidad, tieneProyector, tieneTV, computadoras);
        } else {
            aula = new Aula(null, numero, capacidad, tieneProyector, tieneTV);
        }

        try {
            aulaService.guardar(aula);
            System.out.println("Espacio creado exitosamente.");
        } catch (JsonNotFoundException | BadRequestException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método para modificar espacio del submenu espacio
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarEspacio(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_ESPACIO)) {
            try {
                aulaService.listar().forEach(System.out::println);

                // Solicitar el número del espacio a modificar
                System.out.print("Ingrese el id del espacio que desea modificar: ");
                int numero = Integer.parseInt(scanner.nextLine());

                // Obtener el espacio por número
                Aula aula = aulaService.obtener(numero);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual del espacio
                    System.out.println("\nInformación actual del espacio:");
                    System.out.println(STR."1. Número: \{aula.getNumero()}");
                    System.out.println(STR."2. Capacidad: \{aula.getCapacidad()}");
                    System.out.println(STR."3. Tiene proyector: \{aula.isTieneProyector()}");
                    System.out.println(STR."4. Tiene TV: \{aula.isTieneTV()}");

                    if (aula instanceof Laboratorio){
                        System.out.println(STR."5.Computadoras: \{((Laboratorio) aula).getComputadoras()}");
                        System.out.println("6. Guardar cambios y salir");
                        System.out.print("Seleccione el atributo que desea modificar (1-6): ");
                    }

                    System.out.println("5. Guardar cambios y salir");
                    System.out.print("Seleccione el atributo que desea modificar (1-5): ");


                    var opcion = Integer.parseInt(scanner.nextLine());

                    switch (opcion) {
                        case 1:
                            System.out.print("Ingrese el nuevo número: ");
                            aula.setNumero(Integer.parseInt(scanner.nextLine()));
                            break;
                        case 2:
                            System.out.print("Ingrese la nueva capacidad: ");
                            aula.setCapacidad(Integer.parseInt(scanner.nextLine()));
                            break;
                        case 3:
                            System.out.print("¿Tiene proyector? (Si/No): ");
                            aula.setTieneProyector(scanner.nextLine().equalsIgnoreCase("si"));
                            break;
                        case 4:
                            System.out.print("¿Tiene TV? (Si/No): ");
                            aula.setTieneTV(scanner.nextLine().equalsIgnoreCase("si"));
                            break;
                        case 5:
                            // Guardar cambios
                            aulaService.modificar(aula);
                            System.out.println("El espacio se ha modificado exitosamente.");
                            continuar = false;
                            break;
                        default:
                            System.out.println("Opción no válida.");
                    }
                }
            } catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para modificar espacios.");
        }
    }

    /**
     * Método para eliminar un espacio del submenu espacio
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void eliminarEspacio(Usuario usuario) {
        if(seguridad.verificarPermiso(usuario, Permisos.ELIMINAR_ESPACIO))
        {
            try {
                aulaService.listar().forEach(System.out::println);
                System.out.println("Ingrese el id del espacio a eliminar");
                int id = scanner.nextInt();
                scanner.nextLine();
                aulaService.eliminar(id);
                System.out.println("Espacio eliminado exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para eliminar espacios.");
        }
    }

    /**
     * Método para mostrar el submenu de listados y filtros de espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuListarEspacios(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("\nElija una opción de listado o filtro:");
            System.out.println("1. Listar todos los espacios");
            System.out.println("2. Listar todas las aulas");
            System.out.println("3. Listar todos los laboratorios");
            System.out.println("4. Filtrar espacios por condiciones");
            System.out.println("5. Filtrar aulas por condiciones");
            System.out.println("6. Filtrar laboratorios por condiciones");
            System.out.println("7. Listar espacios disponibles");
            System.out.println("8. Listar aulas disponibles");
            System.out.println("9. Listar laboratorios disponibles");
            System.out.println("10. Salir");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> listarEspacios(usuario);
                case 2 -> listarAulas(usuario);
                case 3 -> listarLaboratorios(usuario);
                case 4 -> filtrarEspacios(usuario);
                case 5 -> filtrarAulas(usuario);
                case 6 -> filtrarLaboratorios(usuario);
                case 7 -> listarEspaciosDisponibles(usuario);
                case 8 -> listarAulasDisponibles(usuario);
                case 9 -> listarLaboratoriosDisponibles(usuario);
                case 10 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para listar todos los espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarEspacios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_ESPACIOS)) {
            try {
                aulaService.listar().forEach(System.out::println);
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver los espacios.");
        }
    }

    /**
     * Método para listar todas las aulas estándar
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarAulas(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                aulaService.listarAulas().forEach(System.out::println);
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para listar todos los laboratorios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarLaboratorios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
                aulaService.listarLaboratorios().forEach(System.out::println);
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver los laboratorios.");
        }
    }

    /**
     * Método para filtrar todos los espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarEspacios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_ESPACIOS)) {
            try {
                var capacidad = obtenerCapacidadEspacio();
                var tieneProyector = obtenerProyectoEspacio();
                var tieneTV = obtenerTvEspacio();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.filtrarEspaciosPorCondiciones(capacidad, tieneProyector, tieneTV);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("No se encontraron espacios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("Espacios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."Error: \{e.getMessage()}");
            }
        } else {
            System.out.println("No posees el permiso para ver los espacios.");
        }
    }

    /**
     * Método para filtrar todas las aulas estándar
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarAulas(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var capacidad = obtenerCapacidadEspacio();
                var tieneProyector = obtenerProyectoEspacio();
                var tieneTV = obtenerTvEspacio();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.filtrarAulasPorCondiciones(capacidad, tieneProyector, tieneTV);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("No se encontraron aulas que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("Aulas encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."Error: \{e.getMessage()}");
            }
        } else {
            System.out.println("No posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para filtrar todos los laboratorios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarLaboratorios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
               var capacidad = obtenerCapacidadEspacio();
               var tieneProyector = obtenerProyectoEspacio();
               var tieneTV = obtenerTvEspacio();

               // Solicitar las computadoras
                System.out.println("Ingrese la cantidad de computadoras (-1 si no quiere aplicar este filtro): ");
                var computadorasInput = scanner.nextInt();
                scanner.nextLine();
                var computadoras = computadorasInput == -1 ? null : computadorasInput;

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService
                        .filtrarLaboratoriosPorCondiciones(capacidad, tieneProyector, tieneTV,computadoras);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("No se encontraron aulas que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("Aulas encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."Error: \{e.getMessage()}");
            }
        } else {
            System.out.println("No posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para preguntarle al usuario la capacidad de un espacio
     * @return la cantidad del espacio
     */
    private Integer obtenerCapacidadEspacio(){
        // Solicitar la capacidad
        System.out.println("Ingrese la capacidad (-1 si no quiere aplicar este filtro): ");
        var capacidadInput = scanner.nextInt();
        scanner.nextLine();
        return capacidadInput == -1 ? null : capacidadInput;
    }

    /**
     * Método para preguntarle al usuario si un espacio tiene proyector
     * @return si tiene o no proyector
     */
    private Boolean obtenerProyectoEspacio(){
        // Solicitar si tiene proyector
        System.out.println("¿Debe tener proyector? (Si/No) (Escriba 'ninguno' si no quiere aplicar este filtro): ");
        var proyectorInput = scanner.nextLine().trim().toLowerCase();

        return proyectorInput.equals("ninguno") ? null : proyectorInput.equalsIgnoreCase("si");

    }
    /**
     * Método para preguntarle al usuario si un espacio tiene tv
     * @return si tiene o no tv
     */
    private Boolean obtenerTvEspacio(){
        // Solicitar si tiene TV
        System.out.println("¿Debe tener TV? (Si/No) (Escriba 'ninguno' si no quiere aplicar este filtro): ");
        var tvInput = scanner.nextLine().trim().toLowerCase();
        return tvInput.equals("ninguno") ? null : tvInput.equalsIgnoreCase("si");

    }

    /**
     * Método para listar todos los espacios disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarEspaciosDisponibles(Usuario usuario) {
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


                var diasYBloques = obtenerDiasYBloques();

                // Llamar a la función y listar aulas disponibles
                var espaciosDisponibles = aulaService.listarEspaciosDisponibles(fechaInicio, fechaFin, diasYBloques);
                System.out.println("Espacios disponibles:");
                espaciosDisponibles.forEach(System.out::println);

            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para ver los espacios.");
        }
    }

    /**
     * Método para listar todas las aulas disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarAulasDisponibles(Usuario usuario) {
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


                var diasYBloques = obtenerDiasYBloques();

                // Llamar a la función y listar aulas disponibles
                List<Aula> aulasDisponibles = aulaService.listarAulasDisponibles(fechaInicio, fechaFin, diasYBloques);
                System.out.println("Aulas disponibles:");
                aulasDisponibles.forEach(System.out::println);

            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para listar todos los laboratorios disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarLaboratoriosDisponibles(Usuario usuario) {
        if(seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS))
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


                var diasYBloques = obtenerDiasYBloques();
                // Llamar a la función y listar aulas disponibles
                var laboratoriosDisponibles = aulaService.listarLaboratoriosDisponibles(fechaInicio, fechaFin, diasYBloques);
                System.out.println("Laboratorios disponibles:");
                laboratoriosDisponibles.forEach(System.out::println);

            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para ver los laboratorios.");
        }
    }

    /**
     * Método para pedir los días seleccionados y bloques horarios.
     * @return Map<DayOfWeek, Set<BloqueHorario>> que contiene los días seleccionados y los bloques horarios
     */
    private Map<DayOfWeek, Set<BloqueHorario>> obtenerDiasYBloques() {
        Map<DayOfWeek, Set<BloqueHorario>> diasYBloques = new HashMap<>();

        // Seleccionar días de la semana
        Set<DayOfWeek> diasSeleccionados = new HashSet<>();
        System.out.println("Ingrese los días de la semana (separados por comas): ");
        System.out.println("1. Lunes");
        System.out.println("2. Martes");
        System.out.println("3. Miércoles");
        System.out.println("4. Jueves");
        System.out.println("5. Viernes");
        System.out.println("6. Sábado");
        System.out.println("7. Domingo");
        String diasInput = scanner.nextLine();
        for (String diaStr : diasInput.split(",")) {
            int dia = Integer.parseInt(diaStr.trim()) - 1;
            diasSeleccionados.add(DayOfWeek.of(dia + 1)); // DayOfWeek usa valores 1-7 para Lunes-Domingo
        }

        // Seleccionar bloques horarios
        for (DayOfWeek dia : diasSeleccionados) {
            Set<BloqueHorario> bloques = new HashSet<>();
            System.out.println(STR."Seleccione los bloques horarios para \{dia} (separado por comas): ");
            int i = 1;
            for (BloqueHorario bloque : BloqueHorario.values()) {
                System.out.println(STR."\{i}. \{bloque}");
                i++;
            }
            String bloquesStr = scanner.nextLine();
            for (String bloqueIndex : bloquesStr.split(",")) {
                int opcionBloque = Integer.parseInt(bloqueIndex.trim()) - 1;
                bloques.add(BloqueHorario.values()[opcionBloque]);
            }
            diasYBloques.put(dia, bloques);
        }

        return diasYBloques;
    }

    /**
     * Método para submenu reserva
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuReserva(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("Elija una opción:");
            System.out.println("1.Crear reserva.");
            System.out.println("2.Listar todas las reservas.");
            System.out.println("3.Listar reservas por profesor.");
            System.out.println("4.Listar reservas por comisión");
            System.out.println("5.Listar reservas por asignatura");
            System.out.println("6.Modificar reserva");
            System.out.println("7.Eliminar reserva");
            System.out.println("8.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> crearReserva(usuario);
                case 2 -> listarReservas(usuario);
                case 3 -> listarReservasXProfesor(usuario);
                case 4 -> listarReservasXComision(usuario);
                case 5 -> listarReservasXAsignatura(usuario);
                case 6 -> modificarReserva(usuario);
                case 7 -> eliminarReserva(usuario);
                case 8 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para crear reserva del submenu reserva
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearReserva(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_RESERVA)) {
            System.out.println("No posees el permiso para crear una reserva.");
            return;
        }

        try{
            Reserva reserva = new Reserva(null);

            // Pedir fecha de inicio
            System.out.print("Ingrese la fecha de inicio (AAAA-MM-DD): ");
            String fechaInicioStr = scanner.nextLine();
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);

            reserva.setFechaInicio(fechaInicio);

            // Pedir fecha de fin
            System.out.print("Ingrese la fecha de fin (AAAA-MM-DD): ");
            String fechaFinStr = scanner.nextLine();
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

            reserva.setFechaFin(fechaFin);

            var diasYBloques = obtenerDiasYBloques();
            reserva.setDiasYBloques(diasYBloques);

            aulaService.listarEspaciosDisponibles(fechaInicio,fechaFin,diasYBloques).forEach(System.out::println);
            System.out.print("Ingrese el id del espacio: ");
            int idAula = scanner.nextInt();
            scanner.nextLine();

            reserva.setAula(new Aula(idAula));

            inscripcionService.listar().forEach(System.out::println);
            System.out.print("Ingrese el id de la inscripción: ");
            int idInscripcion = scanner.nextInt();
            scanner.nextLine();

            reserva.setInscripcion(new Inscripcion(idInscripcion));

            reservaService.guardar(reserva);
            System.out.println("Espacio creado exitosamente.");
        } catch (JsonNotFoundException | BadRequestException | NotFoundException | ConflictException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método para listar todas las reservas
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservas(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                reservaService.listar().forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXProfesor(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                profesorService.listar().forEach(System.out::println);
                System.out.println("Ingresa el id del profesor: ");
                var idProfesor = scanner.nextInt();
                scanner.nextLine();
                reservaService.listarReservasPorProfesor(idProfesor).forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por comision
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXComision(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                System.out.println("Ingresa la comision: ");
                var comision = scanner.nextLine();
                reservaService.listarReservasPorComision(comision).forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por asignatura
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXAsignatura(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                asignaturaService.listar().forEach(System.out::println);
                System.out.println("Ingresa el id de la asignatura: ");
                var idAsignatura = scanner.nextInt();
                scanner.nextLine();
                reservaService.listarReservasPorAsignatura(idAsignatura).forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para modificar reserva del submenu reservas
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarReserva(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_RESERVA)) {
            try {
                reservaService.listar().forEach(System.out::println);

                // Solicitar el número del espacio a modificar
                System.out.print("Ingrese el id de la reserva que desea modificar: ");
                int numero = Integer.parseInt(scanner.nextLine());

                // Obtener el espacio por número
                Reserva reserva = reservaService.obtener(numero);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual del espacio
                    System.out.println("\nInformación actual de la reserva:");
                    System.out.println(STR."1. Fecha de Inicio: \{reserva.getFechaInicio()}");
                    System.out.println(STR."2. Fecha de Fin: \{reserva.getFechaFin()}");
                    System.out.println(STR."3. Dias y Horarios: \{reserva.getDiasYBloques()}");
                    System.out.println(STR."4. \{reserva.getAula()}");
                    System.out.println(STR."5. \{reserva.getInscripcion()}");
                    System.out.println("6. Guardar cambios y salir");
                    System.out.print("Seleccione el atributo que desea modificar (1-6): ");

                    var opcion = Integer.parseInt(scanner.nextLine());

                    switch (opcion) {
                        case 1:
                            // Pedir fecha de inicio
                            System.out.print("Ingrese la fecha de inicio (AAAA-MM-DD): ");
                            String fechaInicioStr = scanner.nextLine();
                            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
                            reserva.setFechaInicio(fechaInicio);
                            break;
                        case 2:
                            System.out.print("Ingrese la fecha de fin (AAAA-MM-DD): ");
                            String fechaFinStr = scanner.nextLine();
                            LocalDate fechaFin = LocalDate.parse(fechaFinStr);

                            reserva.setFechaFin(fechaFin);
                            break;
                        case 3:
                            reserva.setDiasYBloques(obtenerDiasYBloques());
                            break;
                        case 4:
                            aulaService.listar().forEach(System.out::println);
                            System.out.print("Ingrese el id del espacio: ");
                            int idAula = scanner.nextInt();
                            scanner.nextLine();

                            reserva.setAula(new Aula(idAula));
                            break;
                        case 5:
                            inscripcionService.listar().forEach(System.out::println);
                            System.out.print("Ingrese el id de la inscripción: ");
                            int idInscripcion = scanner.nextInt();
                            scanner.nextLine();

                            reserva.setInscripcion(new Inscripcion(idInscripcion));
                            break;
                        case 6:
                            // Guardar cambios
                            reservaService.modificar(reserva);
                            System.out.println("La reserva se ha modificado exitosamente.");
                            continuar = false;
                            break;
                        default:
                            System.out.println("Opción no válida.");
                    }
                }
            } catch (BadRequestException | ConflictException | JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para modificar reservas.");
        }
    }

    /**
     * Método para eliminar una reserva del submenu reservas
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void eliminarReserva(Usuario usuario) {
        if(seguridad.verificarPermiso(usuario, Permisos.ELIMINAR_RESERVA))
        {
            try {
                reservaService.listar().forEach(System.out::println);
                System.out.println("Ingrese el id de la reserva a eliminar");
                int id = scanner.nextInt();
                scanner.nextLine();
                reservaService.eliminar(id);
                System.out.println("Reserva eliminada exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para eliminar reservas.");
        }
    }


    /**
     * Método para el submenu solicitudes
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuSolicitudes(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("Elija una opción:");
            System.out.println("1.Listar");
            System.out.println("2.Revisar solicitudes pendientes");
            System.out.println("3.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> menuListarSolicitudes(usuario);
                case 2 -> revisarSolicitudes(usuario);
                case 3 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para mostrar el submenu de listados y filtros de espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuListarSolicitudes(Usuario usuario) {
        boolean salir = false;
        int opcion;

        while (!salir) {
            System.out.println("\nElija una opción de listado o filtro:");
            System.out.println("1. Listar todas las solicitudes");
            System.out.println("2. Listar todas las solicitudes pendientes");
            System.out.println("3. Listar todas las solicitudes aprobadas");
            System.out.println("4. Listar todas las solicitudes rechazadas");
            System.out.println("5. Listar todas las solicitudes x profesor");
            System.out.println("6. Salir");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> listarSolicitudes(usuario);
                case 2 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.PENDIENTE);
                case 3 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.APROBADA);
                case 4 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.RECHAZADA);
                case 5 -> listarSolicitudesXProfesor(usuario);
                case 6 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para listar todas las solicitudes
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarSolicitudes(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                solicitudCambioAulaService.listar().forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las solicitudes.");
        }
    }

    /**
     * Método para listar todas las solicitudes por estado
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarSolicitudesXEstado(Usuario usuario,EstadoSolicitud estadoSolicitud) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstado(estadoSolicitud);
                if(solicitudes.isEmpty()){
                    System.out.println(STR."No hay solicitudes \{estadoSolicitud.toString().toLowerCase()}s");
                }else {
                    solicitudes.forEach(System.out::println);
                }
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las solicitudes.");
        }
    }

    /**
     * Método para listar todas las solicitudes por profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarSolicitudesXProfesor(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                profesorService.listar().forEach(System.out::println);

                System.out.println("Ingresa el id del profesor: ");
                var idProfesor = scanner.nextInt();
                scanner.nextLine();

                solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(EstadoSolicitud.PENDIENTE,idProfesor)
                        .forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las solicitudes.");
        }
    }

    /**
     * Método para aprobar o rechazar uns solicitud
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void revisarSolicitudes(Usuario usuario){
        if (seguridad.verificarPermiso(usuario,Permisos.GESTIORNAR_CAMBIOS)){
            try {
                solicitudCambioAulaService.listarSolicitudesPorEstado(EstadoSolicitud.PENDIENTE)
                        .forEach(System.out::println);

                System.out.println("Ingresa el id de la solicitud: ");
                var idSolicitud = scanner.nextInt();
                scanner.nextLine();

                System.out.println("""
                        Elija una opción:
                        1.Rechazar
                        2.Aprobar
                        """);
                var estado= scanner.nextInt();
                scanner.nextLine();

                System.out.println("Indica el motivo (Enter si no quieres indicarlo):");
                var motivo = scanner.nextLine();

                switch (estado){
                    case 1:
                        solicitudCambioAulaService.rechazarSolicitud(idSolicitud,motivo);
                        break;
                    case 2:
                        solicitudCambioAulaService.aprobarSolicitud(idSolicitud,motivo);
                        break;
                }

            }catch (NotFoundException | BadRequestException | JsonNotFoundException | ConflictException e){
                System.out.println(e.getMessage());
            }
        }
    }
}