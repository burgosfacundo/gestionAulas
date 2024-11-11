package org.example.utils;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils {
    public static String obtenerDiasEnEspaniol(Set<DayOfWeek> diasSemana) {
        return diasSemana.stream()
                .map(day -> day.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")))
                .collect(Collectors.joining(", "));
    }
}
