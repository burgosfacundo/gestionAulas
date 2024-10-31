package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Aula;
import org.example.model.Reserva;
import org.example.model.dto.ReservaDTO;
import org.example.repository.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
public class ReservaService implements Service<Integer,Reserva> {
    ReservaRepository repositorio = new ReservaRepository();
    AulaRepository aulaRepository = new AulaRepository();
    AulaService aulaService = new AulaService();
    InscripcionService inscripcionService = new InscripcionService();
    InscripcionRepository inscripcionRepository = new InscripcionRepository();
    AsignaturaRepository asignaturaRepository = new AsignaturaRepository();


    /**
     * Método para listar todas las reservas
     * @return List<Reserva>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Reserva> listar() throws JsonNotFoundException {
        List<Reserva> reservas = new ArrayList<>();
        var dtoList = repositorio.getAll();
        for (ReservaDTO dto : dtoList){
            var optionalAula = aulaRepository.findById(dto.idAula());
            try {
                var inscripcion = inscripcionService.obtener(dto.idInscripcion());
                optionalAula.ifPresent(aula -> reservas.add(toReserva(dto, aula, inscripcion)));
            }catch (NotFoundException e){
                throw new JsonNotFoundException("Ocurrio un error con el json de inscripciones");
            }

        }
        return reservas;
    }


    /**
     * Método para guardar una reserva
     * @param reserva que queremos guardar
     * @return Reserva que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si la reserva tiene un ID de Aula o de Inscripción que no existe
     * @throws BadRequestException si existe un problema con los datos de reserva
     */
    @Override
    public Reserva guardar(Reserva reserva) throws JsonNotFoundException, BadRequestException {
        var numeroAula = reserva.getAula().getNumero();
        var idInscripcion = reserva.getInscripcion().getId();

        var optionalInscripcion = inscripcionRepository.findById(idInscripcion);
        if (optionalInscripcion.isEmpty()){
            throw new BadRequestException(STR."La inscripcion \{idInscripcion} no existe");
        }

        var inscripcion = optionalInscripcion.get();

        var asignatura = asignaturaRepository.findById(inscripcion.idAsignatura());

        var optionalAula = aulaRepository.findByNumero(numeroAula);
        if (optionalAula.isEmpty()){
            throw new BadRequestException(STR."El aula: \{numeroAula} no existe");
        }

        var aula = optionalAula.get();

        var aulasDisponibles = aulaService.listarAulasDisponibles(reserva.getFechaInicio(),
                                                                                reserva.getFechaFin(),
                                                                                reserva.getBloque(),
                                                                                reserva.getDiasSemana());

        if (!aulasDisponibles.contains(aula)){
            throw new BadRequestException(STR."El aula \{numeroAula} no esta disponible.");
        }

        if (inscripcion.fechaFinInscripcion().isAfter(LocalDate.now())){
            if (aula.getCapacidad() < (inscripcion.margenAlumnos()+inscripcion.cantidadAlumnos())){
                throw new BadRequestException(STR."El aula \{numeroAula} no alcanza para la cantidad de alumnos");
            }
        }else {
            if (aula.getCapacidad() < inscripcion.cantidadAlumnos()){
                throw new BadRequestException(STR."El aula \{numeroAula} no alcanza para la cantidad de alumnos");
            }
        }

        if (asignatura.get().isRequiereLaboratorio() && (aula.getComputadoras() == 0)){
            throw new BadRequestException(STR."El aula \{numeroAula} no es un laboratorio, no sirve para \{asignatura.get().getNombre()}");
        }

        repositorio.save(toDTO(reserva));
        return reserva;
    }


    /**
     * Método para eliminar una reserva por ID
     * @param id de la reserva que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una reserva con ese ID
     */
    @Override
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe una reserva con ese ID, si no lanzamos excepción
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una reserva con el id: \{id}");
        }
        repositorio.deleteById(id);
    }

    /**
     * Método para obtener una reserva por ID
     * @param id de la reserva
     * @return Reserva con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la reserva con ese ID
     */
    @Override
    public Reserva obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una reserva con el id: \{id}");
        }
        var dto = optional.get();

        var optionalAula = aulaRepository.findById(dto.idAula());
        if (optionalAula.isEmpty()){
            throw new NotFoundException(STR."No existe un aula con el id: \{dto.idAula()}");
        }

        var inscripcion = inscripcionService.obtener(dto.idInscripcion());

        return toReserva(dto,optionalAula.get(),inscripcion);
    }

    /**
     * Método para modificar una reserva
     * @param reserva que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra reserva o aula o inscripción
     */
    @Override
    public void modificar(Reserva reserva) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(reserva.getId());
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una reserva con el id: \{reserva.getId()}");
        }
        var dto = optional.get();

        var optionalAula = aulaRepository.findById(dto.idAula());
        if (optionalAula.isEmpty()){
            throw new NotFoundException(STR."No existe un aula con el id: \{dto.idAula()}");
        }

        var optionalInscripcion = inscripcionRepository.findById(reserva.getInscripcion().getId());
        if (optionalInscripcion.isEmpty()){
            throw new NotFoundException(STR."No existe una inscripción con el id: \{dto.idAula()}");
        }

        repositorio.modify(toDTO(reserva));
    }


    /**
     * Método para mapear un dto a Inscripción
     * @param dto que queremos mapear
     * @return Inscripción mapeado desde dto
     */
    private Reserva toReserva(ReservaDTO dto, Aula aula, model.Inscripcion inscripcion){
        //Retornamos la inscripción mapeando desde DTO, incluyendo su asignatura y profesor
        return new Reserva(
                dto.id(),
                dto.fechaInicio(),
                dto.fechaFin(),
                dto.bloque(),
                aula,
                inscripcion,
                dto.diasSemana()
        );
    }

    /**
     * Método para mapear una Reserva a dto
     * @param reserva que queremos mapear
     * @return ReservaDTO mapeado desde Reserva
     */
    private ReservaDTO toDTO(Reserva reserva) {
        //Retornamos el DTO mapeado desde Reserva
        return new ReservaDTO(
                reserva.getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getBloque(),
                reserva.getAula().getId(),
                reserva.getInscripcion().getId(),
                reserva.getDiasSemana()
        );
    }

}
