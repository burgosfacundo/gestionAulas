package org.example.utils;

import org.example.enums.BloqueHorario;
import org.example.model.Usuario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import java.util.*;


public class Utils {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Método para obtener los nombres de los días de la semana en español.
     * @param diasSemana Set de DayOfWeek con los días de la semana.
     * @return String con los nombres de los días separados por coma.
     */
    public static String obtenerDiasEnEspaniol(Set<DayOfWeek> diasSemana) {
        return diasSemana.stream()
                .map(day -> day.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")))
                .collect(Collectors.joining(", "));
    }

    //Métodos para formatear los toString
    /**
     * Método para añadir indentación a cada línea de un texto.
     * @param text Texto al que se le aplicará la indentación.
     * @param indentSpaces Cantidad de espacios para la indentación.
     * @return String con el texto indentado.
     */
    public static String indentString(String text, int indentSpaces) {
        var indent = " ".repeat(indentSpaces);
        return Arrays.stream(text.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Método para formatear un mapa de días de la semana y bloques horarios.
     * @param diasYBloques Mapa de DayOfWeek a Set de BloqueHorario.
     * @return String con el formato de días y bloques horarios.
     */
    public static String formatDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
        var sb = new StringBuilder();
        diasYBloques.forEach((dia, bloques) -> {
            // Usamos el método obtenerDiasEnEspaniol para obtener el nombre del día en español
            var diaEnEspaniol = obtenerDiasEnEspaniol(Set.of(dia));
            var bloquesFormateados = bloques.stream()
                    .map(BloqueHorario::name)
                    .collect(Collectors.joining(", "));

            sb.append(String.format("   - %s: %s\n", diaEnEspaniol, bloquesFormateados));
        });
        return sb.toString().trim(); // Eliminar último salto de línea
    }

    /**
     * Método para formatear listas de elementos a texto.
     * Convierte cada elemento en una cadena y lo lista con un prefijo "-".
     * @param items Lista de elementos a formatear.
     * @param <T> Tipo genérico de los elementos.
     * @return String con cada elemento en una nueva línea con un prefijo "-".
     */
    public static <T> String formatListItems(List<T> items) {
        return items.stream()
                .map(item -> STR."   - \{item.toString()}") // Convierte cada elemento a String
                .collect(Collectors.joining("\n"));
    }


    // Métodos para inputs
    /**
     * Método para leer un número entero, manejando la excepción si el usuario ingresa algo incorrecto.
     * @param mensaje a mostrar al usuario.
     * @return El número entero ingresado por el usuario.
     */
    public static int leerEntero(String mensaje) {
        int numero;
        while (true) {
            System.out.print(STR."\{mensaje}");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("\nError: No se ingresó ningún valor. Intente de nuevo.");
                continue;  // Salta al siguiente ciclo del bucle
            }
            try {
                numero = Integer.parseInt(input); // Usamos parseInt en lugar de nextInt para manejar el salto de línea
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("\nError: Debe ingresar un número entero.");
            }
        }
    }


    /**
     * Método para leer una fecha, manejando el formato incorrecto.
     * @param mensaje a mostrar al usuario.
     * @return LocalDate ingresada por el usuario.
     */
    public static LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(STR."\{mensaje} (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("\nError: No se ingresó ninguna fecha. Intente de nuevo.");
                continue;  // Salta al siguiente ciclo del bucle
            }
            try {
                return LocalDate.parse(input); // Parseando directamente
            } catch (DateTimeParseException e) {
                System.out.println("\nError: Formato de fecha incorrecto. Use el formato YYYY-MM-DD.");
            }
        }
    }

    /**
     * Método para obtener un rango de fechas válidas (inicio y fin).
     * Se asegura de que la fecha de inicio sea anterior o igual a la de fin,
     * y que ambas sean iguales o posteriores a la fecha actual.
     *
     * @param mensajeInicio Mensaje a mostrar al pedir la fecha de inicio.
     * @param mensajeFin Mensaje a mostrar al pedir la fecha de fin.
     * @return List<LocalDate> lista de fechas válidas (inicio y fin).
     */
    public static List<LocalDate> leerRangoDeFechas(String mensajeInicio, String mensajeFin) {
        LocalDate fechaInicio;
        LocalDate fechaFin;

        while (true) {
            // Pedir fecha de inicio
            fechaInicio = leerFecha(mensajeInicio);

            // Pedir fecha de fin
            fechaFin = leerFecha(mensajeFin);

            // Validar fechas
            if (!fechaInicio.isAfter(fechaFin) && !fechaInicio.isBefore(LocalDate.now())) {
                return List.of(fechaInicio, fechaFin);
            } else if (fechaInicio.isBefore(LocalDate.now())) {
                System.out.println("\nError: Las fechas no pueden ser anteriores a hoy.");
            } else {
                System.out.println("\nError: La fecha de inicio debe ser anterior o igual a la fecha de fin.");
            }
        }
    }



    /**
     * Método para leer una cadena de texto.
     * @param mensaje a mostrar al usuario.
     * @return String ingresada por el usuario.
     */
    public static String leerTexto(String mensaje) {
        System.out.print(STR."\{mensaje}");
        return scanner.nextLine().trim();
    }

    /**
     * Método para leer una respuesta de tipo sí/no ("s"/"n"), con validación de entrada.
     * @param mensaje El mensaje a mostrar al usuario
     * @return true si el usuario ingresa 's' (sí), false si ingresa 'n' (no)
     */
    public static boolean leerConfirmacion(String mensaje) {
        while (true) {
            System.out.print(STR."\{mensaje} (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            // Validación de respuesta
            if (respuesta.equals("s")) {
                return true;
            } else if (respuesta.equals("n")) {
                return false;
            } else {
                System.out.println("\nRespuesta inválida. Debe ingresar 's' para sí o 'n' para no.");
            }
        }
    }


    /**
     * Método para pedir los días seleccionados y bloques horarios.
     * @return Map<DayOfWeek, Set<BloqueHorario>> que contiene los días seleccionados y los bloques horarios
     */
    public static Map<DayOfWeek, Set<BloqueHorario>> leerDiasYBloques() {
        Map<DayOfWeek, Set<BloqueHorario>> diasYBloques = new HashMap<>();
        Set<DayOfWeek> diasSeleccionados = new HashSet<>();

        // Seleccionar días de la semana
        boolean diasValidos = false;
        while (!diasValidos) {
            System.out.println("\nIngrese los días de la semana (separados por comas): ");
            System.out.println("1. Lunes");
            System.out.println("2. Martes");
            System.out.println("3. Miércoles");
            System.out.println("4. Jueves");
            System.out.println("5. Viernes");
            System.out.println("6. Sábado");
            System.out.println("7. Domingo");

            String diasInput = scanner.nextLine().trim();
            if (diasInput.isEmpty()) {
                System.out.println("\nError: No se ingresaron días. Intente de nuevo.");
                continue;  // Salta al siguiente ciclo del bucle
            }

            List<String> diasArray = Arrays.stream(diasInput.split(",")).map(String::trim).toList();

            diasSeleccionados.clear(); // Limpiar la lista antes de validar nuevamente

            // Validar los días ingresados
            for (String diaStr : diasArray) {
                try {
                    int dia = Integer.parseInt(diaStr) - 1;
                    if (dia >= 0 && dia < 7) {
                        diasSeleccionados.add(DayOfWeek.of(dia + 1));
                    } else {
                        throw new IllegalArgumentException("\nDía fuera de rango");
                    }
                } catch (Exception e) {
                    System.out.println("\nError: Ingrese números entre 1 y 7 para los días.");
                    break;
                }
            }

            if (!diasSeleccionados.isEmpty()) {
                diasValidos = true;
            } else {
                System.out.println("\nNo se seleccionaron días válidos. Intente de nuevo.");
            }
        }

        // Seleccionar bloques horarios para cada día
        for (DayOfWeek dia : diasSeleccionados) {
            var bloques = leerBloques(dia);
            diasYBloques.put(dia, bloques);
        }

        return diasYBloques;
    }

    /**
     * Método para pedir los bloques horarios.
     * @return Set<BloqueHorario> que contiene los bloques horarios
     */
    public static Set<BloqueHorario> leerBloques(DayOfWeek dia) {
        Set<BloqueHorario> bloques = new HashSet<>();
            boolean bloquesValidos = false;
            while (!bloquesValidos) {
                System.out.println(STR."\nSeleccione los bloques horarios para \{Utils.obtenerDiasEnEspaniol(Set.of(dia))} (separados por comas): ");
                int i = 1;
                for (BloqueHorario bloque : BloqueHorario.values()) {
                    System.out.println(STR."\{i}. \{bloque}");
                    i++;
                }

                String bloquesStr = scanner.nextLine().trim();
                if (bloquesStr.isEmpty()) {
                    System.out.println("\nError: Debe seleccionar al menos un bloque horario.");
                    continue;  // Salta al siguiente ciclo del bucle
                }

                List<String> bloquesArray = Arrays.stream(bloquesStr.split(",")).map(String::trim).toList();

                try {
                    for (String bloqueIndex : bloquesArray) {
                        int opcionBloque = Integer.parseInt(bloqueIndex) - 1;
                        if (opcionBloque >= 0 && opcionBloque < BloqueHorario.values().length) {
                            bloques.add(BloqueHorario.values()[opcionBloque]);
                        } else {
                            throw new IllegalArgumentException("\nBloque fuera de rango");
                        }
                    }
                    if (!bloques.isEmpty()) {
                        bloquesValidos = true;
                    } else {
                        System.out.println("\nDebe seleccionar al menos un bloque horario.");
                    }
                } catch (Exception e) {
                    System.out.println("\nError: Debe ingresar números válidos para los bloques.");
                }
            }
        return bloques;
    }

    /**
     * Método para preguntarle al usuario la capacidad de un espacio
     * @return la cantidad del espacio o null si no quiere aplicar el filtro
     */
    public static Integer obtenerCapacidadEspacio(){
        // Solicitar la capacidad
        var capacidadInput = Utils.leerEntero("\nIngrese la capacidad (-1 si no quiere aplicar este filtro): ");
        return capacidadInput == -1 ? null : capacidadInput;
    }

    /**
     * Método para preguntarle al usuario si un espacio tiene proyector
     * @return si tiene o no proyector o null si no quiere aplicar el filtro
     */
    public static Boolean obtenerProyectorEspacio(){
        while (true) {
            // Solicitar si tiene proyector
            String proyectorInput = leerTexto("\n¿Debe tener proyector el espacio? (s/n) (Enter si no quiere aplicar el filtro): ").toLowerCase();

            if (proyectorInput.isEmpty()) {
                return null;  // Si presiona Enter sin ingresar nada, no aplicar el filtro
            }

            if (proyectorInput.equals("s")) {
                return true;  // Si responde 's', tiene proyector
            } else if (proyectorInput.equals("n")) {
                return false;  // Si responde 'n', no tiene proyector
            }
            System.out.println("\nRespuesta inválida. Debe ingresar 's' para sí o 'n' para no.");
        }

    }
    /**
     * Método para preguntarle al usuario si un espacio tiene TV
     * @return si tiene o no tv o null si no quiere aplicar el filtro
     */
    public static Boolean obtenerTvEspacio(){
        while (true) {
            // Solicitar si tiene TV
            String tvInput = leerTexto("\n¿Debe tener TV el espacio? (s/n) (Enter si no quiere aplicar el filtro): ").toLowerCase();

            if (tvInput.isEmpty()) {
                return null;  // Si presiona Enter sin ingresar nada, no aplicar el filtro
            }

            if (tvInput.equals("s")) {
                return true;  // Si responde 's', tiene TV
            } else if (tvInput.equals("n")) {
                return false;  // Si responde 'n', no tiene TV
            }

            System.out.println("\nRespuesta inválida. Debe ingresar 's' para sí o 'n' para no.");
        }
    }

    /**
     * Método para obtener la cantidad de computadoras desde la consola
     * @return Integer de cantidad de computadoras o null si no quiere aplicar el filtro
     */
    public static Integer obtenerCantidadComputadoras(){
        // Solicitar las computadoras
        var computadorasInput = Utils.leerEntero("\nIngrese la cantidad de computadoras (-1 si no quiere aplicar este filtro): ");
        return computadorasInput == -1 ? null : computadorasInput;
    }

    /**
     * Método para cambiar contraseña
     * @param usuario que esta logueado para verificar perfil con permisos
     */
    public static Usuario cambiarPassword(Usuario usuario){
        boolean passwordsCoinciden = false;
        while (!passwordsCoinciden) {
            var password = Utils.leerTexto("\nIngresa la nueva contraseña: ");
            var validaPassword = Utils.leerTexto("\nIngresa nuevamente la contraseña: ");

            if (password.equals(validaPassword)) {
                usuario.setPassword(password);
                passwordsCoinciden = true;
            } else {
                System.out.println("\nLas contraseñas no coinciden. Intenta nuevamente.");
            }
        }
        return usuario;
    }

    /**
     * Método para cerrar el Scanner
     */
    public static void cerrarScanner() {
        scanner.close();
    }
}

