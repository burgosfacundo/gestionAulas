package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Profesor;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Repositorio de profesor
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class ProfesorRepository implements JSONRepository<Integer, Profesor>{
    private final String ruta = "./json/profesores.json";

    /**
     * Método para retornar la ruta al json
     * que se quiere utilizar en el método default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return ruta;
    }

    /**
     * Método que guarda un nuevo profesor en el JSON llamando al método write
     * @param profesor el nuevo profesor que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(Profesor profesor) throws JsonNotFoundException {
        //Traigo lo que tengo en el json
        var profesores = getAll();

        //Verífico el último id y genero el próximo
        var lastId = profesores.isEmpty() ? 0 : profesores.getLast().getId();
        profesor.setId(lastId + 1);

        // Agregamos el nuevo profesor
        profesores.add(profesor);

        // Convertir cada profesor a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        profesores.forEach(p -> jsonArray.add(JsonParser.parseString(p.toJson())));
        write(jsonArray);
    }


    /**
     * Método que devuelve la lista de profesores que tenemos en el JSON
     * @return List<Profesor> lista de profesores en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Profesor> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Indico que el tipo va a ser una Lista de profesores
            var rolListType = new TypeToken<List<Profesor>>() {}.getType();
            return gson.fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar un profesor por ID
     * @param id para buscar y devolver el profesor
     * @return Profesor con el ID del parámetro
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Profesor findById(Integer id) throws JsonNotFoundException {
        //Uso stream para filtrar por id si no existe lanzo excepción
        return getAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Método para borrar un profesor por ID
     * @param id para buscar y borrar el profesor
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException{
        //Traigo lo que tiene el json
        var profesores = getAll();

        //Borro al que tenga el id
        profesores.removeIf(p -> p.getId() == id);

        // Convertir cada profesor a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        profesores.forEach(p -> jsonArray.add(JsonParser.parseString(p.toJson())));
        write(jsonArray);
    }

    /**
     * Método para buscar profesor por matrícula
     * @param matricula para buscar el profesor
     * @return Profesor con esa matrícula
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Profesor findByMatricula(String matricula) throws JsonNotFoundException {
        return getAll().stream()
                .filter(p -> p.getMatricula().equals(matricula))
                .findFirst()
                .orElse(null);
    }
}