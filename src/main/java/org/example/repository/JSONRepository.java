package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.example.adaptadores.AdaptadorLocalDate;
import org.example.exception.JsonNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz genérica para comunicarse con el JSON
 * @param <ID> Recibe el tipo del ID de T
 * @param <T> Recibe el tipo de Clase que se guarda en el JSON
 */
public interface JSONRepository <ID,T>{
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate()).create();

    /**
     * Método para obtener la ruta del archivo JSON.
     * @return la ruta del archivo JSON.
     */
    String getRuta();

    /**
     * Método default que escribe en un JSON
     * @param list JsonArray que se va a write en el archivo JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    default void write(JsonArray list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            gson.toJson(list, writer);
            writer.flush();
        }catch (IOException e){
            throw new JsonNotFoundException(STR."No se encontro el archivo json \{getRuta()}");
        }
    }
    void save(T t) throws JsonNotFoundException;
    List<T> getAll() throws JsonNotFoundException;
    T findById(ID id) throws JsonNotFoundException;
    void deleteById(ID id) throws JsonNotFoundException;
}
