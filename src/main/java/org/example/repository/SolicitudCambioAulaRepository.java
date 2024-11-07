package org.example.repository;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.JsonNotFoundException;
import org.example.model.dto.SolicitudCambioAulaDTO;

import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repositorio de solicitud de cambio de aula
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class SolicitudCambioAulaRepository implements JSONRepository<Integer, SolicitudCambioAulaDTO>{
    private final String ruta = "solicitudes.json";

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
     * Método que guarda una nueva solicitud en el JSON llamando al método write
     * @param dto la nueva solicitud que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(SolicitudCambioAulaDTO dto) throws JsonNotFoundException {
        // Listamos todos las solicitudes (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.isEmpty() ? 0 : dtos.getLast().id();
        dto = new SolicitudCambioAulaDTO(lastId + 1, dto.idProfesor(),dto.idReserva(),dto.idAula(),dto.estadoSolicitud(),
                dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),dto.diasSemana(),dto.bloqueHorario(), dto.comentarioEstado(),
                dto.comentarioProfesor(),dto.fechaHoraSolicitud());

        // Agregamos la nueva solicitud
        dtos.add(dto);

        // Convertimos la lista a JSON y la escribimos en el archivo
        var jsonArray = new JsonArray();
        dtos.forEach(s -> jsonArray.add(gson.toJsonTree(s)));
        write(jsonArray);
    }

    /**
     * Método que devuelve la lista de solicitudes que tenemos en el JSON
     * @return List<SolicitudCambioAulaDTO> lista de los DTO de solicitudes en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<SolicitudCambioAulaDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos SolicitudCambioAulaDTO porque guardamos solo el ID de Clases que contiene en el json
            var listType = new TypeToken<List<SolicitudCambioAulaDTO>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }

    /**
     * Método para buscar una solicitud por ID
     * @param id para buscar y devolver la solicitud
     * @return Optional<SolicitudCambioAulaDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<SolicitudCambioAulaDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve la solicitud si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> dto.id() == id)
                .findFirst();
    }

    /**
     * Método para borrar una solicitud por ID
     * @param id para buscar y borrar la solicitud
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todas las solicitudes y borramos el que tenga ese ID
        var solicitudes = getAll();
        solicitudes.removeIf(dto -> dto.id() == id);

        //guardamos la lista sin la solicitud con ese ID
        var jsonArray = new JsonArray();
        solicitudes.forEach(dto -> jsonArray.add(gson.toJsonTree(dto)));
        write(jsonArray);
    }

    /**
     * Método para modificar una solicitud
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(SolicitudCambioAulaDTO dto) throws JsonNotFoundException {
        // Obtén todas las solicitudes del JSON
        var dtos = getAll();

        // Busca el índice de la solicitud que deseas modificar
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
        var nuevoDTO = new SolicitudCambioAulaDTO(dto.id(), dto.idProfesor(),dto.idReserva(),dto.idAula(),dto.estadoSolicitud(),
                dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),dto.diasSemana(),dto.bloqueHorario(), dto.comentarioEstado(),
                dto.comentarioProfesor(),dto.fechaHoraSolicitud());
        dtos.set(index, nuevoDTO);

        // Crea el array JSON actualizado
        var jsonArray = new JsonArray();
        dtos.forEach(d -> jsonArray.add(gson.toJsonTree(d)));

        // Guarda los cambios en el archivo JSON
        write(jsonArray);
    }

    /**
     * Método para buscar una solicitud de cambio de aula que coincida con los parámetros dados.
     * @param idProfesor    el ID del profesor
     * @param idAula        el ID del aula
     * @param idReserva     el ID de la reserva original
     * @param fechaInicio   la fecha de inicio de la solicitud
     * @param fechaFin      la fecha de fin de la solicitud
     * @param diasSemana    los días de la semana en que aplica la solicitud
     * @param bloqueHorario el bloque horario de la solicitud
     * @return Optional<SolicitudCambioAulaDTO> el DTO si se encuentra una coincidencia u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<SolicitudCambioAulaDTO> find(Integer idProfesor, Integer idAula, Integer idReserva,
                                                 LocalDate fechaInicio, LocalDate fechaFin, Set<DayOfWeek> diasSemana,
                                                  BloqueHorario bloqueHorario) throws JsonNotFoundException {
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.idProfesor(), idProfesor) &&
                        Objects.equals(dto.idAula(), idAula) &&
                        Objects.equals(dto.idReserva(), idReserva) &&
                        dto.fechaInicio().equals(fechaInicio) &&
                        dto.fechaFin().equals(fechaFin) &&
                        dto.diasSemana().equals(diasSemana) &&
                        dto.bloqueHorario().equals(bloqueHorario))
                .findFirst();
    }


    /**
     * Método para buscar las solicitudes de cambio de aula que coincida con los parámetros dados.
     * @param idAula        el ID del aula
     * @param fechaInicio   la fecha de inicio de las solicitudes
     * @param fechaFin      la fecha de fin de las solicitudes
     * @param diasSemana    los días de la semana en que aplica las solicitudes
     * @param bloqueHorario el bloque horario de las solicitudes
     * @param estadoSolicitud el estado de las solicitudes
     * @return List<SolicitudCambioAulaDTO> lista de las solicitudes que cumplan con estos parámetros
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<SolicitudCambioAulaDTO> find(Integer idAula, LocalDate fechaInicio,
                                             LocalDate fechaFin, Set<DayOfWeek> diasSemana,
                                             BloqueHorario bloqueHorario, EstadoSolicitud estadoSolicitud) throws JsonNotFoundException {
        return getAll().stream()
                .filter(dto -> Objects.equals(dto.idAula(), idAula) &&
                        dto.fechaInicio().equals(fechaInicio) &&
                        dto.fechaFin().equals(fechaFin) &&
                        dto.diasSemana().equals(diasSemana) &&
                        dto.bloqueHorario().equals(bloqueHorario) &&
                        dto.estadoSolicitud().equals(estadoSolicitud))
                .collect(Collectors.toList());
    }

}
