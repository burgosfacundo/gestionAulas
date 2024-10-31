package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.InscripcionDTO;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Repositorio de inscripciones
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class InscripcionRepository implements JSONRepository<Integer, InscripcionDTO> {
    private final String ruta = "inscripciones.json";

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
     * Método que guarda una nueva inscripción en el JSON llamando al método write
     * @param dto la nueva inscripción que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(InscripcionDTO dto) throws JsonNotFoundException {
        // Listamos todos las inscripciones (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.isEmpty() ? 0 : dtos.getLast().id();
        dto = new InscripcionDTO(lastId + 1, dto.cantidadAlumnos(), dto.margenAlumnos(),dto.fechaFinInscripcion(),
                dto.idAsignatura(),dto.idComision(),dto.idProfesor());

        // Agregamos la nueva inscripción
        dtos.add(dto);

        // Convertimos la lista a JSON y la escribimos en el archivo
        var jsonArray = new JsonArray();
        dtos.forEach(d -> jsonArray.add(gson.toJsonTree(d)));
        write(jsonArray);
    }

    /**
     * Método que devuelve la lista de inscripciones que tenemos en el JSON
     * @return List<InscripcionDTO> lista de los dto de inscripciones en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<InscripcionDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos InscripcionDTO porque guardamos solo los id de otras entidades
            // que corresponde a la inscripción en este json
            var listType = new TypeToken<List<InscripcionDTO>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar una inscripción por ID
     * @param id para buscar y devolver la inscripción
     * @return InscripcionDTO con el id del parámetro
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public InscripcionDTO findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la Inscripción si existe
        //Devuelve null si no existe
        return getAll().stream()
                .filter(dto -> dto.id() == id)
                .findFirst()
                .orElse(null);
    }


    /**
     * Método para borrar una inscripción por ID
     * @param id para buscar y borrar la inscripción
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todas las inscripciones y borramos el que tenga ese ID
        var inscripciones = getAll();
        inscripciones.removeIf(dto -> dto.id() == id);

        //guardamos la lista sin la inscripción con ese ID
        var jsonArray = new JsonArray();
        inscripciones.forEach(dto -> jsonArray.add(gson.toJsonTree(dto)));
        write(jsonArray);
    }
}
