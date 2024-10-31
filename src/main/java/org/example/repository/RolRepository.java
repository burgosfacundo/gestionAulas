package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Rol;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * Repositorio de rol
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class RolRepository implements JSONRepository<Integer, Rol>{
    private final String ruta = "./json/roles.json";

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
     * Método que guarda un nuevo rol en el JSON llamando al método write
     * @param rol el nuevo rol que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(Rol rol) throws JsonNotFoundException {
        //Traigo lo que tengo en el json
        var roles = getAll();

        //Verifico el ultimo id y genero el proximo
        var lastId = roles.isEmpty() ? 0 : roles.getLast().getId();
        rol.setId(lastId + 1);

        // Agregamos el nuevo rol
        roles.add(rol);

        // Convertir cada rol a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        roles.forEach(r -> jsonArray.add(JsonParser.parseString(r.toJson())));
        write(jsonArray);
    }


    /**
     * Método que devuelve la lista de roles que tenemos en el JSON
     * @return List<Rol> lista de roles en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Rol> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Indico que el tipo va a ser una Lista de Roles
            var rolListType = new TypeToken<List<Rol>>() {}.getType();
            return gson.fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar un rol por id
     * @param id para buscar y devolver el rol
     * @return Rol con el ID del parámetro
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Rol findById(Integer id) throws JsonNotFoundException {
        //Uso stream para filtrar por id si no existe lanzo excepción
        return getAll().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Método para borrar un rol por ID
     * @param id para buscar y borrar el rol
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException{
        //Traigo lo que tiene el json
        var roles = getAll();

        //Borro al que tenga el id
        roles.removeIf(r -> r.getId() == id);

        // Convertir cada rol a JsonObject usando el método toJson personalizado
        var jsonArray = new JsonArray();
        roles.forEach(r -> jsonArray.add(JsonParser.parseString(r.toJson())));
        write(jsonArray);
    }

    /**
     * Método para buscar rol por nombre
     * @param nombre para buscar el rol
     * @return Rol con ese nombre
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Rol findByNombre(String nombre) throws JsonNotFoundException {
        return getAll().stream()
                .filter(r -> r.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }
}