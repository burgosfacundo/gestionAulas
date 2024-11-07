package org.example.adaptadores;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que permite registrar subtipos de una clase base en Gson para serialización y deserialización polimórfica.
  */

public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<T> baseType;  // Clase base para el polimorfismo
    private final String typeFieldName;  // Nombre del campo que identificará el tipo en el JSON
    private final Map<String, Class<? extends T>> labelToSubtype = new HashMap<>();  // Mapa de etiquetas a subtipos

    // Constructor privado que inicializa el tipo base y el nombre del campo de tipo
    private RuntimeTypeAdapterFactory(Class<T> baseType, String typeFieldName) {
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    // Método estático de fábrica para crear una instancia de RuntimeTypeAdapterFactory
    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    // Método para registrar un subtipo con su etiqueta. Permite añadir clases que extienden el tipo base.
    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> subtype, String label) {
        labelToSubtype.put(label, subtype);
        return this;
    }

    // Método de Gson para crear el adaptador de tipos
    @Override
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        // Verifica que el tipo solicitado sea asignable desde la clase base
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }

        // Mapa inverso para obtener la etiqueta a partir del tipo de clase
        final Map<Class<? extends T>, String> subtypeToLabel = new HashMap<>();
        for (Map.Entry<String, Class<? extends T>> entry : labelToSubtype.entrySet()) {
            subtypeToLabel.put(entry.getValue(), entry.getKey());
        }

        // Retorna un nuevo TypeAdapter anónimo para manejar la serialización y deserialización
        return new TypeAdapter<R>() {
            // Serializa el objeto a JSON, añadiendo el campo de tipo
            @Override
            public void write(JsonWriter out, R value) {
                var srcType = value.getClass();
                var label = subtypeToLabel.get(srcType);  // Obtiene la etiqueta del tipo
                if (label == null) {
                    throw new JsonParseException(STR."No se puede deserealizar \{srcType.getName()}");
                }

                // Convierte el objeto a JsonObject y añade el campo de tipo
                var jsonObject = gson.toJsonTree(value).getAsJsonObject();
                jsonObject.addProperty(typeFieldName, label);  // Añade el tipo como propiedad
                gson.toJson(jsonObject, out);  // Escribe el JsonObject
            }


            // Deserializa el JSON a un objeto del subtipo adecuado
            @SuppressWarnings("unchecked")
            @Override
            public R read(JsonReader in) {
                var jsonObject = JsonParser.parseReader(in).getAsJsonObject();
                var labelJsonElement = jsonObject.remove(typeFieldName);  // Elimina el campo de tipo del JSON
                if (labelJsonElement == null) {
                    throw new JsonParseException(STR."No se puede deserealizar \{baseType.getName()} porque no tiene el atributo \{typeFieldName}");
                }

                // Busca el subtipo correspondiente en el mapa usando la etiqueta obtenida
                var label = labelJsonElement.getAsString();
                var subtype = labelToSubtype.get(label);
                if (subtype == null) {
                    throw new JsonParseException(STR."No se puede deserealizar el subtipo de \{baseType.getName()} llamado \{label}");
                }


                // Convierte el JsonObject al subtipo registrado
                return (R) gson.fromJson(jsonObject, subtype);
            }
        }.nullSafe();  // Asegura que el adaptador trate con valores nulos sin fallar
    }
}

