package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Asignatura;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Repositorio de asignatura
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class AsignaturaRepository implements JSONRepository<Integer,Asignatura> {
    private final String ruta = "asignaturas.json";

    /**
     * Metodo para retornar la ruta al json
     * que se quiere utilizar en el metodo default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return this.ruta;
    }

    /**
     * Metodo que guarda una nueva asignatura en el JSON llamando al metodo write
     * @param asignatura la nueva asignatura que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(Asignatura asignatura) throws JsonNotFoundException {
        //Traigo lo que tengo en el json
        var asignaturas = getAll();

        //Verifico el ultimo id y genero el proximo
        var lastId = asignaturas.isEmpty() ? 0 : asignaturas.getLast().getId();
        asignatura.setId(lastId + 1);

        // Agregamos la nueva asignatura
        asignaturas.add(asignatura);

        // Convertir cada asignatura a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        asignaturas.forEach(p -> jsonArray.add(JsonParser.parseString(p.toJson())));
        write(jsonArray);
    }


    /**
     * Metodo que devuelve la lista de asignaturas que tenemos en el JSON
     * @return List<Asignatura> lista de asignaturas en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Asignatura> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Indico que el tipo va a ser una Lista de asignaturas
            var rolListType = new TypeToken<List<Asignatura>>() {}.getType();
            return gson.fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Metodo para buscar una asignatura por id
     * @param id para buscar y devolver la asignatura
     * @return Asignatura con el id del parametro
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Asignatura findById(Integer id) throws JsonNotFoundException {
        //Uso stream para filtrar por id si no existe lanzo excepcion
        return getAll().stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Metodo para borrar una asignatura por id
     * @param id para buscar y borrar el asignatura
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException{
        //Traigo lo que tiene el json
        var asignaturas = getAll();

        //Borro al que tenga el id
        asignaturas.removeIf(a -> a.getId() == id);

        // Convertir cada asignatura a JsonObject usando el método de toJson personalizado
        var jsonArray = new JsonArray();
        asignaturas.forEach(a -> jsonArray.add(JsonParser.parseString(a.toJson())));
        write(jsonArray);
    }
}