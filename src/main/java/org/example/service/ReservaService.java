package org.example.service;

import org.example.enums.BloqueHorario;
import org.example.enums.EstadoSolicitud;
import org.example.exception.BadRequestException;
import org.example.exception.ConflictException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.model.dto.ReservaDTO;
import org.example.repository.*;
import org.example.utils.Mapper;
import org.example.utils.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
public class ReservaService{
    private final ReservaRepository repositorio = new ReservaRepository();
    private final AulaRepository aulaRepository = new AulaRepository();
    private final AulaService aulaService = new AulaService();
    private final InscripcionService inscripcionService = new InscripcionService();
    private final AsignaturaRepository asignaturaRepository = new AsignaturaRepository();
    private final SolicitudCambioAulaRepository solicitudRepository = new SolicitudCambioAulaRepository();


    /**
     * Método para listar todas las reservas
     * @return List<Reserva>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Reserva> listar() throws JsonNotFoundException, NotFoundException {
        List<Reserva> reservas = new ArrayList<>();
        //Obtenemos todas las reservas del JSON
        var dtoList = repositorio.getAll();
        //Recorremos
        for (ReservaDTO dto : dtoList){
            // Validamos que el aula exista
            var aula = validarAulaExistenteById(dto.idAula());
            // Validamos que la inscripción exista
            var inscripcion = validarInscripcionExistente(dto.idInscripcion());
            // Mapeamos y guardamos en reservas
            reservas.add(Mapper.toReserva(dto, aula, inscripcion));
        }
        return reservas;
    }


    /**
     * Método para guardar una reserva
     * @param reserva que queremos guardar
     * @return Reserva que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un problema con los datos de reserva
     */
    public Reserva guardar(Reserva reserva) throws JsonNotFoundException, BadRequestException, ConflictException, NotFoundException {
        var idAula = reserva.getAula().getId();
        var idInscripcion = reserva.getInscripcion().getId();

        //Validamos y obtenemos la inscripción de la reserva
        var inscripcion = validarInscripcionExistente(idInscripcion);

        //Validamos y obtenemos el aula de la reserva
        var aula = validarAulaExistenteById(idAula);

        // Validamos si el aula está disponible en el rango y bloque horario especificado
        validarDisponibilidadAula(reserva);

        // Validamos la capacidad del aula y la cantidad de alumnos en la inscripción
        validarCapacidadAula(aula,inscripcion);

        // Validación de que el aula sea un laboratorio si la asignatura lo requiere
        validarRequiereLaboratorio(aula,inscripcion.getAsignatura().getId());

        // Validamos si existen solicitudes de cambio pendientes que generen conflicto con la reserva
        validarSolicitudesPendientes(reserva);

        // Guardamos la reserva si pasa todas las validaciones
        repositorio.save(Mapper.reservaToDTO(reserva));
        return reserva;
    }


    /**
     * Método para eliminar una reserva por ID
     * @param id de la reserva que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una reserva con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe una reserva con ese ID, si no lanzamos excepción
        validarReservaExistente(id);
        repositorio.deleteById(id);
    }

    /**
     * Método para obtener una reserva por ID
     * @param id de la reserva
     * @return Reserva con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     */
    public Reserva obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos que existe una reserva con ese ID, si no lanzamos excepción
        var dto = validarReservaExistente(id);

        // Validamos que exista el aula
        var aula = validarAulaExistenteById(dto.idAula());

        // Validamos que exista la inscripción
        var inscripcion = validarInscripcionExistente(dto.idInscripcion());

        // Mapeamos a reserva y retornamos la reserva
        return Mapper.toReserva(dto,aula,inscripcion);
    }

    /**
     * Método para modificar una reserva
     * @param reserva que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra reserva o aula o inscripción
     */
    public void modificar(Reserva reserva) throws JsonNotFoundException, NotFoundException, BadRequestException, ConflictException {
        // Validamos que existe la reserva que se quiere modificar
        repositorio.findById(reserva.getId())
                .orElseThrow(()-> new NotFoundException(STR."No existe una reserva con el id: \{reserva.getId()}"));

        //Validamos y obtenemos la inscripción de la reserva
        var inscripcion = validarInscripcionExistente(reserva.getInscripcion().getId());

        //Validamos y obtenemos el aula de la reserva
        var aula = validarAulaExistenteById(reserva.getAula().getId());

        // Validamos si el aula está disponible en el rango y bloque horario especificado
        validarDisponibilidadAula(reserva);

        // Validamos la capacidad del aula y la cantidad de alumnos en la inscripción
        validarCapacidadAula(aula,inscripcion);

        // Validación de que el aula sea un laboratorio si la asignatura lo requiere
        validarRequiereLaboratorio(aula,inscripcion.getAsignatura().getId());

        // Validamos si existen solicitudes de cambio pendientes que generen conflicto con la reserva
        validarSolicitudesPendientes(reserva);

        // Modificamos la reserva si pasa todas las validaciones
        repositorio.modify(Mapper.reservaToDTO(reserva));
    }


    // Validaciones
    /**
     * Método para validar la existencia de una reserva por ID
     * @param id de la reserva que se quiere verificar
     * @return ReservaDTO si existe
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private ReservaDTO validarReservaExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(id)
                .orElseThrow(()-> new NotFoundException(STR."No existe una reserva con el id: \{id}"));
    }

    /**
     * Método para validar la existencia de un Aula por ID
     * @param idAula del aula que se quiere verificar
     * @return Aula si existe
     * @throws NotFoundException Si no se encuentra el aula con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Aula validarAulaExistenteById(Integer idAula) throws NotFoundException, JsonNotFoundException {
        return aulaRepository.findById(idAula)
                .orElseThrow(() -> new NotFoundException(STR."No existe un aula con el id: \{idAula}"));
    }

    /**
     * Método para validar la existencia de una Asignatura por ID
     * @param idAsignatura de la asignatura que se quiere verificar
     * @return Asignatura si existe
     * @throws NotFoundException Si no se encuentra la asignatura con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Asignatura validarAsignaturaExistente(Integer idAsignatura) throws NotFoundException, JsonNotFoundException {
        return asignaturaRepository.findById(idAsignatura)
                .orElseThrow(()-> new NotFoundException(STR."No existe una asignatura con el id: \{idAsignatura}"));
    }


    /**
     * Método para validar la existencia de una inscripción
     * @param idInscripcion de la inscripción que se quiere verificar
     * @return Inscripción si existe
     * @throws NotFoundException Si no se encuentra al inscripción con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Inscripcion validarInscripcionExistente(Integer idInscripcion) throws NotFoundException, JsonNotFoundException {
        return inscripcionService.obtener(idInscripcion);
    }


    /**
     * Método para validar la capacidad de un aula con respecto a la cantidad de alumnos de una Inscripción
     * @param aula que se quiere validar
     * @param inscripcion que se quiere validar
     * @throws BadRequestException si no alcanza la capacidad del aula para la cantidad de alumnos de la inscripción
     */
    private void validarCapacidadAula(Aula aula, Inscripcion inscripcion) throws BadRequestException {
        int alumnosRequeridos = inscripcion.getCantidadAlumnos() +
                (inscripcion.getFechaFinInscripcion().isAfter(LocalDate.now()) ? inscripcion.getMargenAlumnos() : 0);
        if (aula.getCapacidad() < alumnosRequeridos) {
            throw new BadRequestException(STR."El aula \{aula.getNumero()} tiene capacidad para \{aula.getCapacidad()} alumnos, pero se requieren \{alumnosRequeridos}.");
        }
    }

    /**
     * Método para validar la disponibilidad de un aula
     * @param reserva la reserva que contiene el aula y el período que se valida
     * @throws BadRequestException si no está disponible el aula en ese período
     * @throws JsonNotFoundException sí ocurre un problema con el archivo JSON de aulas
     */
    private void validarDisponibilidadAula(Reserva reserva) throws BadRequestException, JsonNotFoundException {
        var aulasDisponibles = aulaService.listarEspaciosDisponibles(reserva.getFechaInicio(), reserva.getFechaFin()
                , reserva.getDiasYBloques());
        if (!aulasDisponibles.contains(reserva.getAula())) {
            throw new BadRequestException(STR."El aula \{reserva.getAula().getNumero()} no está disponible.");
        }
    }

    /**
     * Método para validar las solicitudes pendientes
     * @param reserva que contiene la información de la reserva
     * @throws ConflictException si existe alguna solicitud pendiente con los mismos parámetros que la reserva
     * @throws JsonNotFoundException si ocurre un problema con el archivo JSON
     */
    private void validarSolicitudesPendientes(Reserva reserva) throws ConflictException, JsonNotFoundException {
        var solicitudesPendientes = solicitudRepository.find(reserva.getAula().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getDiasYBloques(),
                EstadoSolicitud.PENDIENTE);

        if (!solicitudesPendientes.isEmpty()) {
            // Creamos un StringBuilder para armar el mensaje de conflicto
            StringBuilder mensajeConflicto = new StringBuilder();
            mensajeConflicto.append(STR."Hay solicitudes pendientes en el Aula \{reserva.getAula().getNumero()}.\n");

            for (var solicitud : solicitudesPendientes) {
                // Obtenemos los días y bloques horarios de la solicitud pendiente
                Map<DayOfWeek, Set<BloqueHorario>> diasYBloquesSolicitud = solicitud.diasYBloques();

                mensajeConflicto.append("Solicitud ID: ").append(solicitud.id())
                        .append(" - Días y bloques en conflicto:\n");

                // Iteramos sobre los días y bloques de la solicitud para mostrar los conflictos
                diasYBloquesSolicitud.forEach((dia, bloques) -> {
                    String bloquesStr = bloques.stream()
                            .map(BloqueHorario::name)
                            .collect(Collectors.joining(", "));

                    mensajeConflicto.append("  Día: ").append(Utils.obtenerDiasEnEspaniol(Set.of(dia)))
                            .append(" - Bloques: ").append(bloquesStr).append("\n");
                });
            }

            throw new ConflictException(mensajeConflicto.toString());
        }
    }

    private void validarRequiereLaboratorio(Aula aula,Integer idAsignatura) throws JsonNotFoundException, NotFoundException, BadRequestException {
        var asignatura = validarAsignaturaExistente(idAsignatura);
        if (asignatura.isRequiereLaboratorio() && !(aula instanceof Laboratorio)){
            throw new BadRequestException(STR."El aula \{aula.getNumero()} no es un laboratorio, no sirve para \{asignatura.getNombre()}");
        }
    }

    //filtros

    private List<Reserva> listarReservasPorProfesor(int idProfe)
            throws JsonNotFoundException, NotFoundException {
        ProfesorService ps = new ProfesorService();
        if(ps.listar().stream().noneMatch(p -> p.getId() == idProfe)){
            throw new NotFoundException("El profesor no existe");
        }

        return listar().stream()
                .filter(r -> r.getInscripcion().getProfesor().getId() == idProfe)
                .collect(Collectors.toList());
    }

    private List<Reserva> listarReservasPorProfesor(Profesor profesor)
            throws JsonNotFoundException, NotFoundException {
        ProfesorService ps = new ProfesorService();
        if(ps.listar().stream().noneMatch(p -> p.equals(profesor))){
            throw new NotFoundException("El profesor no existe");
        }

        return listar().stream()
                .filter(r -> r.getInscripcion().getProfesor().equals(profesor))
                .collect(Collectors.toList());
    }

    private List<Reserva> listarReservasPorComision(String comision)
            throws JsonNotFoundException, NotFoundException{
        InscripcionService is = new InscripcionService();
        if(is.listar().stream().noneMatch(i -> i.getComision().equals(comision))){
            throw new NotFoundException("La comisión no existe.");
        }

        return listar().stream()
                .filter(r -> r.getInscripcion().getComision().equals(comision))
                .collect(Collectors.toList());
    }

    private List<Reserva> listarReservasPorAsignatura(Asignatura asignatura)
        throws JsonNotFoundException, NotFoundException {
        InscripcionService is = new InscripcionService();
        if(is.listar().stream().noneMatch(i -> i.getAsignatura().equals(asignatura))){
            throw new NotFoundException("La comisión no existe.");
        }

        return listar().stream()
                .filter(r -> r.getInscripcion().getAsignatura().equals(asignatura))
                .collect(Collectors.toList());
    }

}
