package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.adaptadores.AdaptadorLocalDate;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.InscripcionDTO;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Repositorio de inscripciones
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class InscripcionRepository implements JSONRepository<Integer, InscripcionDTO> {
    String relativePath = "gestionAulas/json/inscripciones.json";
    String ruta = Paths.get(System.getProperty("user.dir"), relativePath).toString();
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
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate()).create();
    }

    /**
     * Método para escribir un archivo JSON
     * @param list List<InscripcionDTO> que se quiere escribir
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public void write(List<InscripcionDTO> list) throws JsonNotFoundException {
        try (FileWriter writer = new FileWriter(getRuta())) {
            getGson().toJson(list, writer);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{getRuta()}");
        }
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
                dto.idAsignatura(),dto.comision(),dto.idProfesor());

        // Agregamos la nueva inscripción
        dtos.add(dto);

        // Convertimos la lista a JSON y la escribimos en el archivo
        write(dtos);
    }

    /**
     * Método que devuelve la lista de inscripciones que tenemos en el JSON
     * @return List<InscripcionDTO> lista de los DTO de inscripciones en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<InscripcionDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos InscripcionDTO porque guardamos solo los id de otras entidades
            // que corresponde a la inscripción en este json
            var listType = new TypeToken<List<InscripcionDTO>>() {}.getType();
            return getGson().fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar una inscripción por ID
     * @param id para buscar y devolver la inscripción
     * @return Optional<InscripcionDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<InscripcionDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la Inscripción si existe
        //Devuelve Optional.empty() si no existe
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.id(), id))
                .findFirst();
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
        inscripciones.removeIf(dto -> Objects.equals(dto.id(), id));

        write(inscripciones);
    }

    /**
     * Método para modificar una inscripción
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(InscripcionDTO dto) throws JsonNotFoundException {
        // Obtén todas las inscripciones del JSON
        var dtos = getAll();

        // Busca el índice de la inscripción que deseas modificar
        int index = -1;
        for (int i = 0; i < dtos.size(); i++) {
            if (Objects.equals(dtos.get(i).id(), dto.id())) {
                index = i;
                break;
            }
        }
        // Si no se encuentra el índice, lanza la excepción
        if (index == -1) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
        // Reemplaza el objeto en la lista con una nueva instancia actualizada
        var nuevoDTO = new InscripcionDTO(dto.id(), dto.cantidadAlumnos(),dto.margenAlumnos(),
                dto.fechaFinInscripcion(),dto.idAsignatura(),dto.comision(),dto.idProfesor());
        dtos.set(index, nuevoDTO);

        // Guarda los cambios en el archivo JSON
        write(dtos);
    }


    /**
     * Método para saber si una inscripción ya existe
     * @param idAsignatura id de la asignatura de la inscripción
     * @param idProfesor id del profesor de la inscripción
     * @param comision comisión de la inscripción
     * @return Optional<InscripcionDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    public Optional<InscripcionDTO> find(Integer idAsignatura, Integer idProfesor, String comision) throws JsonNotFoundException {
        //Usamos stream para filtrar por los id de Asignatura y Profesor y la comisión
        //Devuelve la Inscripción si existe
        //Devuelve Optional.empty() si no existe
        return getAll().stream()
                .filter(dto -> dto.idAsignatura() == idAsignatura &&
                        dto.idProfesor() == idProfesor &&
                        dto.comision().equals(comision))
                .findFirst();
    }
}
