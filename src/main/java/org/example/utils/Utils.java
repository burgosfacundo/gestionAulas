package org.example.utils;

import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import java.util.*;


public class Utils {

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
}

