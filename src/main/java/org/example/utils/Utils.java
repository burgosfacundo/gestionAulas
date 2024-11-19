package org.example.utils;

import org.example.enums.BloqueHorario;

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
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: No se ingresó ningún valor. Intente de nuevo.");
                continue;  // Salta al siguiente ciclo del bucle
            }
            try {
                numero = Integer.parseInt(input); // Usamos parseInt en lugar de nextInt para manejar el salto de línea
                return numero;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero.");
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
                System.out.println("Error: No se ingresó ninguna fecha. Intente de nuevo.");
                continue;  // Salta al siguiente ciclo del bucle
            }
            try {
                return LocalDate.parse(input); // Parseando directamente
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha incorrecto. Use el formato YYYY-MM-DD.");
            }
        }
    }


    /**
     * Método para leer una cadena de texto.
     * @param mensaje a mostrar al usuario.
     * @return String ingresada por el usuario.
     */
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
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
            if (respuesta.equals("s")) {
                return true;
            }
            if (respuesta.equals("n")) {
                return false;
            }
            System.out.println("Respuesta inválida. Debe ingresar 's' para sí o 'n' para no.");
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
            System.out.println("Ingrese los días de la semana (separados por comas): ");
            System.out.println("1. Lunes");
            System.out.println("2. Martes");
            System.out.println("3. Miércoles");
            System.out.println("4. Jueves");
            System.out.println("5. Viernes");
            System.out.println("6. Sábado");
            System.out.println("7. Domingo");

            String diasInput = scanner.nextLine().trim();
            if (diasInput.isEmpty()) {
                System.out.println("Error: No se ingresaron días. Intente de nuevo.");
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
                        throw new IllegalArgumentException("Día fuera de rango");
                    }
                } catch (Exception e) {
                    System.out.println("Error: Ingrese números entre 1 y 7 para los días.");
                    break;
                }
            }

            if (!diasSeleccionados.isEmpty()) {
                diasValidos = true;
            } else {
                System.out.println("No se seleccionaron días válidos. Intente de nuevo.");
            }
        }

        // Seleccionar bloques horarios para cada día
        for (DayOfWeek dia : diasSeleccionados) {
            Set<BloqueHorario> bloques = new HashSet<>();
            boolean bloquesValidos = false;

            while (!bloquesValidos) {
                System.out.println(STR."Seleccione los bloques horarios para \{dia} (separados por comas): ");
                int i = 1;
                for (BloqueHorario bloque : BloqueHorario.values()) {
                    System.out.println(STR."\{i}. \{bloque}");
                    i++;
                }

                String bloquesStr = scanner.nextLine().trim();
                if (bloquesStr.isEmpty()) {
                    System.out.println("Error: Debe seleccionar al menos un bloque horario.");
                    continue;  // Salta al siguiente ciclo del bucle
                }

                List<String> bloquesArray = Arrays.stream(bloquesStr.split(",")).map(String::trim).toList();

                try {
                    for (String bloqueIndex : bloquesArray) {
                        int opcionBloque = Integer.parseInt(bloqueIndex) - 1;
                        if (opcionBloque >= 0 && opcionBloque < BloqueHorario.values().length) {
                            bloques.add(BloqueHorario.values()[opcionBloque]);
                        } else {
                            throw new IllegalArgumentException("Bloque fuera de rango");
                        }
                    }
                    if (!bloques.isEmpty()) {
                        bloquesValidos = true;
                    } else {
                        System.out.println("Debe seleccionar al menos un bloque horario.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: Debe ingresar números válidos para los bloques.");
                }
            }

            diasYBloques.put(dia, bloques);
        }

        return diasYBloques;
    }

    /**
     * Método para preguntarle al usuario la capacidad de un espacio
     * @return la cantidad del espacio o null si no quiere aplicar el filtro
     */
    public static Integer obtenerCapacidadEspacio(){
        // Solicitar la capacidad
        var capacidadInput = Utils.leerEntero("Ingrese la capacidad (-1 si no quiere aplicar este filtro): ");
        return capacidadInput == -1 ? null : capacidadInput;
    }

    /**
     * Método para preguntarle al usuario si un espacio tiene proyector
     * @return si tiene o no proyector o null si no quiere aplicar el filtro
     */
    public static Boolean obtenerProyectoEspacio(){
        // Solicitar si tiene proyector
        var proyectorInput = Utils
                .leerTexto("¿Debe tener proyector? (Si/No) (Enter si no quiere aplicar el filtro): ")
                .toLowerCase();

        return proyectorInput.isEmpty() ? null : proyectorInput.equalsIgnoreCase("si");

    }
    /**
     * Método para preguntarle al usuario si un espacio tiene TV
     * @return si tiene o no tv o null si no quiere aplicar el filtro
     */
    public static Boolean obtenerTvEspacio(){
        // Solicitar si tiene TV
        var tvInput = Utils
                .leerTexto("¿Debe tener TV? (Si/No) (Enter si no quiere aplicar el filtro):")
                .toLowerCase();
        return tvInput.isEmpty() ? null : tvInput.equalsIgnoreCase("si");

    }

    /**
     * Método para obtener la cantidad de computadoras desde la consola
     * @return Integer de cantidad de computadoras o null si no quiere aplicar el filtro
     */
    public static Integer obtenerCantidadComputadoras(){
        // Solicitar las computadoras
        var computadorasInput = Utils.leerEntero("Ingrese la cantidad de computadoras (-1 si no quiere aplicar este filtro): ");
        return computadorasInput == -1 ? null : computadorasInput;
    }

    /**
     * Método para cerrar el Scanner
     */
    public static void cerrarScanner() {
        scanner.close();
    }
}

