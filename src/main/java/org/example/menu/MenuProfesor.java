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
import org.example.utils.MenuUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class MenuProfesor {

    private final Seguridad seguridad = new Seguridad();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final SolicitudCambioAulaService solicitudCambioAulaService = new SolicitudCambioAulaService();

    public void iniciarMenuProfesor(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Menú Profesor");
            System.out.println("======================================");
            System.out.println("Elija una opción:");
            System.out.println("1. Listar espacios.");
            System.out.println("2. Solicitar cambio.");
            System.out.println("3. Listar mis solicitudes pendientes.");
            System.out.println("4. Listar mis solicitudes aprobadas.");
            System.out.println("5. Listar mis solicitudes rechazadas.");
            System.out.println("6. Listar mis reservas.");
            System.out.println("7. Cambiar mi contraseña.");
            System.out.println("8. Salir.");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuListarEspacios(usuario);
                case 2 -> solicitarCambio(usuario);
                case 3 -> listarMisSolicitudes(usuario, EstadoSolicitud.PENDIENTE);
                case 4 -> listarMisSolicitudes(usuario, EstadoSolicitud.APROBADA);
                case 5 -> listarMisSolicitudes(usuario, EstadoSolicitud.RECHAZADA);
                case 6 -> listarMisReservas(usuario);
                case 7 -> cambiarPassword(usuario);
                case 8 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
            }
        }
    }
    /**
     * Método para mostrar el submenu de listados y filtros de espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuListarEspacios(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Listado Espacios");
            System.out.println("======================================");
            System.out.println("Elija una opción de listado o filtro:");
            System.out.println("1. Listar todas las aulas");
            System.out.println("2. Listar todos los laboratorios");
            System.out.println("3. Filtrar aulas disponibles");
            System.out.println("4. Filtrar laboratorios disponibles");
            System.out.println("5. Salir");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> listarAulas(usuario);
                case 2 -> listarLaboratorios(usuario);
                case 3 -> filtrarAulasDisponibles(usuario);
                case 4 -> filtrarLaboratoriosDisponibles(usuario);
                case 5 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
            }
        }
    }

    /**
     * Método para listar todas las aulas estándar
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarAulas(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var aulas = aulaService.listarAulas();
                if (aulas.isEmpty()){
                    System.out.println("\nNo hay aulas");
                    return;
                }
                System.out.println("\nAulas:");
                System.out.println("======================");
                aulas.forEach(System.out::println);
                System.out.println("======================");
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para listar todos los laboratorios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarLaboratorios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
                var laboratorios = aulaService.listarLaboratorios();
                if (laboratorios.isEmpty()){
                    System.out.println("\nNo hay laboratorios");
                    return;
                }
                System.out.println("\nLaboratorios:");
                System.out.println("======================");
                laboratorios.forEach(System.out::println);
                System.out.println("======================");
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los laboratorios.");
        }
    }

    /**
     * Método para filtrar todas las aulas estándar disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarAulasDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = MenuUtils.leerDiasYBloques();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.listarAulasDisponiblesConCondiciones(capacidad, tieneProyector,
                        tieneTV,fechaInicio,fechaFin,diasYBloques);

                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron aulas que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nAulas encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\nError: \{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para filtrar todos los laboratorios disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarLaboratoriosDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = MenuUtils.leerDiasYBloques();

                var computadoras = MenuUtils.obtenerCantidadComputadoras();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService
                        .listarLaboratoriosDisponiblesConCondiciones(computadoras,capacidad, tieneProyector, tieneTV,fechaInicio,fechaFin,diasYBloques);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron laboratorios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nLaboratorios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\nError: \{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los laboratorios.");
        }
    }


    /**
     * Método para solicitar reserva como profesor.
     * @param usuario que esta logeado para verificar permisos
     */
    private void solicitarCambio(Usuario usuario){
        if(seguridad.verificarPermiso(usuario, Permisos.SOLICITAR_CAMBIO)){
            try {
                // listar reservas por profesor:
                var reservas = reservaService.listarReservasPorProfesor(usuario.getProfesor().getId());
                if (reservas.isEmpty()){
                    System.out.println("\nNo tienes reservas aun");
                    return;
                }
                System.out.println("\nReservas:");
                System.out.println("============================");
                reservas.forEach(System.out::println);
                System.out.println("============================");


                // Solicitar el ID de la reserva que necesita cambio
                int idReserva = MenuUtils.leerEntero("\nIngresa el id de la reserva en la cual necesita un cambio:");

                // Validar que el idReserva ingresado esté dentro de las reservas listadas
                boolean reservaValida = reservas.stream()
                        .anyMatch(reserva -> reserva.getId() == idReserva);

                if (!reservaValida) {
                    System.out.println("\nEl ID de la reserva ingresado no es válido.");
                    return;  // Salir del método si el ID no es válido
                }

                var reserva = reservaService.obtener(idReserva);


                // Extraer fecha inicio y fin. Por defecto tipo es permanente.
                var tipoSolicitud = TipoSolicitud.PERMANENTE;
                LocalDate fechaInicio = reserva.getFechaInicio();
                LocalDate fechaFin = reserva.getFechaFin();

                // consultar al user si es temporal
                int opcion;
                do {
                    opcion = MenuUtils.leerEntero("""
                            \n
                            Ingresa que tipo de cambio necesita:
                            1. Temporal
                            2. Permanente
                            -\s
                            """);
                } while (opcion != 1 && opcion != 2);

                Map<DayOfWeek, Set<BloqueHorario>> diasYBloques;
                if(opcion == 1){
                    tipoSolicitud = TipoSolicitud.TEMPORAL;

                    var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                    fechaInicio = rangoFechas.get(0);
                    fechaFin = rangoFechas.get(1);
                    if (fechaInicio.equals(fechaFin)){
                        var bloques = MenuUtils.leerBloques(fechaFin.getDayOfWeek());
                        diasYBloques = new HashMap<>();
                        diasYBloques.put(fechaFin.getDayOfWeek(),bloques);
                    }else {
                        diasYBloques  = MenuUtils.leerDiasYBloques();
                    }
                }else {
                    // obtener días y bloque horario
                    diasYBloques  = MenuUtils.leerDiasYBloques();
                }



                // Comentario adicional
                String comentarioProfesor = MenuUtils.leerTexto("\nEscriba un comentario adicional para la solicitud: ");


                List<? extends Aula> aulasCoinciden;
                if(reserva.getAula() instanceof Laboratorio) {
                    aulasCoinciden = aulaService.listarLaboratoriosDisponiblesConCondiciones(
                            ((Laboratorio) reserva.getAula()).getComputadoras(), reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }else{
                    aulasCoinciden = aulaService.listarAulasDisponiblesConCondiciones(reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }

                if (!aulasCoinciden.isEmpty()){
                    System.out.println("\nEspacios disponibles: ");
                    aulasCoinciden.forEach(System.out::println);
                    Aula aula = aulaService.obtener(MenuUtils.leerEntero("\nIngresa el id del espacio que desea solicitar: "));
                    SolicitudCambioAula solicitud = new SolicitudCambioAula(null, reserva.getInscripcion().getProfesor(), reserva,
                            aula, tipoSolicitud, fechaInicio, fechaFin, diasYBloques, comentarioProfesor);
                    solicitudCambioAulaService.guardar(solicitud);
                    System.out.println("\nSolicitud creada exitosamente.");
                }else {
                    System.out.println("\nNo hay espacios disponibles con esas características en las fechas ingresadas");
                }
            } catch (BadRequestException | JsonNotFoundException | NotFoundException e) {
            System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Método para listar solicitudes del usuario actual según el estado
     * @param usuario que esta logueado para verificar perfil con permisos
     * @param estadoSolicitud por el cual se quiere filtrar
     */
    private void listarMisSolicitudes(Usuario usuario, EstadoSolicitud estadoSolicitud){
        if(seguridad.verificarPermiso(usuario, Permisos.VER_SOLICITUDES_CAMBIO)){
            try{
                var solicitudes = solicitudCambioAulaService
                        .listarSolicitudesPorEstadoYProfesor(estadoSolicitud, usuario.getProfesor().getId());
                if (solicitudes.isEmpty()){
                    System.out.println(STR."\nNo tiene solicitudes \{estadoSolicitud.toString().toLowerCase()}s");
                    return;
                }
                System.out.println("\nSolicitudes:");
                System.out.println("==============================");
                solicitudes.forEach(System.out::println);
                System.out.println("==============================");
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
                var reservas = reservaService.listarReservasPorProfesor(usuario.getProfesor().getId());
                if (reservas.isEmpty()){
                    System.out.println("\nNo tienes reservas");
                    return;
                }
                System.out.println("\nReservas:");
                System.out.println("==============================");
                reservas.forEach(System.out::println);
                System.out.println("==============================");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para cambiar contraseña del profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void cambiarPassword(Usuario usuario){
        if(seguridad.verificarPermiso(usuario, Permisos.CAMBIAR_PASSWORD)){
            try {
                var usuarioNuevo = MenuUtils.cambiarPassword(usuario);
                usuarioService.modificar(usuarioNuevo);
                System.out.println("\nContraseña modificada correctamente.");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para cambiar tu contraseña.");
        }
    }
}
