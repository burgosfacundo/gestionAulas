package org.example.menu;

import org.example.enums.EstadoSolicitud;
import org.example.enums.Permisos;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.security.Seguridad;
import org.example.service.*;
import org.example.utils.Utils;

import java.time.LocalDate;
import java.util.*;

public class MenuAdministrador {
    private final Seguridad seguridad = new Seguridad();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final AsignaturaService asignaturaService = new AsignaturaService();
    private final InscripcionService inscripcionService = new InscripcionService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final SolicitudCambioAulaService solicitudCambioAulaService = new SolicitudCambioAulaService();
    private final ProfesorService profesorService = new ProfesorService();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Método para iniciar menu principal de admin
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    public void iniciarMenuAdmin(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\nElija una opción:");
            System.out.println("1. Gestionar espacios");
            System.out.println("2. Gestionar reservas");
            System.out.println("3. Gestionar solicitudes");
            System.out.println("4. Gestionar usuarios");
            System.out.println("5. Salir");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuEspacios(usuario);
                case 2 -> menuReserva(usuario);
                case 3 -> menuSolicitudes(usuario);
                case 4 -> menuUsuarios(usuario);
                case 5 -> salir = true;
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

        while (!salir) {
            System.out.println("\nElija una opción:");
            System.out.println("1. Crear espacio");
            System.out.println("2. Listar espacios y filtros");
            System.out.println("3. Modificar espacio");
            System.out.println("4. Eliminar espacio");
            System.out.println("5. Salir");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

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
        int tipo;
        do {
            tipo = Utils.leerEntero("""
                Elija el tipo de Espacio:
                1. Aula
                2. Laboratorio
                -\s
                """);
            if (tipo != 1 && tipo != 2) {
                System.out.println("Opción inválida, debe elegir 1 (Aula) o 2 (Laboratorio).");
            }
        } while (tipo != 1 && tipo != 2);

        int numero;
        do {
            numero = Utils.leerEntero("Ingrese el número de aula: ");
            if (numero <= 0) {
                System.out.println("El número de aula debe ser mayor que 0.");
            }
        } while (numero <= 0);

        int capacidad;
        do {
            capacidad = Utils.leerEntero("Ingrese la capacidad del aula: ");
            if (capacidad <= 0) {
                System.out.println("La capacidad debe ser mayor que 0.");
            }
        } while (capacidad <= 0);

        boolean tieneProyector = Utils.leerConfirmacion("¿El aula tiene proyector?");

        boolean tieneTV = Utils.leerConfirmacion("¿El aula tiene televisor?");

        if (tipo == 2) {
            int computadoras;
            do {
                computadoras = Utils.leerEntero("Ingrese la cantidad de computadoras: ");
                if (computadoras < 0) {
                    System.out.println("La cantidad de computadoras no puede ser negativa.");
                }
            } while (computadoras < 0);
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
                int numero = Utils.leerEntero("Ingrese el id del espacio que desea modificar: ");

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


                    var opcion = Utils.leerEntero("Seleccione el atributo que desea modificar (1-5): ");

                    switch (opcion) {
                        case 1:
                            aula.setNumero(Utils.leerEntero("Ingrese el nuevo número: "));
                            break;
                        case 2:
                            aula.setCapacidad(Utils.leerEntero("Ingrese la nueva capacidad: "));
                            break;
                        case 3:
                            aula.setTieneProyector(Utils.leerConfirmacion("¿Tiene proyector?"));
                            break;
                        case 4:
                            aula.setTieneTV(Utils.leerConfirmacion("¿Tiene TV?"));
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
                int id = Utils.leerEntero("Ingrese el id del espacio a eliminar");
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
            System.out.println("10. Filtrar aulas disponibles");
            System.out.println("11. Filtrar laboratorios disponibles");
            System.out.println("12. Salir");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

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
                case 10 -> filtrarAulasDisponibles(usuario);
                case 11 -> filtrarLaboratoriosDisponibles(usuario);
                case 12 -> salir = true;
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
                var capacidad = Utils.obtenerCapacidadEspacio();
                var tieneProyector = Utils.obtenerProyectoEspacio();
                var tieneTV = Utils.obtenerTvEspacio();

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
                var capacidad = Utils.obtenerCapacidadEspacio();
                var tieneProyector = Utils.obtenerProyectoEspacio();
                var tieneTV = Utils.obtenerTvEspacio();

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
                var capacidad = Utils.obtenerCapacidadEspacio();
                var tieneProyector = Utils.obtenerProyectoEspacio();
                var tieneTV = Utils.obtenerTvEspacio();

                var computadoras = Utils.obtenerCantidadComputadoras();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService
                        .filtrarLaboratoriosPorCondiciones(capacidad, tieneProyector, tieneTV,computadoras);
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
     * Método para listar todos los espacios disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarEspaciosDisponibles(Usuario usuario) {
        if(seguridad.verificarPermiso(usuario, Permisos.VER_AULAS))
        {
            try
            {
                // Pedir fecha de inicio
                LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");

                // Pedir fecha de fin
                LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");


                var diasYBloques = Utils.leerDiasYBloques();

                // Llamar a la función y listar lab y aulas disponibles
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
                /// Pedir fecha de inicio
                LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");

                // Pedir fecha de fin
                LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");


                var diasYBloques =  Utils.leerDiasYBloques();

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
                LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");

                // Pedir fecha de fin
                LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");


                var diasYBloques = Utils.leerDiasYBloques();
                // Llamar a la función y listar laboratorios disponibles
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
     * Método para submenu reserva
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuReserva(Usuario usuario) {
        boolean salir = false;

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

            int opcion = Utils.leerEntero("Seleccione una opción: ");

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

            /// Pedir fecha de inicio
            LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");
            reserva.setFechaInicio(fechaInicio);

            // Pedir fecha de fin
            LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");
            reserva.setFechaFin(fechaFin);


            var diasYBloques = Utils.leerDiasYBloques();
            reserva.setDiasYBloques(diasYBloques);

            aulaService.listarEspaciosDisponibles(fechaInicio,fechaFin,diasYBloques).forEach(System.out::println);
            int idAula = Utils.leerEntero("Ingrese el id del espacio: ");
            reserva.setAula(new Aula(idAula));

            inscripcionService.listar().forEach(System.out::println);
            int idInscripcion = Utils.leerEntero("Ingrese el id de la inscripción: ");
            reserva.setInscripcion(new Inscripcion(idInscripcion));

            reservaService.guardar(reserva);
            System.out.println("Reserva creada exitosamente.");
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
                var idProfesor = Utils.leerEntero("Ingresa el id del profesor: ");
                reservaService.listarReservasPorProfesor(idProfesor).forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por comisión
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXComision(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                var comision = Utils.leerTexto("Ingresa la comisión: ");
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
                var idAsignatura = Utils.leerEntero("Ingresa el id de la asignatura: ");
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

                // Solicitar el número de la reserva a modificar
                int numero = Utils.leerEntero("Ingrese el id de la reserva que desea modificar: ");

                // Obtener la reserva por id
                Reserva reserva = reservaService.obtener(numero);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual de la reserva
                    System.out.println("\nInformación actual de la reserva:");
                    System.out.println(STR."1. Fecha de Inicio: \{reserva.getFechaInicio()}");
                    System.out.println(STR."2. Fecha de Fin: \{reserva.getFechaFin()}");
                    System.out.println(STR."3. Dias y Horarios: \{reserva.getDiasYBloques()}");
                    System.out.println(STR."4. \{reserva.getAula()}");
                    System.out.println(STR."5. \{reserva.getInscripcion()}");
                    System.out.println("6. Guardar cambios y salir");

                    var opcion = Utils.leerEntero("Seleccione el atributo que desea modificar (1-6): ");

                    switch (opcion) {
                        case 1:
                            // Pedir fecha de inicio
                            LocalDate fechaInicio = Utils.leerFecha("Ingrese la fecha de inicio");
                            reserva.setFechaInicio(fechaInicio);
                            break;
                        case 2:
                            LocalDate fechaFin = Utils.leerFecha("Ingrese la fecha de fin");

                            reserva.setFechaFin(fechaFin);
                            break;
                        case 3:
                            reserva.setDiasYBloques(Utils.leerDiasYBloques());
                            break;
                        case 4:
                            aulaService.listar().forEach(System.out::println);
                            int idAula = Utils.leerEntero("Ingrese el id del espacio: ");

                            reserva.setAula(new Aula(idAula));
                            break;
                        case 5:
                            inscripcionService.listar().forEach(System.out::println);
                            int idInscripcion = Utils.leerEntero("Ingrese el id de la inscripción: ");

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
                int id = Utils.leerEntero("Ingrese el id de la reserva a eliminar");
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

        while (!salir) {
            System.out.println("Elija una opción:");
            System.out.println("1.Listar");
            System.out.println("2.Revisar solicitudes pendientes");
            System.out.println("3.Salir.");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

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

        while (!salir) {
            System.out.println("\nElija una opción de listado o filtro:");
            System.out.println("1. Listar todas las solicitudes");
            System.out.println("2. Listar todas las solicitudes pendientes");
            System.out.println("3. Listar todas las solicitudes aprobadas");
            System.out.println("4. Listar todas las solicitudes rechazadas");
            System.out.println("5. Listar todas las solicitudes x profesor");
            System.out.println("6. Salir");

            int opcion = Utils.leerEntero("Seleccione una opción: ");

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
                var idProfesor = Utils.leerEntero("Ingresa el id del profesor: ");

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
        if (seguridad.verificarPermiso(usuario,Permisos.GESTIONAR_CAMBIOS)){
            try {
                solicitudCambioAulaService.listarSolicitudesPorEstado(EstadoSolicitud.PENDIENTE)
                        .forEach(System.out::println);

                var idSolicitud = Utils.leerEntero("Ingresa el id de la solicitud: ");

                var estado= Utils.leerEntero("""
                        Elija una opción:
                        1.Rechazar
                        2.Aprobar
                        -\s
                        """);

                var motivo = Utils.leerTexto("Indica el motivo (Enter si no quieres indicarlo): ");

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


    /**
     * Método para el submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuUsuarios(Usuario usuario){
        boolean salir = false;

        while (!salir) {
            System.out.println("Elija una opción:");
            System.out.println("1. Crear usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Modificar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Cambiar contraseña");
            System.out.println("6. Salir.");

            int opcion = Utils.leerEntero("Seleccione una opción: ");
            scanner.nextLine();

            switch (opcion) {
                case 1 -> crearUsuario(usuario);
                case 2 -> listarUsuarios(usuario);
                case 3 -> modificarUsuario(usuario);
                case 4 -> eliminarUsuario(usuario);
                case 5 -> cambiarPassword(usuario);
                case 6 -> salir = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    /**
     * Método para crear usuario del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearUsuario(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_USUARIO)) {
            System.out.println("No posees el permiso para crear un usuario.");
            return;
        }

        try{
            // Pedir nombre de usuario
            var username = Utils.leerTexto("Ingrese el nombre de usuario: ");

            var contrasenia = Utils.leerTexto("Ingrese la contraseña: ");

            var idRol = Utils.leerEntero("""
                            Ingrese el rol:
                            1.Admin
                            2.Profesor
                            -\s
                            """);

            profesorService.listar().forEach(System.out::println);
            var idProfesor = Utils.leerEntero("Ingresa el id del profesor que representa: ");
            usuarioService.guardar(new Usuario(null,username,contrasenia,new Rol(idRol),new Profesor(idProfesor)));
            System.out.println("Usuario creado exitosamente.");
        } catch (JsonNotFoundException | BadRequestException | NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Método para listar todos los usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarUsuarios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_USUARIOS)) {
            try {
                usuarioService.listar().forEach(System.out::println);
            } catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No posees el permiso para ver los usuarios.");
        }
    }

    /**
     * Método para modificar usuarios del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarUsuario(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_USUARIO)) {
            try {
                usuarioService.listar().forEach(System.out::println);

                // Solicitar el número del usuario a modificar
                int id = Utils.leerEntero("Ingrese el id del usuario que desea modificar: ");

                // Obtener el usuario por id
                var user= usuarioService.obtener(id);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual del usuario
                    System.out.println("\nInformación actual del usuario:");
                    System.out.println(STR."1. Nombre de usuario: \{user.getUsername()}");
                    System.out.println(STR."2. Contraseña: \{user.getPassword()}");
                    System.out.println(STR."3. Rol: \{user.getRol().getNombre()}");
                    System.out.println("4. Guardar cambios y salir");

                    var opcion = Utils.leerEntero("Seleccione el atributo que desea modificar (1-4): ");

                    switch (opcion) {
                        case 1:
                            // Pedir username
                            user.setUsername( Utils.leerTexto("Ingrese el nuevo nombre de usuario: "));
                            break;
                        case 2:
                            // Pedir password
                            user.setPassword(Utils.leerTexto("Ingrese la nueva contraseña: "));
                            break;
                        case 3:
                            // Pedir rol
                            boolean sigo = true;
                            int rol = 2;
                            while (sigo) {

                                rol = Utils.leerEntero("""
                                    Ingresa el nuevo rol:
                                    1. Admin
                                    2. Profesor
                                    """);

                                if (rol == 1 || rol == 2) {
                                    sigo = false;
                                } else {
                                    System.out.println("Opción inválida. Vuelve a intentarlo.");
                                }
                            }

                            user.setRol(new Rol(rol));
                            break;
                        case 4:
                            // Guardar cambios
                            usuarioService.modificar(usuario);
                            System.out.println("El usuario se ha modificado exitosamente.");
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
            System.out.println("No posees el permiso para modificar reservas.");
        }
    }

    /**
     * Método para eliminar un usuario del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void eliminarUsuario(Usuario usuario) {
        if(seguridad.verificarPermiso(usuario, Permisos.ELIMINAR_USUARIO))
        {
            try {
                usuarioService.listar().forEach(System.out::println);
                usuarioService.eliminar(Utils.leerEntero("Ingrese el id del usuario a eliminar: "));
                System.out.println("Usuario eliminado exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("No posees el permiso para eliminar usuarios.");
        }
    }

    /**
     * Método para cambiar contrasenia del submenu usuarios
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