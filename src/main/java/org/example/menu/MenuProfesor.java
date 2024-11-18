package org.example.menu;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.Permisos;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.security.Seguridad;
import org.example.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MenuProfesor {

    private final Seguridad seguridad = new Seguridad();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final SolicitudCambioAulaService solicitudCambioAulaService = new SolicitudCambioAulaService();
    private final Scanner scanner = new Scanner(System.in);




    public void iniciarMenuProfesor(Usuario usuario) {
        boolean salir = false;
        int opcion;


        while (!salir) {
            System.out.println("Elija una opcion:");
            System.out.println("1.Listar espacios.");
            System.out.println("2.Solicitar reserva.");
            System.out.println("3.Listar mis solicitudes pendientes.");
            System.out.println("4.Listar mis solicitudes aprobadas.");
            System.out.println("5.Listar mis solicitudes rechazadas.");
            System.out.println("6.Listar mis reservas.");
            System.out.println("7.Cambiar mi contraseña.");
            System.out.println("8.Salir.");

            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> menuListarEspacios(usuario);
                case 2 -> solicitarReserva(usuario);
                case 3 -> listarMisSolicitudes(usuario, EstadoSolicitud.PENDIENTE);
                case 4 -> listarMisSolicitudes(usuario, EstadoSolicitud.APROBADA);
                case 5 -> listarMisSolicitudes(usuario, EstadoSolicitud.RECHAZADA);
                case 6 -> listarMisReservas(usuario);
                case 7 -> cambiarPassword(usuario);
                case 8 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
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
     * Método para solicitar reserva como profesor.
     * @param usuario que esta logeado para verificar permisos
     */
    private void solicitarReserva(Usuario usuario){
        if(seguridad.verificarPermiso(usuario, Permisos.SOLICITAR_CAMBIO)){
            try {



                // listar reservas por profesor:
                List<Reserva> reservas = reservaService.listarReservasPorProfesor(usuario.getId());
                reservas.forEach(System.out::println);


                // consultar reserva segun id (nos da cant de usuarios, computadoras o no, proyector, etc)
                System.out.println("Digite el id de reserva para el que desea solicitar cambio: ");
                Reserva reserva = reservaService.obtener(scanner.nextInt());
                scanner.nextLine();


                // Extraer fecha inicio y fin. Por defecto tipo es permanente.
                var tipoSolicitud = TipoSolicitud.PERMANENTE;
                LocalDate fechaInicio = reserva.getFechaInicio();
                LocalDate fechaFin = reserva.getFechaFin();

                // consultar al user si es temporal
                int opcion;
                do {
                    System.out.println("Digite '1' si el cambio es temporal o '2' si es permanente: ");
                    opcion = scanner.nextInt();
                    scanner.nextLine();
                } while (opcion != 1 && opcion != 2);

                if(opcion == 1){
                    tipoSolicitud = TipoSolicitud.TEMPORAL;

                    // Pedir fecha de inicio
                    System.out.print("Ingrese la fecha de inicio (AAAA-MM-DD): ");
                    String fechaInicioStr = scanner.nextLine();
                    fechaInicio = LocalDate.parse(fechaInicioStr);


                    // Pedir fecha de fin
                    System.out.print("Ingrese la fecha de fin (AAAA-MM-DD): ");
                    String fechaFinStr = scanner.nextLine();
                    fechaFin = LocalDate.parse(fechaFinStr);

                }

                // obtener días y bloques horario
                Map<DayOfWeek, Set<BloqueHorario>> diasYBloques = obtenerDiasYBloques();

                // Comentario adicional
                System.out.println("Escriba un comentario adicional para la solicitud: ");
                String comentarioProfesor = scanner.nextLine();


                List<? extends Aula> aulasCoinciden;
                if(reserva.getAula() instanceof Laboratorio) {

                    aulasCoinciden = aulaService.listarLaboratoriosDisponiblesPorSolicitud(
                            ((Laboratorio) reserva.getAula()).getComputadoras(), reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }else{
                    aulasCoinciden = aulaService.listarAulasDisponiblesPorSolicitud(reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }

                System.out.println("Digite el numero de aula que desea solicitar: ");
                Aula aula = aulaService.obtener(scanner.nextInt());
                scanner.nextLine();
                if(aulasCoinciden.contains(aula)){
                    SolicitudCambioAula solicitud = new SolicitudCambioAula(null, reserva.getInscripcion().getProfesor(), reserva,
                            aula, tipoSolicitud, fechaInicio, fechaFin, diasYBloques, comentarioProfesor);
                    solicitudCambioAulaService.guardar(solicitud);
                }else{
                    throw new BadRequestException("Error. El aula solicitada no coincide con los parametros de busqueda o no existe.");
                }

            } catch (BadRequestException | JsonNotFoundException | NotFoundException e) {
            System.out.println(e.getMessage());
            }
        }
    }



    /**
     * Método para listar solicitudes del usuario actual segun el estado
     * @param usuario que esta logueado para verificar perfil con permisos
     * @param estadoSolicitud por el cual se quiere filtrar
     */
    private void listarMisSolicitudes(Usuario usuario, EstadoSolicitud estadoSolicitud){
        if(seguridad.verificarPermiso(usuario, Permisos.SOLICITAR_CAMBIO)){
            try{
                solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(estadoSolicitud, usuario.getId())
                        .forEach(System.out::println);

            }catch (NotFoundException | JsonNotFoundException e){
                System.out.println(e.getMessage());
            }
        }

    }




    /**
     * Método para listar todas las reservas del profesor instanciado
     * @param usuario que esta logueado para verificar perfil con permisos y obtener id
     */
    private void listarMisReservas(Usuario usuario){
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                reservaService.listarReservasPorProfesor(usuario.getId()).forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para cambiar contrasenia del profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void cambiarPassword(Usuario usuario){
        if(seguridad.verificarPermiso(usuario, Permisos.CAMBIAR_PASSWORD))
        {
            try {
                boolean passwordsCoinciden = false;
                while (!passwordsCoinciden) {
                    System.out.println("Ingresa la nueva contraseña: ");
                    var password = scanner.nextLine();
                    System.out.println("Ingresa nuevamente la contraseña: ");
                    var validaPassword = scanner.nextLine();

                    if (password.equals(validaPassword)) {
                        usuario.setPassword(password);
                        usuarioService.modificar(usuario);
                        passwordsCoinciden = true;
                        System.out.println("Contraseña modificada correctamente. ");
                    } else {
                        System.out.println("Las contraseñas no coinciden. Intenta nuevamente.");
                    }
                }
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para eliminar usuarios.");
        }
    }

}
