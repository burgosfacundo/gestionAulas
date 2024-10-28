package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Aula;


import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * Repositorio de aula
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class AulaRepository implements JSONRepository<Integer,Aula> {
    private final String ruta = "aulas.json";

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
     * Metodo que guarda una nueva aula en el JSON llamando al metodo write
     * @param aula la nueva aula que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(Aula aula) throws JsonNotFoundException {
        //Traigo lo que tengo en el json
        var aulas = getAll();

        //Verifico el ultimo id y genero el proximo
        var lastId = aulas.isEmpty() ? 0 : aulas.getLast().getId();
        aula.setId(lastId + 1);

        // Agregamos la nueva aula
        aulas.add(aula);

        // Convertir cada aula a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        aulas.forEach(a -> jsonArray.add(JsonParser.parseString(a.toJson())));
        write(jsonArray);
    }


    /**
     * Metodo que devuelve la lista de aulas que tenemos en el JSON
     * @return List<Aula> lista de aulas en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Aula> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Indico que el tipo va a ser una Lista de aulas
            var rolListType = new TypeToken<List<Aula>>() {}.getType();
            return gson.fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Metodo para buscar un aula por id
     * @param id para buscar y devolver la aula
     * @return Aula con el id del parametro
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Aula findById(Integer id) throws JsonNotFoundException {
        //Uso stream para filtrar por id si no existe lanzo excepcion
        return getAll().stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Metodo para borrar un aula por id
     * @param id para buscar y borrar la aula
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException{
        //Traigo lo que tiene el json
        var aulas = getAll();

        //Borro al que tenga el id
        aulas.removeIf(a -> a.getId() == id);

        // Convertir cada aula a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        aulas.forEach(a -> jsonArray.add(JsonParser.parseString(a.toJson())));
        write(jsonArray);
    }
}
