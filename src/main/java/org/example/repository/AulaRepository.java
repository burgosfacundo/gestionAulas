package org.example.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.config.GsonConfig;
import org.example.exception.JsonNotFoundException;
import org.example.model.Aula;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Repositorio de aula y laboratorios
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class AulaRepository implements JSONRepository<Integer, Aula> {
    private final String ruta = "./json/aulas.json";

    /**
     * Método para retornar la ruta al json
     * que se quiere utilizar en el método default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return this.ruta;
    }

    @Override
    public Gson getGson() {
        // Utilizamos Gson configurado con RuntimeTypeAdapterFactory para Aula y Laboratorio
        return GsonConfig.createGsonAulaLaboratorio();
    }

    @Override
    public void write(List<Aula> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            Gson gsonEspecializado = getGson();
            JsonArray jsonArray = new JsonArray();

            // Serializamos cada elemento explícitamente utilizando el TypeAdapter
            for (Aula aula : list) {
                JsonElement jsonElement = gsonEspecializado.toJsonTree(aula, Aula.class);
                jsonArray.add(jsonElement);
            }

            gsonEspecializado.toJson(jsonArray, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{getRuta()}");
        }
    }


    /**
     * Método que guarda una nueva aula o laboratorio en el JSON llamando al método write
     * @param aula la nueva aula o laboratorio que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(Aula aula) throws JsonNotFoundException {
        var aulas = getAll();

        // Verifíco el último id y genero el próximo
        int lastId = aulas.isEmpty() ? 0 : aulas.getLast().getId();
        aula.setId(lastId + 1);

        // Agregamos la nueva aula o laboratorio
        aulas.add(aula);

        // Guarda los cambios en el archivo JSON
        write(aulas);
    }

    /**
     * Método que devuelve la lista de aulas y laboratorios que tenemos en el JSON
     * @return List<Aula> lista de aulas y laboratorios en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Aula> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            // Gson deserializará automáticamente a Aula o Laboratorio usando RuntimeTypeAdapterFactory
            var listType = new TypeToken<List<Aula>>() {}.getType();
            return getGson().fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }

    /**
     * Método para buscar un aula o laboratorio por ID
     * @param id para buscar y devolver el aula o laboratorio
     * @return Optional<Aula> el aula o laboratorio si lo encuentra, o Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<Aula> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve el aula si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(aula -> Objects.equals(aula.getId(), id))
                .findFirst();
    }

    /**
     * Método para borrar un aula o laboratorio por ID
     * @param id para buscar y borrar el aula o laboratorio
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        var aulas = getAll();

        // Borra el aula o laboratorio con el ID dado
        aulas.removeIf(aula -> Objects.equals(aula.getId(), id));

        // Guarda los cambios en el archivo JSON
        write(aulas);
    }

    /**
     * Método para modificar un aula o laboratorio
     * @param aula que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(Aula aula) throws JsonNotFoundException {
        // Obtén todas las aulas del JSON
        var aulas = getAll();

        aulas.replaceAll(a -> {
            if (Objects.equals(a.getId(), aula.getId())) {
                a.actualizar(aula);
            }
            return a;
        });

        // Guarda los cambios en el archivo JSON
        write(aulas);
    }

    /**
     * Método para buscar un aula o laboratorio por número
     * @param numero para buscar y devolver el aula o laboratorio
     * @return Optional<Aula> el aula o laboratorio si lo encuentra, o Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<Aula> findByNumero(Integer numero) throws JsonNotFoundException {
        //Usamos stream para filtrar por número
        //Devuelve el aula si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(aula -> aula.getNumero() == numero)
                .findFirst();
    }

}

