package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Rol;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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
     * Método para obtener el gson que utiliza este repositorio
     * @return Gson que se va a utilizar
     */
    @Override
    public Gson getGson() {
        return new Gson();
    }

    /**
     * Método para escribir un archivo JSON
     * @param list List<Rol> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<Rol> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{getRuta()}");
        }
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

        //Verifíco el último id y genero el próximo
        var lastId = roles.isEmpty() ? 0 : roles.getLast().getId();
        rol.setId(lastId + 1);

        // Agregamos el nuevo rol
        roles.add(rol);

        // Guarda los cambios en el archivo JSON
        write(roles);
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
            return getGson().fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar un rol por id
     * @param id para buscar y devolver el rol
     * @return Optional<Rol> el rol si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<Rol> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por ID
        //Devuelve el rol si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst();
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
        roles.removeIf(r -> Objects.equals(r.getId(), id));

        // Guarda los cambios en el archivo JSON
        write(roles);
    }

    /**
     * Método para modificar el rol
     * @param rol que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(Rol rol) throws JsonNotFoundException {
        // Obtén todos los roles del JSON
        var roles = getAll();

        // Busca el rol por ID y actualiza sus campos
        var exist = roles.stream()
                .filter(r -> Objects.equals(r.getId(), rol.getId()))
                .findFirst()
                .orElseThrow(() -> new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}"));

        // Actualiza los atributos del rol existente con los del nuevo objeto
        exist.setId(rol.getId());
        exist.setNombre(rol.getNombre());
        exist.setPermisos(rol.getPermisos());

        // Guarda los cambios en el archivo JSON
        write(roles);
    }

    /**
     * Método para buscar rol por nombre
     * @param nombre para buscar el rol
     * @return Optional<Rol> el rol si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<Rol> findByNombre(String nombre) throws JsonNotFoundException {
        //Usamos stream para filtrar por nombre
        //Devuelve el rol si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(r -> r.getNombre().equals(nombre))
                .findFirst();
    }
}
