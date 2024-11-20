package org.example.menu;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.enums.Permisos;
import org.example.exception.*;
import org.example.model.*;
import org.example.security.Seguridad;
import org.example.service.*;
import org.example.utils.MenuUtils;
import org.example.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.example.utils.MenuUtils.*;

public class MenuAdministrador {
    private final Seguridad seguridad = new Seguridad();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final AsignaturaService asignaturaService = new AsignaturaService();
    private final InscripcionService inscripcionService = new InscripcionService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final SolicitudCambioAulaService solicitudCambioAulaService = new SolicitudCambioAulaService();
    private final ProfesorService profesorService = new ProfesorService();

    /**
     * Método para iniciar menu principal de admin
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    public void iniciarMenuAdmin(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Menú Administrador");
            System.out.println("======================================");
            System.out.println("Elija una opción:");
            System.out.println("1. Gestionar espacios");
            System.out.println("2. Gestionar reservas");
            System.out.println("3. Gestionar solicitudes");
            System.out.println("4. Gestionar usuarios");
            System.out.println("5. Salir");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuEspacios(usuario);
                case 2 -> menuReserva(usuario);
                case 3 -> menuSolicitudes(usuario);
                case 4 -> menuUsuarios(usuario);
                case 5 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
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
            System.out.println("\n====================================");
            System.out.println("     Menú Espacios");
            System.out.println("======================================");
            System.out.println("Elija una opción:");
            System.out.println("1. Crear espacio");
            System.out.println("2. Listar espacios y filtros");
            System.out.println("3. Modificar espacio");
            System.out.println("4. Eliminar espacio");
            System.out.println("5. Salir");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> crearEspacio(usuario);
                case 2 -> menuListarEspacios(usuario);
                case 3 -> modificarEspacio(usuario);
                case 4 -> eliminarEspacio(usuario);
                case 5 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
            }
        }
    }

    /**
     * Método para crear espacio del submenu espacio
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearEspacio(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_ESPACIO)) {
            System.out.println("\nNo posees el permiso para crear un aula.");
            return;
        }
        try{
            Aula aula;
            int tipo;
            do {
                tipo = MenuUtils.leerEntero("""
                    \n
                    Elija el tipo de Espacio:
                    1. Aula
                    2. Laboratorio
                    
                    """);
                if (tipo != 1 && tipo != 2) {
                    System.out.println("\nOpción inválida, debe elegir 1 (Aula) o 2 (Laboratorio).");
                }
            } while (tipo != 1 && tipo != 2);

            int capacidad;
            do {
                capacidad = MenuUtils.leerEntero("\nIngrese la capacidad del aula: ");
                if (capacidad <= 0) {
                    System.out.println("\nLa capacidad debe ser mayor que 0.");
                }
            } while (capacidad <= 0);

            boolean tieneProyector = MenuUtils.leerConfirmacion("\n¿El aula tiene proyector?");

            boolean tieneTV = MenuUtils.leerConfirmacion("\n¿El aula tiene televisor?");

            int numero;
            do {
                numero = MenuUtils.leerEntero("\nIngrese el número de aula: ");
                if (numero <= 0) {
                    System.out.println("\nEl número de aula debe ser mayor que 0.");
                }
            } while (numero <= 0);

            if (tipo == 2) {
                int computadoras;
                do {
                    computadoras = MenuUtils.leerEntero("\nIngrese la cantidad de computadoras: ");
                    if (computadoras < 0) {
                        System.out.println("\nLa cantidad de computadoras no puede ser negativa.");
                    }
                } while (computadoras < 0);
                aula = new Laboratorio(null, numero, capacidad, tieneProyector, tieneTV, computadoras);
            } else {
                aula = new Aula(null, numero, capacidad, tieneProyector, tieneTV);
            }

            aulaService.guardar(aula);
            System.out.println("\nEspacio creado exitosamente.");
        } catch (JsonNotFoundException | BadRequestException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Método para modificar espacio del submenu espacio
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarEspacio(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_ESPACIO)) {
            System.out.println("\nNo posees el permiso para modificar espacios.");
            return;
        }

        try {
            var aulas = aulaService.listar();
            if (aulas.isEmpty()) {
                System.out.println("\nNo hay espacios disponibles.");
                return;
            }

            System.out.println("\nEspacios:");
            System.out.println("==========================");
            aulas.forEach(System.out::println);
            System.out.println("==========================");

            // Solicitar el número del espacio a modificar
            int numero = MenuUtils.leerEntero("\nIngrese el id del espacio que desea modificar: ");

            // Obtener el espacio por número
            Aula aula = aulaService.obtener(numero);

            boolean continuar = true;
            while (continuar) {
                // Mostrar información actual del espacio
                System.out.println("\nInformación actual del espacio:");
                System.out.println(STR."1. Número: \{aula.getNumero()}");
                System.out.println(STR."2. Capacidad: \{aula.getCapacidad()}");
                System.out.println(STR."3. Tiene proyector: \{aula.isTieneProyector() ? "sí" : "no"}");
                System.out.println(STR."4. Tiene TV: \{aula.isTieneTV() ? "sí" : "no"}");

                int opcion;
                if (aula instanceof Laboratorio laboratorio) {
                    System.out.println(STR."5. Computadoras: \{laboratorio.getComputadoras()}");
                    System.out.println("6. Guardar cambios y salir");
                    opcion = MenuUtils.leerEntero("\nSeleccione el atributo que desea modificar (1-6): ");

                    switch (opcion) {
                        case 1 -> aula.setNumero(MenuUtils.leerEntero("\nIngrese el nuevo número: "));
                        case 2 -> aula.setCapacidad(MenuUtils.leerEntero("\nIngrese la nueva capacidad: "));
                        case 3 -> aula.setTieneProyector(MenuUtils.leerConfirmacion("\n¿Tiene proyector?"));
                        case 4 -> aula.setTieneTV(MenuUtils.leerConfirmacion("\n¿Tiene TV?"));
                        case 5 -> laboratorio.setComputadoras(MenuUtils.leerEntero("\nIngrese la nueva cantidad de computadoras: "));
                        case 6 -> {
                            aulaService.modificar(aula);
                            System.out.println("\nEl espacio se ha modificado exitosamente.");
                            continuar = false;
                        }
                        default -> System.out.println("\nOpción no válida.");
                    }
                } else {
                    System.out.println("5. Guardar cambios y salir");
                    opcion = MenuUtils.leerEntero("\nSeleccione el atributo que desea modificar (1-5): ");

                    switch (opcion) {
                        case 1 -> aula.setNumero(MenuUtils.leerEntero("\nIngrese el nuevo número: "));
                        case 2 -> aula.setCapacidad(MenuUtils.leerEntero("\nIngrese la nueva capacidad: "));
                        case 3 -> aula.setTieneProyector(MenuUtils.leerConfirmacion("\n¿Tiene proyector?"));
                        case 4 -> aula.setTieneTV(MenuUtils.leerConfirmacion("\n¿Tiene TV?"));
                        case 5 -> {
                            aulaService.modificar(aula);
                            System.out.println("\nEl espacio se ha modificado exitosamente.");
                            continuar = false;
                        }
                        default -> System.out.println("\nOpción no válida.");
                    }
                }
            }
        } catch (JsonNotFoundException | NotFoundException e) {
            System.out.println(e.getMessage());
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
                var aulas = aulaService.listar();
                if (aulas.isEmpty()){
                    System.out.println("\nNo hay espacios");
                    return;
                }
                System.out.println("\nEspacios:");
                System.out.println("==========================");
                aulas.forEach(System.out::println);
                System.out.println("==========================");

                int id = MenuUtils.leerEntero("\nIngrese el id del espacio a eliminar: ");
                aulaService.eliminar(id);
                System.out.println("\nEspacio eliminado exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para eliminar espacios.");
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
            System.out.println("1. Listar todos los espacios");
            System.out.println("2. Listar todas las aulas");
            System.out.println("3. Listar todos los laboratorios");
            System.out.println("4. Filtrar espacios por condiciones");
            System.out.println("5. Filtrar aulas por condiciones");
            System.out.println("6. Filtrar laboratorios por condiciones");
            System.out.println("7. Listar espacios disponibles");
            System.out.println("8. Listar aulas disponibles");
            System.out.println("9. Listar laboratorios disponibles");
            System.out.println("10. Filtrar espacios disponibles");
            System.out.println("11. Filtrar aulas disponibles");
            System.out.println("12. Filtrar laboratorios disponibles");
            System.out.println("13. Salir");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

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
                case 10 -> filtrarEspaciosDisponibles(usuario);
                case 11 -> filtrarAulasDisponibles(usuario);
                case 12 -> filtrarLaboratoriosDisponibles(usuario);
                case 13 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
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
                var aulas = aulaService.listar();
                if (aulas.isEmpty()){
                    System.out.println("\nNo hay espacios");
                    return;
                }
                System.out.println("\nEspacios:");
                System.out.println("==========================");
                aulas.forEach(System.out::println);
                System.out.println("==========================");
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los espacios.");
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
                System.out.println("==========================");
                aulas.forEach(System.out::println);
                System.out.println("==========================");
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
                System.out.println("==========================");
                laboratorios.forEach(System.out::println);
                System.out.println("==========================");
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los laboratorios.");
        }
    }

    /**
     * Método para filtrar todos los espacios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarEspacios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_ESPACIOS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.filtrarEspaciosPorCondiciones(capacidad, tieneProyector, tieneTV);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron espacios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nEspacios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\n\{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los espacios.");
        }
    }

    /**
     * Método para filtrar todas las aulas estándar
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarAulas(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.filtrarAulasPorCondiciones(capacidad, tieneProyector, tieneTV);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron aulas que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nAulas encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\n\{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las aulas.");
        }
    }

    /**
     * Método para filtrar todos los laboratorios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarLaboratorios(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_LABORATORIOS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                var computadoras = MenuUtils.obtenerCantidadComputadoras();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService
                        .filtrarLaboratoriosPorCondiciones(capacidad, tieneProyector, tieneTV,computadoras);
                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron laboratorios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nLaboratorios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\n\{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los laboratorios.");
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
                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = MenuUtils.leerDiasYBloques();

                var espacios = aulaService.listarEspaciosDisponibles(fechaInicio, fechaFin, diasYBloques);
                if (espacios.isEmpty()){
                    System.out.println("\nNo hay espacios disponibles");
                    return;
                }
                // Llamar a la función y listar lab y aulas disponibles
                System.out.println("\nEspacios disponibles:");
                espacios.forEach(System.out::println);
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para ver los espacios.");
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
                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques =  MenuUtils.leerDiasYBloques();

                var aulas = aulaService.listarAulasDisponibles(fechaInicio, fechaFin, diasYBloques);
                if (aulas.isEmpty()){
                    System.out.println("\nNo hay aulas disponibles");
                    return;
                }
                // Llamar a la función y listar lab y aulas disponibles
                System.out.println("\nAulas disponibles:");
                aulas.forEach(System.out::println);
            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para ver las aulas.");
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
                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);


                var diasYBloques = MenuUtils.leerDiasYBloques();
                // Llamar a la función y listar laboratorios disponibles
                var laboratorios = aulaService.listarLaboratoriosDisponibles(fechaInicio, fechaFin, diasYBloques);
                if (laboratorios.isEmpty()){
                    System.out.println("\nNo hay laboratorios disponibles");
                    return;
                }

                System.out.println("\nLaboratorios disponibles:");
                laboratorios.forEach(System.out::println);

            } catch (JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para ver los laboratorios.");
        }
    }

    /**
     * Método para filtrar todas las aulas estándar disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarEspaciosDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_ESPACIOS)) {
            try {
                var capacidad = MenuUtils.obtenerCapacidadEspacio();
                var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                var tieneTV = MenuUtils.obtenerTvEspacio();

                var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = MenuUtils.leerDiasYBloques();

                // Filtrar los espacios según los parámetros ingresados
                var espaciosFiltrados = aulaService.listarEspaciosDisponiblesConCondiciones(capacidad, tieneProyector,
                        tieneTV,fechaInicio,fechaFin,diasYBloques);

                if (espaciosFiltrados.isEmpty()) {
                    System.out.println("\nNo se encontraron espacios que coincidan con los criterios de búsqueda.");
                } else {
                    System.out.println("\nEspacios encontrados:");
                    espaciosFiltrados.forEach(System.out::println);
                }

            } catch (JsonNotFoundException e) {
                System.out.println(STR."\nError: \{e.getMessage()}");
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los espacios.");
        }
    }

    /**
     * Método para filtrar todas las aulas estándar disponibles
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void filtrarAulasDisponibles(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_AULAS)) {
            try {
                var capacidad = obtenerCapacidadEspacio();
                var tieneProyector = obtenerProyectorEspacio();
                var tieneTV = obtenerTvEspacio();

                var rangoFechas = leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = leerDiasYBloques();

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
                var capacidad = obtenerCapacidadEspacio();
                var tieneProyector = obtenerProyectorEspacio();
                var tieneTV = obtenerTvEspacio();

                var rangoFechas = leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
                LocalDate fechaInicio = rangoFechas.get(0);
                LocalDate fechaFin = rangoFechas.get(1);

                var diasYBloques = leerDiasYBloques();

                var computadoras = obtenerCantidadComputadoras();

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
     * Método para submenu reserva
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuReserva(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Menú Reservas");
            System.out.println("======================================");
            System.out.println("Elija una opción:");
            System.out.println("1.Crear reserva.");
            System.out.println("2.Listar todas las reservas.");
            System.out.println("3.Listar reservas por profesor.");
            System.out.println("4.Listar reservas por comisión");
            System.out.println("5.Listar reservas por asignatura");
            System.out.println("6.Modificar reserva");
            System.out.println("7.Eliminar reserva");
            System.out.println("8.Salir.");

            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> crearReserva(usuario);
                case 2 -> listarReservas(usuario);
                case 3 -> listarReservasXProfesor(usuario);
                case 4 -> listarReservasXComision(usuario);
                case 5 -> listarReservasXAsignatura(usuario);
                case 6 -> modificarReserva(usuario);
                case 7 -> eliminarReserva(usuario);
                case 8 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
            }
        }
    }

    /**
     * Método para crear reserva del submenu reserva
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearReserva(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_RESERVA)) {
            System.out.println("\nNo posees el permiso para crear una reserva.");
            return;
        }

        try{
            Reserva reserva = new Reserva(null);

            var rangoFechas = MenuUtils.leerRangoDeFechas("\nIngrese la fecha de inicio:", "\nIngrese la fecha de fin:");
            LocalDate fechaInicio = rangoFechas.get(0);
            LocalDate fechaFin = rangoFechas.get(1);
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);

            Map<DayOfWeek,Set<BloqueHorario>> diasYBloques;

            if (fechaInicio.equals(fechaFin)){
                diasYBloques = new HashMap<>();
                var bloques = MenuUtils.leerBloques(fechaInicio.getDayOfWeek());

                diasYBloques.put(fechaInicio.getDayOfWeek(),bloques);
                reserva.setDiasYBloques(diasYBloques);
            }else {
                diasYBloques = MenuUtils.leerDiasYBloques();
                reserva.setDiasYBloques(diasYBloques);
            }

            System.out.println("\nInscripciones:");
            System.out.println("===============================");
            inscripcionService.listar().forEach(System.out::println);
            System.out.println("===============================");
            int idInscripcion = leerEntero("\nIngrese el id de la inscripción: ");
            reserva.setInscripcion(new Inscripcion(idInscripcion));

            // Verificar superposición de días, horarios y fechas
            var inscripcion = inscripcionService.obtener(idInscripcion);
            var reservasExistentes = reservaService.listarPorInscripcion(idInscripcion);

            boolean haySuperposicion = reservasExistentes.stream()
                    .anyMatch(reservaExistente ->
                            Utils.tieneSolapamientoEnDiasYBloques(reservaExistente.getDiasYBloques(), diasYBloques) &&
                                    Utils.seSolapanFechas(reservaExistente.getFechaInicio(), reservaExistente.getFechaFin(), fechaInicio, fechaFin));

            if (haySuperposicion) {
                System.out.println("\nError: La inscripción ya tiene una reserva en los días y horarios seleccionados dentro del rango de fechas.");
                return;
            }

            int alumnosRequeridos = inscripcion.getCantidadAlumnos() +
                    (inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ? inscripcion.getMargenAlumnos() : 0);

            var tieneProyector = MenuUtils.obtenerProyectorEspacio();
            var tieneTv = MenuUtils.obtenerTvEspacio();

            List<? extends Aula> espaciosDisponibles;
            if (inscripcion.getAsignatura().isRequiereLaboratorio()){
                int computadoras = MenuUtils.leerEntero("\n Ingrese la cantidad de computadoras que requiere el laboratorio: ");
                espaciosDisponibles = aulaService.listarLaboratoriosDisponiblesConCondiciones(
                        computadoras, alumnosRequeridos, tieneProyector,
                        tieneTv,fechaInicio,fechaFin,diasYBloques);
            }else {
                espaciosDisponibles = aulaService.listarAulasDisponiblesConCondiciones(
                        alumnosRequeridos,tieneProyector, tieneTv,
                        fechaInicio,fechaFin,diasYBloques);
            }

            // Mostrar espacios disponibles
            if (espaciosDisponibles.isEmpty()) {
                System.out.println("\nNo hay espacios disponibles que cumplan con las condiciones.");
                return;
            }
            espaciosDisponibles.forEach(System.out::println);

            // Seleccionar espacio
            Aula aulaSeleccionada = null;
            while (aulaSeleccionada == null) {
                var idAula = MenuUtils.leerEntero("\nIngrese el id del espacio: ");
                aulaSeleccionada = espaciosDisponibles.stream()
                        .filter(aula -> aula.getId() == idAula)
                        .findFirst()
                        .orElse(null);

                if (aulaSeleccionada == null) {
                    System.out.println("\nError: El id ingresado no corresponde a un espacio disponible. Intente nuevamente.");
                }
            }

            reserva.setAula(aulaSeleccionada);

            // Guardar reserva
            reservaService.guardar(reserva);
            System.out.println("\nReserva creada exitosamente.");
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
                var reservas = reservaService.listar();
                if (reservas.isEmpty()){
                    System.out.println("\nNo hay reservas realizadas aun.");
                    return;
                }
                reservas.forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXProfesor(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                System.out.println("\nProfesores:");
                System.out.println("========================");
                profesorService.listar().forEach(System.out::println);
                System.out.println("========================");
                var idProfesor = MenuUtils.leerEntero("\nIngresa el id del profesor: ");
                var reservasXProfesor = reservaService.listarReservasPorProfesor(idProfesor);
                if (reservasXProfesor.isEmpty()){
                    System.out.println(STR."\nEl profesor con id: \{idProfesor} no tiene reservas");
                    return;
                }
                reservasXProfesor.forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por comisión
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXComision(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                var comision = MenuUtils.leerTexto("\nIngresa la comisión: ");
                var reservasXComision = reservaService.listarReservasPorComision(comision);
                if (reservasXComision.isEmpty()){
                    System.out.println(STR."\nLa comision \{comision} no tiene reservas");
                    return;
                }
                reservasXComision.forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para listar todas las reservas por asignatura
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarReservasXAsignatura(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_RESERVAS)) {
            try {
                System.out.println("\nAsignaturas:");
                System.out.println("==========================");
                asignaturaService.listar().forEach(System.out::println);
                System.out.println("==========================");
                var idAsignatura = MenuUtils.leerEntero("\nIngresa el id de la asignatura: ");
                var reservasXAsignatura = reservaService.listarReservasPorAsignatura(idAsignatura);
                if (reservasXAsignatura.isEmpty()){
                    System.out.println(STR."La asignatura con id: \{idAsignatura} no tiene reservas");
                    return;
                }
                reservasXAsignatura.forEach(System.out::println);
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las reservas.");
        }
    }

    /**
     * Método para modificar reserva del submenu reservas
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarReserva(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_RESERVA)) {
            try {
                var reservas = reservaService.listar();
                if (reservas.isEmpty()){
                    System.out.println("No hay reservas realizadas aun.");
                    return;
                }
                System.out.println("\nReservas:");
                System.out.println("============================");
                reservas.forEach(System.out::println);
                System.out.println("============================");

                // Solicitar el número de la reserva a modificar
                int numero = MenuUtils.leerEntero("\nIngrese el id de la reserva que desea modificar: ");

                // Obtener la reserva por id
                Reserva reserva = reservaService.obtener(numero);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual de la reserva
                    System.out.println("\nInformación actual de la reserva:");
                    System.out.println(STR."1. Fecha de Inicio: \{reserva.getFechaInicio()}");
                    System.out.println(STR."2. Fecha de Fin: \{reserva.getFechaFin()}");
                    System.out.println(STR."3. Dias y Horarios: \{Utils.formatDiasYBloques(reserva.getDiasYBloques())}");
                    System.out.println(STR."4. Aula: \{reserva.getAula().getId()}");
                    System.out.println(STR."5. Inscripción: \{reserva.getInscripcion().getId()}");
                    System.out.println("6. Guardar cambios y salir");

                    var opcion = MenuUtils.leerEntero("\nSeleccione el atributo que desea modificar (1-6): ");

                    switch (opcion) {
                        case 1:
                            while (true) {
                                LocalDate fechaInicio = MenuUtils.leerFecha("\nIngrese la fecha de inicio");
                                if (!reserva.getFechaFin().isBefore(fechaInicio)) {
                                    reserva.setFechaInicio(fechaInicio);

                                    // Validar si las fechas ahora son iguales
                                    if (reserva.getFechaInicio().equals(reserva.getFechaFin())) {
                                        System.out.println("\nLas fechas de inicio y fin ahora coinciden. Debe reconfigurar los días y bloques.");
                                        var diaSemana = reserva.getFechaInicio().getDayOfWeek();
                                        var bloques = MenuUtils.leerBloques(diaSemana);
                                        reserva.setDiasYBloques(Map.of(diaSemana, bloques));
                                    }
                                    break;
                                } else {
                                    System.out.println("Error: La fecha de inicio no puede ser posterior a la fecha de fin actual.");
                                }
                            }
                            break;

                        case 2:
                            while (true) {
                                LocalDate fechaFin = MenuUtils.leerFecha("\nIngrese la fecha de fin");
                                if (!fechaFin.isBefore(reserva.getFechaInicio())) {
                                    reserva.setFechaFin(fechaFin);

                                    // Validar si las fechas ahora son iguales
                                    if (reserva.getFechaInicio().equals(reserva.getFechaFin())) {
                                        System.out.println("\nLas fechas de inicio y fin ahora coinciden. Debe reconfigurar los días y bloques.");
                                        var diaSemana = reserva.getFechaInicio().getDayOfWeek();
                                        var bloques = MenuUtils.leerBloques(diaSemana);
                                        reserva.setDiasYBloques(Map.of(diaSemana, bloques));
                                    }
                                    break;
                                } else {
                                    System.out.println("Error: La fecha de inicio no puede ser posterior a la fecha de fin actual.");
                                }
                            }
                            break;
                        case 3:
                            var diasYBloques = reserva.getFechaInicio().equals(reserva.getFechaFin())
                                    ? Map.of(reserva.getFechaInicio().getDayOfWeek(),
                                    MenuUtils.leerBloques(reserva.getFechaInicio().getDayOfWeek()))
                                    : MenuUtils.leerDiasYBloques();
                            reserva.setDiasYBloques(diasYBloques);
                            break;
                        case 4:
                            var inscripcion = reserva.getInscripcion();
                            var tieneProyector = MenuUtils.obtenerProyectorEspacio();
                            var tieneTv = MenuUtils.obtenerTvEspacio();
                            int alumnosRequeridos = inscripcion.getCantidadAlumnos() +
                                    (inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ? inscripcion.getMargenAlumnos() : 0);

                            List<? extends Aula> espaciosDisponibles;
                            if (inscripcion.getAsignatura().isRequiereLaboratorio()){
                                int computadoras = MenuUtils.leerEntero("\n Ingrese la cantidad de computadoras que requiere el laboratorio: ");
                                espaciosDisponibles = aulaService.listarLaboratoriosDisponiblesConCondiciones(
                                        computadoras, alumnosRequeridos, tieneProyector,
                                        tieneTv,reserva.getFechaInicio(),reserva.getFechaFin(),reserva.getDiasYBloques());
                            }else {
                                espaciosDisponibles = aulaService.listarAulasDisponiblesConCondiciones(
                                        alumnosRequeridos,tieneProyector, tieneTv,
                                        reserva.getFechaInicio(),reserva.getFechaFin(),reserva.getDiasYBloques());
                            }

                            // Mostrar espacios disponibles
                            if (espaciosDisponibles.isEmpty()) {
                                System.out.println("\nNo hay espacios disponibles que cumplan con las condiciones.");
                                break;
                            }
                            espaciosDisponibles.forEach(System.out::println);

                            // Seleccionar espacio
                            Aula aulaSeleccionada = null;
                            while (aulaSeleccionada == null) {
                                var idAula = MenuUtils.leerEntero("\nIngrese el id del espacio: ");
                                aulaSeleccionada = espaciosDisponibles.stream()
                                        .filter(aula -> aula.getId() == idAula)
                                        .findFirst()
                                        .orElse(null);

                                if (aulaSeleccionada == null) {
                                    System.out.println("\nError: El id ingresado no corresponde a un espacio disponible. Intente nuevamente.");
                                }
                            }

                            reserva.setAula(aulaSeleccionada);
                            break;
                        case 5:
                            // Mostrar inscripciones disponibles
                            inscripcionService.listar().forEach(System.out::println);
                            int idInscripcion = MenuUtils.leerEntero("\nIngrese el id de la inscripción: ");
                            var nuevaInscripcion = inscripcionService.obtener(idInscripcion);

                            // Validar si el aula actual es adecuada para la nueva inscripción
                            var aulaActual = reserva.getAula();
                            int alumnos = nuevaInscripcion.getCantidadAlumnos() +
                                    (nuevaInscripcion.getFechaFinInscripcion().isAfter(LocalDate.now())
                                            ? nuevaInscripcion.getMargenAlumnos()
                                            : 0);

                            boolean aulaEsAdecuada = validarAulaParaInscripcion(aulaActual, nuevaInscripcion, alumnos);

                            if (!aulaEsAdecuada) {
                                System.out.println("\nEl aula actual no cumple con los requisitos de la nueva inscripción.");
                                System.out.println("Debe seleccionar un nuevo aula.");

                                // Solicitar un nuevo aula
                                var hasProyector = MenuUtils.obtenerProyectorEspacio();
                                var hasTv = MenuUtils.obtenerTvEspacio();

                                List<? extends Aula> espacios;
                                if (nuevaInscripcion.getAsignatura().isRequiereLaboratorio()) {
                                    int computadoras = MenuUtils.leerEntero("\nIngrese la cantidad de computadoras que requiere el laboratorio: ");
                                    espacios = aulaService.listarLaboratoriosDisponiblesConCondiciones(
                                            computadoras, alumnos, hasProyector, hasTv,
                                            reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getDiasYBloques());
                                } else {
                                    espacios = aulaService.listarAulasDisponiblesConCondiciones(
                                            alumnos, hasProyector, hasTv,
                                            reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getDiasYBloques());
                                }

                                // Mostrar espacios disponibles
                                if (espacios.isEmpty()) {
                                    System.out.println("\nNo hay espacios disponibles que cumplan con las condiciones.");
                                    break;
                                }
                                espacios.forEach(System.out::println);

                                // Seleccionar ua nueva aula
                                Aula aula = null;
                                while (aula == null) {
                                    var idAula = MenuUtils.leerEntero("\nIngrese el id del espacio: ");
                                    aula = espacios.stream()
                                            .filter(a -> a.getId() == idAula)
                                            .findFirst()
                                            .orElse(null);

                                    if (aula == null) {
                                        System.out.println("\nError: El id ingresado no corresponde a un espacio disponible. Intente nuevamente.");
                                    }
                                }

                                reserva.setAula(aula);
                            }

                            // Finalmente, setear la nueva inscripción
                            reserva.setInscripcion(nuevaInscripcion);
                            break;

                        case 6:
                            // Guardar cambios
                            reservaService.modificar(reserva);
                            System.out.println("\nReserva se ha modificado exitosamente.");
                            continuar = false;
                            break;
                        default:
                            System.out.println("\nOpción no válida.");
                    }
                }
            } catch (BadRequestException | ConflictException | JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para modificar reservas.");
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
                var reservas = reservaService.listar();
                if (reservas.isEmpty()){
                    System.out.println("No hay reservas realizadas aun.");
                    return;
                }
                System.out.println("\nReservas:");
                System.out.println("============================");
                reservas.forEach(System.out::println);
                System.out.println("============================");

                int id = MenuUtils.leerEntero("\nIngrese el id de la reserva a eliminar: ");
                reservaService.eliminar(id);
                System.out.println("\nReserva eliminada exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para eliminar reservas.");
        }
    }


    /**
     * Método para el submenu solicitudes
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void menuSolicitudes(Usuario usuario) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====================================");
            System.out.println("     Menú Solicitudes");
            System.out.println("======================================");
            System.out.println("Elija una opción:");
            System.out.println("1.Listar");
            System.out.println("2.Revisar solicitudes pendientes");
            System.out.println("3.Salir.");

            int opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuListarSolicitudes(usuario);
                case 2 -> revisarSolicitudes(usuario);
                case 3 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
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
            System.out.println("\n====================================");
            System.out.println("     Listado Solicitudes");
            System.out.println("======================================");
            System.out.println("\nElija una opción de listado o filtro:");
            System.out.println("1. Listar todas las solicitudes");
            System.out.println("2. Listar todas las solicitudes pendientes");
            System.out.println("3. Listar todas las solicitudes aprobadas");
            System.out.println("4. Listar todas las solicitudes rechazadas");
            System.out.println("5. Listar todas las solicitudes x profesor");
            System.out.println("6. Salir");

            int opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> listarSolicitudes(usuario);
                case 2 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.PENDIENTE);
                case 3 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.APROBADA);
                case 4 -> listarSolicitudesXEstado(usuario,EstadoSolicitud.RECHAZADA);
                case 5 -> listarSolicitudesXProfesor(usuario);
                case 6 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
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
                var solicitudes = solicitudCambioAulaService.listar();
                if (solicitudes.isEmpty()){
                    System.out.println("No hay solicitudes realizadas aun.");
                    return;
                }
                System.out.println("\nSolicitudes:");
                System.out.println("===============================");
                solicitudes.forEach(System.out::println);
                System.out.println("===============================");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las solicitudes.");
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
                if (solicitudes.isEmpty()){
                    System.out.println(STR."No hay solicitudes \{estadoSolicitud.toString().toLowerCase()}s.");
                    return;
                }
                System.out.println("\nSolicitudes:");
                System.out.println("===============================");
                solicitudes.forEach(System.out::println);
                System.out.println("===============================");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las solicitudes.");
        }
    }

    /**
     * Método para listar todas las solicitudes por profesor
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void listarSolicitudesXProfesor(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.VER_SOLICITUDES_CAMBIO)) {
            try {
                var profesores = profesorService.listar();
                if (profesores.isEmpty()){
                    System.out.println("No hay profesores.");
                    return;
                }
                System.out.println("\nProfesores:");
                System.out.println("===============================");
                profesores.forEach(System.out::println);
                System.out.println("===============================");
                var idProfesor = MenuUtils.leerEntero("\nIngresa el id del profesor: ");

                var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstadoYProfesor(EstadoSolicitud.PENDIENTE,idProfesor);
                if (solicitudes.isEmpty()){
                    System.out.println(STR."El profesor con id:\{idProfesor} no tiene solicitudes pendientes.");
                    return;
                }
                System.out.println("\nSolicitudes:");
                System.out.println("===============================");
                solicitudes.forEach(System.out::println);
                System.out.println("===============================");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver las solicitudes.");
        }
    }

    /**
     * Método para aprobar o rechazar uns solicitud
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void revisarSolicitudes(Usuario usuario){
        if (seguridad.verificarPermiso(usuario,Permisos.GESTIONAR_CAMBIOS)){
            try {

                var solicitudes = solicitudCambioAulaService.listarSolicitudesPorEstado(EstadoSolicitud.PENDIENTE);
                if (solicitudes.isEmpty()){
                    System.out.println("No hay solicitudes pendientes.");
                    return;
                }
                System.out.println("\nSolicitudes:");
                System.out.println("===============================");
                solicitudes.forEach(System.out::println);
                System.out.println("===============================");

                var idSolicitud = MenuUtils.leerEntero("\nIngrese el id de la solicitud a revisar: ");

                var estado= MenuUtils.leerEntero("""
                        \n
                        Elija una opción:
                        1.Rechazar
                        2.Aprobar
                        -\s
                        """);

                var motivo = MenuUtils.leerTexto("\nIndica el motivo (Enter si no quieres indicarlo): ");

                switch (estado){
                    case 1:
                        solicitudCambioAulaService.rechazarSolicitud(idSolicitud,motivo);
                        System.out.println("\nSolicitud rechaza exitosamente.");
                        break;
                    case 2:
                        solicitudCambioAulaService.aprobarSolicitud(idSolicitud,motivo);
                        System.out.println("\nSolicitud aprobada exitosamente.");
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
            System.out.println("\n====================================");
            System.out.println("     Menú Usuarios");
            System.out.println("======================================");
            System.out.println("\nElija una opción:");
            System.out.println("1. Crear usuario");
            System.out.println("2. Listar usuarios");
            System.out.println("3. Modificar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Cambiar contraseña");
            System.out.println("6. Salir.");


            var opcion = MenuUtils.leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> crearUsuario(usuario);
                case 2 -> listarUsuarios(usuario);
                case 3 -> modificarUsuario(usuario);
                case 4 -> eliminarUsuario(usuario);
                case 5 -> cambiarPassword(usuario);
                case 6 -> salir = true;
                default -> System.out.println("\nOpción inválida.");
            }
        }
    }

    /**
     * Método para crear usuario del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void crearUsuario(Usuario usuario) {
        if (!seguridad.verificarPermiso(usuario, Permisos.CREAR_USUARIO)) {
            System.out.println("\nNo posees el permiso para crear un usuario.");
            return;
        }

        try{
            // Pedir nombre de usuario
            var username = MenuUtils.leerTexto("\nIngrese el nombre de usuario: ");

            var contrasenia = MenuUtils.leerTexto("\nIngrese la contraseña: ");

            var idRol = MenuUtils.leerEntero("""
                            \n
                            Ingrese el rol:
                            1.Admin
                            2.Profesor
                            -\s
                            """);

            System.out.println("\nProfesores:");
            System.out.println("========================");
            profesorService.listar().forEach(System.out::println);
            System.out.println("========================");
            var idProfesor = MenuUtils.leerEntero("\nIngresa el id del profesor que representa: ");
            usuarioService.guardar(new Usuario(null,username,contrasenia,new Rol(idRol),new Profesor(idProfesor)));
            System.out.println("\nUsuario creado exitosamente.");
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
                var usuarios = usuarioService.listar();
                if (usuarios.isEmpty()){
                    System.out.println("No hay usuarios");
                    return;
                }
                System.out.println("\nUsuarios:");
                System.out.println("========================");
                usuarios.forEach(System.out::println);
                System.out.println("========================");
            } catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para ver los usuarios.");
        }
    }

    /**
     * Método para modificar usuarios del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void modificarUsuario(Usuario usuario) {
        if (seguridad.verificarPermiso(usuario, Permisos.MODIFICAR_USUARIO)) {
            try {
                var usuarios = usuarioService.listar();
                if (usuarios.isEmpty()){
                    System.out.println("No hay usuarios");
                    return;
                }
                System.out.println("\nUsuarios:");
                System.out.println("============================");
                usuarios.forEach(System.out::println);
                System.out.println("============================");

                // Solicitar el número del usuario a modificar
                int id = MenuUtils.leerEntero("\nIngrese el id del usuario que desea modificar: ");

                // Obtener el usuario por ID
                var user= usuarioService.obtener(id);

                boolean continuar = true;
                while (continuar) {
                    // Mostrar información actual del usuario
                    System.out.println("\nInformación actual del usuario:");
                    System.out.println(STR."1. Nombre de usuario: \{user.getUsername()}");
                    System.out.println(STR."2. Contraseña: \{user.getPassword()}");
                    System.out.println(STR."3. Rol: \{user.getRol().getNombre()}");
                    System.out.println("4. Guardar cambios y salir");

                    var opcion = MenuUtils.leerEntero("\nSeleccione el atributo que desea modificar (1-4): ");

                    switch (opcion) {
                        case 1:
                            var username = MenuUtils.leerTexto("\nIngrese el nuevo nombre de usuario: ");
                            usuarioService.validarUsernameUnico(username);
                            user.setUsername(username);
                            break;
                        case 2:
                            // Pedir password
                            user.setPassword(MenuUtils.leerTexto("\nIngrese la nueva contraseña: "));
                            break;
                        case 3:
                            var opcionesValidas = Set.of(1, 2);
                            int rol;
                            do {
                                rol = MenuUtils.leerEntero("""
                                       \nIngresa el nuevo rol:
                                       1. Admin
                                       2. Profesor
                                       """);
                                if (!opcionesValidas.contains(rol)) {
                                    System.out.println("\nOpción inválida. Vuelve a intentarlo.");
                                }
                            } while (!opcionesValidas.contains(rol));

                            user.setRol(new Rol(rol));
                            break;
                        case 4:
                            // Guardar cambios
                            usuarioService.modificar(user);
                            System.out.println("\nEl usuario se ha modificado exitosamente.");
                            continuar = false;
                            break;
                        default:
                            System.out.println("\nOpción no válida.");
                    }
                }
            } catch (JsonNotFoundException | NotFoundException | BadRequestException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("\nNo posees el permiso para modificar reservas.");
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
                var usuarios = usuarioService.listar();
                if (usuarios.isEmpty()){
                    System.out.println("No hay usuarios");
                    return;
                }
                System.out.println("\nUsuarios:");
                System.out.println("============================");
                usuarios.forEach(System.out::println);
                System.out.println("============================");

                usuarioService.eliminar(MenuUtils.leerEntero("\nIngrese el id del usuario a eliminar: "));
                System.out.println("\nUsuario eliminado exitosamente");
            }catch (JsonNotFoundException | NotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para eliminar usuarios.");
        }
    }

    /**
     * Método para cambiar contraseña del submenu usuarios
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    private void cambiarPassword(Usuario usuario){
        if(seguridad.verificarPermiso(usuario, Permisos.CAMBIAR_PASSWORD)){
            try {
                var usuarioNuevo = MenuUtils.cambiarPassword(usuario);
                usuarioService.modificar(usuarioNuevo);
                System.out.println("\nContraseña modificada exitosamente.");
            } catch (NotFoundException | JsonNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("\nNo posees el permiso para cambiar tu contraseña.");
        }
    }

    /**
     * Método para validar el aula con la inscripción y sus condiciones
     * @param aula a validar
     * @param inscripcion a validar
     * @param alumnosRequeridos que tiene la inscripción
     * @return boolean true si cumple las condiciones, false si no las cumple
     */
    private boolean validarAulaParaInscripcion(Aula aula, Inscripcion inscripcion, int alumnosRequeridos) {
        if (aula.getCapacidad() < alumnosRequeridos) {
            return false;
        }

        if (inscripcion.getAsignatura().isRequiereLaboratorio()) {
            if (!(aula instanceof Laboratorio lab)) {
                return false;
            }
            return lab.getComputadoras() >= alumnosRequeridos;
        }

        return true;
    }
}