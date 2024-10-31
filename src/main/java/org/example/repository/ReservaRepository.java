package org.example.repository;


import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.ReservaDTO;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de reservas
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class ReservaRepository implements JSONRepository<Integer, ReservaDTO> {
    private final String ruta = "reservas.json";

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
     * Método que guarda una nueva reserva en el JSON llamando al método write
     * @param dto la nueva reserva que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(ReservaDTO dto) throws JsonNotFoundException {
        // Listamos todos las reservas (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.isEmpty() ? 0 : dtos.getLast().id();
        dto = new ReservaDTO(lastId + 1, dto.fechaInicio(), dto.fechaFin(), dto.bloque(),
                                dto.idAula(),dto.idInscripcion(),dto.diasSemana());

        // Agregamos la nueva reserva
        dtos.add(dto);

        // Convertimos la lista a JSON y la escribimos en el archivo
        var jsonArray = new JsonArray();
        dtos.forEach(i -> jsonArray.add(gson.toJsonTree(i)));
        write(jsonArray);
    }

    /**
     * Método que devuelve la lista de reservas que tenemos en el JSON
     * @return List<UsuarioDTO> lista de los DTO de usuarios en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<ReservaDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos ReservaDTO porque guardamos solo el ID de inscripción y aula que corresponde al usuario en este json
            var listType = new TypeToken<List<ReservaDTO>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar una reserva por ID
     * @param id para buscar y devolver la reserva
     * @return Optional<ReservaDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<ReservaDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la reserva si existe
        //Devuelve null si no existe
        return getAll().stream()
                .filter(dto -> dto.id() == id)
                .findFirst();
    }


    /**
     * Método para borrar una reserva por ID
     * @param id para buscar y borrar la reserva
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todas las reservas y borramos el que tenga ese ID
        var reservas = getAll();
        reservas.removeIf(dto -> dto.id() == id);

        //guardamos la lista sin la reserva con ese ID
        var jsonArray = new JsonArray();
        reservas.forEach(dto -> jsonArray.add(gson.toJsonTree(dto)));
        write(jsonArray);
    }

    /**
     * Método para modificar una reserva
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(ReservaDTO dto) throws JsonNotFoundException {
        // Obtén todas las reservas del JSON
        var dtos = getAll();

        // Busca el índice de la reserva que deseas modificar
        int index = -1;
        for (int i = 0; i < dtos.size(); i++) {
            if (dtos.get(i).id() == dto.id()) {
                index = i;
                break;
            }
        }
        // Si no se encuentra el índice, lanza la excepción
        if (index == -1) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
        // Reemplaza el objeto en la lista con una nueva instancia actualizada
        var nuevoDTO = new ReservaDTO(dto.id(), dto.fechaInicio(),dto.fechaFin(),dto.bloque(),dto.idAula(),
                dto.idInscripcion(),dto.diasSemana());
        dtos.set(index, nuevoDTO);

        // Crea el array JSON actualizado
        var jsonArray = new JsonArray();
        dtos.forEach(d -> jsonArray.add(gson.toJsonTree(d)));

        // Guarda los cambios en el archivo JSON
        write(jsonArray);
    }
}
