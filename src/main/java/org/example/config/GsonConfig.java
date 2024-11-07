package org.example.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.adaptadores.RuntimeTypeAdapterFactory;
import org.example.model.Aula;
import org.example.model.Laboratorio;


/**
 * Clase para configurar GSON
 */
public class GsonConfig {
    /**
     * Método para crear un GSON que registre subtipo Laboratorio de Aula de manera polimórfica
     * usando el RunTypeAdapterFactory
     * @return Gson configurado para Aula y Laboratorio
     */
    public static Gson createGsonAulaLaboratorio() {
        RuntimeTypeAdapterFactory<Aula> typeFactory = RuntimeTypeAdapterFactory
                .of(Aula.class, "type")
                .registerSubtype(Aula.class, "Aula")
                .registerSubtype(Laboratorio.class, "Laboratorio");

        return new GsonBuilder()
                .registerTypeAdapterFactory(typeFactory)
                .setPrettyPrinting()
                .create();
    }
}


