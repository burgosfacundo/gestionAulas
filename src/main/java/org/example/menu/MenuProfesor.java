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
import org.example.utils.Utils;

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
            System.out.println("Elija una opcion:");
            System.out.println("1.Listar espacios.");
            System.out.println("2.Solicitar reserva.");
            System.out.println("3.Listar mis solicitudes pendientes.");
            System.out.println("4.Listar mis solicitudes aprobadas.");
            System.out.println("5.Listar mis solicitudes rechazadas.");
            System.out.println("6.Listar mis reservas.");
            System.out.println("7.Cambiar mi contraseña.");
            System.out.println("8.Salir.");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuListarEspacios(usuario);
                case 2 -> solicitarCambio(usuario);
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

        while (!salir) {
            System.out.println("\nElija una opción de listado o filtro:");
            System.out.println("1. Listar todas las aulas");
            System.out.println("2. Listar todos los laboratorios");
            System.out.println("3. Filtrar aulas disponibles");
            System.out.println("4. Filtrar laboratorios disponibles");
            System.out.println("5. Salir");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> listarAulas(usuario);
                case 2 -> listarLaboratorios(usuario);
                case 3 -> filtrarAulasDisponibles(usuario);
                case 4 -> filtrarLaboratoriosDisponibles(usuario);
                case 5 -> salir = true;
                default -> System.out.println("Opción inválida.");
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
     * Método para filtrar todas las aulas estándar disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarAulasDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var capacidad = Utils.obtenerCapacidadEspacio();
                var tieneProyector = Utils.obtenerProyectoEspacio();
                var tieneTV = Utils.obtenerTvEspacio();

                LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");


                LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");

                var diasYBloques = Utils.leerDiasYBloques();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.listarAulasDisponiblesPorSolicitud(capacidad, tieneProyector,
                        tieneTV,fechaInicio,fechaFin,diasYBloques);

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
     * Método para filtrar todos los laboratorios disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarLaboratoriosDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
                var capacidad = Utils.obtenerCapacidadEspacio();
                var tieneProyector = Utils.obtenerProyectoEspacio();
                var tieneTV = Utils.obtenerTvEspacio();

                LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");


                LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");

                var diasYBloques = Utils.leerDiasYBloques();

                var computadoras = Utils.obtenerCantidadComputadoras();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService
                        .listarLaboratoriosDisponiblesPorSolicitud(computadoras,capacidad, tieneProyector, tieneTV,fechaInicio,fechaFin,diasYBloques);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("No se encontraron laboratorios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("Laboratorios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."Error: \{e.getMessage()}");
            }
        } else {
            System.out.println("No posees el permiso para ver los laboratorios.");
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
                List<Reserva> reservas = reservaService.listarReservasPorProfesor(usuario.getProfesor().getId());
                reservas.forEach(System.out::println);


                int idReserva = Utils.leerEntero("Ingresa el id de la reserva en la cual necesita un cambio:");
                Reserva reserva = reservaService.obtener(idReserva);


                // Extraer fecha inicio y fin. Por defecto tipo es permanente.
                var tipoSolicitud = TipoSolicitud.PERMANENTE;
                LocalDate fechaInicio = reserva.getFechaInicio();
                LocalDate fechaFin = reserva.getFechaFin();

                // consultar al user si es temporal
                int opcion;
                do {
                    opcion = Utils.leerEntero("""
                            Ingresa que tipo de cambio necesita:
                            1. Temporal
                            2. Permanente
                            -\s
                            """);
                } while (opcion != 1 && opcion != 2);

                if(opcion == 1){
                    tipoSolicitud = TipoSolicitud.TEMPORAL;

                    // Pedir fecha de inicio
                    fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");


                    // Pedir fecha de fin
                    fechaFin = Utils.leerFecha("Ingrese la fecha de fin");

                }

                // obtener días y bloque horario
                Map<DayOfWeek, Set<BloqueHorario>> diasYBloques = Utils.leerDiasYBloques();

                // Comentario adicional
                String comentarioProfesor = Utils.leerTexto("Escriba un comentario adicional para la solicitud: ");


                List<? extends Aula> aulasCoinciden;
                if(reserva.getAula() instanceof Laboratorio) {

                    aulasCoinciden = aulaService.listarLaboratoriosDisponiblesPorSolicitud(
                            ((Laboratorio) reserva.getAula()).getComputadoras(), reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }else{
                    aulasCoinciden = aulaService.listarAulasDisponiblesPorSolicitud(reserva.getAula().getCapacidad(),
                            reserva.getAula().isTieneProyector(), reserva.getAula().isTieneTV(), fechaInicio, fechaFin, diasYBloques);
                }

                if (!aulasCoinciden.isEmpty()){
                    System.out.println("Espacios disponibles: ");
                    aulasCoinciden.forEach(System.out::println);
                    Aula aula = aulaService.obtener(Utils.leerEntero("Ingresa el id del aula que desea solicitar: "));
                    SolicitudCambioAula solicitud = new SolicitudCambioAula(null, reserva.getInscripcion().getProfesor(), reserva,
                            aula, tipoSolicitud, fechaInicio, fechaFin, diasYBloques, comentarioProfesor);
                    solicitudCambioAulaService.guardar(solicitud);
                }else {
                    System.out.println("No hay espacios disponibles con esas características en las fechas ingresadas");
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
        if(seguridad.verificarPermiso(usuario, Permisos.SOLICITAR_CAMBIO)){
            try{
                solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(estadoSolicitud, usuario.getProfesor().getId())
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
                reservaService.listarReservasPorProfesor(usuario.getProfesor().getId()).forEach(System.out::println);
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
        if(seguridad.verificarPermiso(usuario, Permisos.CAMBIAR_PASSWORD))
        {
            try {
                boolean passwordsCoinciden = false;
                while (!passwordsCoinciden) {
                    var password = Utils.leerTexto("Ingresa la nueva contraseña: ");
                    var validaPassword = Utils.leerTexto("Ingresa nuevamente la contraseña: ");

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
            System.out.println("No posees el permiso para cambiar tu contraseña.");
        }
    }

}
