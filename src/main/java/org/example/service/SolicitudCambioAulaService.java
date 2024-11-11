package org.example.service;

import org.example.enums.EstadoSolicitud;
import org.example.enums.TipoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.model.dto.SolicitudCambioAulaDTO;
import org.example.repository.AulaRepository;
import org.example.repository.ProfesorRepository;
import org.example.repository.SolicitudCambioAulaRepository;
import org.example.utils.Mapper;

import java.util.ArrayList;
import java.util.List;

public class SolicitudCambioAulaService{
    private final SolicitudCambioAulaRepository repositorio = new SolicitudCambioAulaRepository();
    private final AulaRepository aulaRepository = new AulaRepository();
    private final AulaService aulaService = new AulaService();
    private final ReservaService reservaService = new ReservaService();
    private final ProfesorRepository profesorRepository = new ProfesorRepository();
    /**
     * Método para listar todas las solicitudes
     * @return List<SolicitudCambioAula>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<SolicitudCambioAula> listar() throws JsonNotFoundException, NotFoundException {
        List<SolicitudCambioAula> solicitudes = new ArrayList<>();
        var dtoList = repositorio.getAll();
        for (SolicitudCambioAulaDTO dto : dtoList){
            // Validamos que el aula exista
            var aula = validarAulaExistente(dto.idAula());
            // Validamos que el profesor exista
            var profesor = validarProfesorExistente(dto.idProfesor());
            // Validamos que la reserva exista
            var reserva = validarReservaExistente(dto.idReserva());

            // Mapeamos y guardamos en solicitudes
            solicitudes.add(Mapper.toSolicitud(dto,profesor,aula,reserva));
        }
        return solicitudes;
    }

    /**
     * Método para guardar una solicitud
     * @param solicitud que queremos guardar
     * @return SolicitudCambioAula que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe una solicitud, o no se encuentra las clases que contiene
     */
    public SolicitudCambioAula guardar(SolicitudCambioAula solicitud) throws JsonNotFoundException, NotFoundException, BadRequestException {
        var idAula = solicitud.getNuevaAula().getId();
        var idProfesor = solicitud.getProfesor().getId();
        var idReserva = solicitud.getReservaOriginal().getId();

        // Validamos que no exista una solicitud con esas características
        var solicitudExistente = repositorio.find(idProfesor, idAula, idReserva, solicitud.getFechaInicio(),
                solicitud.getFechaFin(), solicitud.getDiasYBloques());
        if (solicitudExistente.isPresent()) {
            throw new BadRequestException("Ya existe una solicitud con esas características");
        }

        validarAulaExistente(idAula);
        validarDisponibilidadAula(solicitud);
        validarProfesorExistente(idProfesor);
        validarReservaExistente(idReserva);

        repositorio.save(Mapper.solicitudToDTO(solicitud));
        return solicitud;
    }



    /**
     * Método para eliminar una solicitud por ID
     * @param id de la solicitud que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una solicitud con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos que existe una solicitud con ese ID, si no lanzamos excepción
        validarSolicitudExistente(id);
        repositorio.deleteById(id);
    }


    /**
     * Método para obtener una solicitud por ID
     * @param id de la solicitud
     * @return SolicitudCambioAula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     */
    public SolicitudCambioAula obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        // Validamos que existe el aula, si no lanzamos excepción
        var aula = validarAulaExistente(dto.idAula());

        // Validamos que existe el profesor, si no lanzamos excepción
        var profesor = validarProfesorExistente(dto.idProfesor());

        // Validamos que existe, si no lanzamos excepción
        var reserva = validarReservaExistente(dto.idReserva());

        //Devuelvo la solicitud, luego de mapearla de DTO a SolicitudCambioAula
        return Mapper.toSolicitud(dto,profesor,aula,reserva);
    }


    /**
     * Método para modificar una solicitud
     * @param solicitud que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra solicitud o las clases que contiene
     */
    public void modificar(SolicitudCambioAula solicitud) throws JsonNotFoundException, NotFoundException, BadRequestException {
        // Validamos y obtenemos la solicitud por ID
        var dto = validarSolicitudExistente(solicitud.getId());

        // Validamos que existe el aula, si no lanzamos excepción
        validarAulaExistente(dto.idAula());

        // Validamos que el aula esté disponible
        validarDisponibilidadAula(solicitud);

        // Validamos que existe el profesor, si no lanzamos excepción
        validarProfesorExistente(dto.idProfesor());

        // Validamos que existe la reserva, si no lanzamos excepción
        reservaService.obtener(dto.idReserva());

        repositorio.modify(Mapper.solicitudToDTO(solicitud));
    }

    /**
     * Método para aprobar una solicitud pendiente
     * @param id de la solicitud a aprobar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws BadRequestException si la solicitud no esta pendiente
     * @throws ConflictException si ocurre un problema de conflictos al guardar la nueva reserva
     */
    public void aprobarSolicitud(Integer id) throws NotFoundException, JsonNotFoundException, BadRequestException, ConflictException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!dto.estadoSolicitud().equals(EstadoSolicitud.PENDIENTE)){
            throw new BadRequestException(STR."La solicitud \{id} no esta pendiente");
        }

        //Creamos un DTO con la solicitud aprobada
        SolicitudCambioAulaDTO nuevoDTO = new SolicitudCambioAulaDTO(dto.id(),dto.idProfesor(),dto.idReserva(),
                dto.idAula(),EstadoSolicitud.APROBADA, dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),
                dto.diasYBloques(),dto.comentarioEstado(),dto.comentarioProfesor(), dto.fechaHoraSolicitud());

        //La modificamos
        repositorio.modify(nuevoDTO);

        var reserva = reservaService.obtener(dto.idReserva());

        // Si la solicitud es temporal creamos una nueva reserva
        if (dto.tipoSolicitud().equals(TipoSolicitud.TEMPORAL)){
            reservaService.guardar(new Reserva(null,dto.fechaInicio(),dto.fechaFin(),new Aula(dto.id()),
                    reserva.getInscripcion(),dto.diasYBloques()));

            //Si no la reserva es Permanente y modificamos la original
        }else if(dto.tipoSolicitud().equals(TipoSolicitud.PERMANENTE)){
            reservaService.modificar(new Reserva(reserva.getId(),dto.fechaInicio(),dto.fechaFin(),new Aula(dto.id()),
                    reserva.getInscripcion(),dto.diasYBloques()));
        }
    }

    /**
     * Método para rechazar una solicitud pendiente
     * @param id de la solicitud a rechazar
     * @throws NotFoundException si no encuentra la solicitud o reserva
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     * @throws BadRequestException si la solicitud no esta pendiente
     */
    public void rechazarSolicitud(Integer id) throws NotFoundException, JsonNotFoundException, BadRequestException {
        // Validamos que existe una solicitud con ese ID
        var dto = validarSolicitudExistente(id);

        //Validamos que la solicitud esté pendiente
        if (!dto.estadoSolicitud().equals(EstadoSolicitud.PENDIENTE)) {
            throw new BadRequestException(STR."La solicitud \{id} no esta pendiente");
        }

        SolicitudCambioAulaDTO nuevoDTO = new SolicitudCambioAulaDTO(dto.id(),dto.idProfesor(),dto.idReserva(),
                dto.idAula(),EstadoSolicitud.RECHAZADA, dto.tipoSolicitud(),dto.fechaInicio(),dto.fechaFin(),
                dto.diasYBloques(),dto.comentarioEstado(),dto.comentarioProfesor(), dto.fechaHoraSolicitud());

        //La modificamos
        repositorio.modify(nuevoDTO);
    }




    // Validaciones
    /**
     * Método para validar la existencia de una solicitud
     * @param id de la solicitud que se quiere verificar
     * @return SolicitudCambioAulaDTO si existe
     * @throws NotFoundException Si no se encuentra la solicitud con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private SolicitudCambioAulaDTO validarSolicitudExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(id)
                .orElseThrow(() -> new NotFoundException(STR."No existe una solicitud con el id: \{id}"));
    }

    /**
     * Método para validar la existencia de un Aula
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistente(Integer idAula) throws NotFoundException, JsonNotFoundException {
        return aulaRepository.findById(idAula)
                .orElseThrow(() -> new NotFoundException(STR."No existe un aula con el id: \{idAula}"));
    }

    /**
     * Método para validar la existencia de un Profesor
     * @param idProfesor del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Profesor validarProfesorExistente(Integer idProfesor) throws NotFoundException, JsonNotFoundException {
        return profesorRepository.findById(idProfesor)
                .orElseThrow(() -> new NotFoundException(STR."No existe un profesor con el id: \{idProfesor}"));
    }


    /**
     * Método para validar la existencia de una Reserva
     * @param idReserva de la reserva que se quiere verificar
     * @return Reserva si existe
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Reserva validarReservaExistente(Integer idReserva) throws NotFoundException, JsonNotFoundException{
        return reservaService.obtener(idReserva);
    }

    /**
     * Método para validar la disponibilidad de un aula
     * @param solicitud la solicitud que contiene el aula y el período que se valida
     * @throws BadRequestException si no está disponible el aula en ese período
     * @throws JsonNotFoundException sí ocurre un problema con el archivo JSON de aulas
     */
    private void validarDisponibilidadAula(SolicitudCambioAula solicitud) throws JsonNotFoundException, BadRequestException {
        //Traemos todas las aulas disponibles en ese período
        var aulasDisponibles = aulaService.listarAulasDisponibles(solicitud.getFechaInicio(),solicitud.getFechaFin(),solicitud.getDiasYBloques());
        //Si no está dentro de las disponibles lanzamos excepción
        if (!aulasDisponibles.contains(solicitud.getNuevaAula())) {
            throw new BadRequestException(STR."El aula \{solicitud.getNuevaAula()} no está disponible.");
        }
    }
}
