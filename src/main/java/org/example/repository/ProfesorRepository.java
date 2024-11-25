package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.Profesor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * Método para obtener el gson que utiliza este repositorio
     * @return Gson que se va a utilizar
     */
    @Override
    public Gson getGson() {
        return new Gson();
    }

    /**
     * Método para escribir un archivo JSON
     * @param list List<Profesor> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<Profesor> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{getRuta()}");
        }
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

        // Guarda los cambios en el archivo JSON
        write(profesores);
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
            return getGson().fromJson(reader, rolListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar un profesor por ID
     * @param id para buscar y devolver el profesor
     * @return Optional<Profesor> el profesor si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<Profesor> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por ID
        //Devuelve el profesor si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
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
        profesores.removeIf(p -> Objects.equals(p.getId(), id));

        // Guarda los cambios en el archivo JSON
        write(profesores);
    }


    /**
     * Método para modificar el profesor
     * @param profesor que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(Profesor profesor) throws JsonNotFoundException {
        // Obtén todos los profesores del JSON
        var profesores = getAll();

        // Busca el profesor por ID y actualiza sus campos
        var exist = profesores.stream()
                .filter(p -> Objects.equals(p.getId(), profesor.getId()))
                .findFirst()
                .orElseThrow(() -> new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}"));

        // Actualiza los atributos del profesor existente con los del nuevo objeto
        exist.setId(profesor.getId());
        exist.setNombre(profesor.getNombre());
        exist.setApellido(profesor.getApellido());
        exist.setMatricula(profesor.getMatricula());

        // Guarda los cambios en el archivo JSON
        write(profesores);
    }

    /**
     * Método para buscar profesor por matrícula
     * @param matricula para buscar el profesor
     * @return Optional<Profesor> el profesor si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<Profesor> findByMatricula(String matricula) throws JsonNotFoundException {
        //Usamos stream para filtrar por matricula
        //Devuelve el profesor si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(p -> p.getMatricula().equals(matricula))
                .findFirst();
    }
}
