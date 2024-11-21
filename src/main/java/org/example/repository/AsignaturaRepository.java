package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Asignatura;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Repositorio de asignatura
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class AsignaturaRepository implements JSONRepository<Integer,Asignatura> {
    private final String ruta = "./json/asignaturas.json";

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
     * @param list List<Asignatura> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<Asignatura> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{getRuta()}");
        }
    }

    /**
     * Método que guarda una nueva asignatura en el JSON llamando al método write
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

        // Guarda los cambios en el archivo JSON
        write(asignaturas);
    }


    /**
     * Método que devuelve la lista de asignaturas que tenemos en el JSON
     * @return List<Asignatura> lista de asignaturas en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Asignatura> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Indico que el tipo va a ser una Lista de asignaturas
            var rolListType = new TypeToken<List<Asignatura>>() {}.getType();
            return getGson().fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar una asignatura por ID
     * @param id para buscar y devolver la asignatura
     * @return Optional<Asignatura> el DTO si lo encuentra o Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<Asignatura> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la asignatura si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(a -> Objects.equals(a.getId(), id))
                .findFirst();
    }

    /**
     * Método para borrar una asignatura por ID
     * @param id para buscar y borrar el asignatura
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException{
        //Traigo lo que tiene el json
        var asignaturas = getAll();

        //Borro al que tenga el id
        asignaturas.removeIf(a -> Objects.equals(a.getId(), id));

        // Guarda los cambios en el archivo JSON
        write(asignaturas);
    }

    /**
     * Método para modificar la asignatura
     * @param asignatura que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(Asignatura asignatura) throws JsonNotFoundException {
        // Obtén todas las asignaturas del JSON
        var asignaturas = getAll();

        // Busca la asignatura por ID y actualiza sus campos
        var exist = asignaturas.stream()
                .filter(a -> Objects.equals(a.getId(), asignatura.getId()))
                .findFirst()
                .orElseThrow(() -> new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}"));

        // Actualiza los atributos de la asignatura existente con los del nuevo objeto
        exist.setId(asignatura.getId());
        exist.setNombre(asignatura.getNombre());
        exist.setCodigo(asignatura.getCodigo());
        exist.setRequiereLaboratorio(asignatura.isRequiereLaboratorio());

        // Guarda los cambios en el archivo JSON
        write(asignaturas);
    }

    /**
     * Método para buscar una asignatura por código
     * @param codigo para buscar y devolver la asignatura
     * @return Optional<Asignatura> el DTO si lo encuentra o Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<Asignatura> findByCodigo(int codigo) throws JsonNotFoundException {
        //Usamos stream para filtrar por código
        //Devuelve la asignatura si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(a -> a.getCodigo() == codigo)
                .findFirst();
    }

}
