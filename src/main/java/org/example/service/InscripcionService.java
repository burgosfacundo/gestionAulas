package org.example.service;


import model.Inscripcion;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Asignatura;
import org.example.model.Profesor;
import org.example.model.dto.InscripcionDTO;
import org.example.repository.AsignaturaRepository;
import org.example.repository.InscripcionRepository;
import org.example.repository.ProfesorRepository;


import java.util.ArrayList;
import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular inscripciones
 */
public class InscripcionService implements Service<Integer,Inscripcion> {
    InscripcionRepository repositorio = new InscripcionRepository();
    AsignaturaRepository asignaturaRepository = new AsignaturaRepository();
    ProfesorRepository profesorRepository = new ProfesorRepository();


    /**
     * Método para listar todas las inscripciones
     * @return List<Inscripcion>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Inscripcion> listar() throws JsonNotFoundException {
        List<Inscripcion> inscripciones = new ArrayList<>();
        var dtoList = repositorio.getAll();
        for (InscripcionDTO dto : dtoList){
            var optionalAsignatura = asignaturaRepository.findById(dto.idAsignatura());
            var optionalProfesor = profesorRepository.findById(dto.idProfesor());
            if (optionalProfesor.isPresent() && optionalAsignatura.isPresent()){
                inscripciones.add(toInscripcion(dto,optionalAsignatura.get(),optionalProfesor.get()));
            }
        }
        return inscripciones;
    }


    /**
     * Método para guardar una inscripción
     * @param inscripcion que queremos guardar
     * @return Inscripción que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe una inscripción con ese profesor,asignatura y comisión
     */
    @Override
    public Inscripcion guardar(Inscripcion inscripcion) throws JsonNotFoundException, BadRequestException {
        var idAsignatura = inscripcion.getAsignatura().getId();
        var idProfesor = inscripcion.getProfesor().getId();
        //Verificamos que no existe una inscripción con esas características, sino lanzamos excepción
        var optional = repositorio.find(idAsignatura, idProfesor, inscripcion.getComision());
        if (optional.isPresent()){
            throw new BadRequestException("Ya existe una inscripción con esas características");
        }

        //Tomamos el ID de Asignatura y verificamos que existe, si no lanzamos excepción
        var optionalAsignatura = asignaturaRepository.findById(idAsignatura);
        if (optionalAsignatura.isEmpty()){
            throw new BadRequestException(STR."No existe una asignatura con el id: \{idAsignatura}");
        }

        //Tomamos el ID de Profesor y verificamos que existe, si no lanzamos excepción
        var optionalProfesor = profesorRepository.findById(idProfesor);
        if (optionalProfesor.isEmpty()){
            throw new BadRequestException(STR."No existe un profesor con el id: \{idProfesor}");
        }

        repositorio.save(toDTO(inscripcion));
        return inscripcion;
    }


    /**
     * Método para eliminar una inscripción por ID
     * @param id de la inscripción que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una inscripción con ese ID
     */
    @Override
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe una inscripción con ese ID, si no lanzamos excepción
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una inscripción con el id: \{id}");
        }

        repositorio.deleteById(id);
    }

    /**
     * Método para obtener una inscripción por ID
     * @param id de la inscripción
     * @return Inscripcion con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     * @throws NotFoundException Si no se encuentra la inscripción con ese ID
     */
    @Override
    public Inscripcion obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una inscripción con el id: \{id}");
        }
         var dto = optional.get();

        var optionalAsignatura = asignaturaRepository.findById(dto.idAsignatura());
        if (optionalAsignatura.isEmpty()){
            throw new NotFoundException(STR."No existe una asignatura con el id: \{dto.idAsignatura()}");
        }

        var optionalProfesor = profesorRepository.findById(dto.idProfesor());
        if (optionalProfesor.isEmpty()){
            throw new NotFoundException(STR."No existe un profesor con el id: \{dto.idProfesor()}");
        }

        return toInscripcion(dto,optionalAsignatura.get(),optionalProfesor.get());
    }

    /**
     * Método para modificar una inscripción
     * @param inscripcion que se va a modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException Si no encuentra inscripción o asignatura o profesor
     */
    @Override
    public void modificar(Inscripcion inscripcion) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(inscripcion.getId());
        if(optional.isEmpty()){
            throw new NotFoundException(STR."No existe una inscripción con el id: \{inscripcion.getId()}");
        }
        var dto = optional.get();

        var optionalAsignatura = asignaturaRepository.findById(dto.idAsignatura());
        if (optionalAsignatura.isEmpty()){
            throw new NotFoundException(STR."No existe una asignatura con el id: \{dto.idAsignatura()}");
        }

        var optionalProfesor = profesorRepository.findById(dto.idProfesor());
        if (optionalProfesor.isEmpty()){
            throw new NotFoundException(STR."No existe un profesor con el id: \{dto.idProfesor()}");
        }

        repositorio.modify(toDTO(inscripcion));
    }


    /**
     * Método para mapear un dto a Inscripción
     * @param dto que queremos mapear
     * @return Inscripción mapeado desde dto
     */
    private Inscripcion toInscripcion(InscripcionDTO dto, Asignatura asignatura, Profesor profesor){
        //Retornamos la inscripción mapeando desde DTO, incluyendo su asignatura y profesor
        return new Inscripcion(
                dto.id(),
                dto.cantidadAlumnos(),
                dto.margenAlumnos(),
                dto.fechaFinInscripcion(),
                asignatura,
                dto.comision(),
                profesor
        );
    }

    /**
     * Método para mapear una Inscripción a dto
     * @param inscripcion que queremos mapear
     * @return InscripciónDTO mapeado desde Inscripción
     */
    private InscripcionDTO toDTO(Inscripcion inscripcion) {
        //Retornamos el DTO mapeado desde InscripciÓn
        return new InscripcionDTO(
                inscripcion.getId(),
                inscripcion.getCantidadAlumnos(),
                inscripcion.getMargenAlumnos(),
                inscripcion.getFechaFinInscripcion(),
                inscripcion.getAsignatura().getId(),
                inscripcion.getComision(),
                inscripcion.getProfesor().getId()
        );
    }
}
