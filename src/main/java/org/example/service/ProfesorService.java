package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Profesor;
import org.example.repository.ProfesorRepository;

import java.util.List;

public class ProfesorService {
    private final ProfesorRepository repositorio = new ProfesorRepository();


    /**
     * Método para listar todos los profesores
     * @return List<Profesor>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Profesor> listar() throws JsonNotFoundException {
        return repositorio.getAll();
    }


    /**
     * Método parar guardar un profesor
     * @param profesor que queremos guarde
     * @return Profesor que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException sí existe un profesor con esa matrícula
     */
    public Profesor guardar(Profesor profesor) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe esa asignatura y si existe lanzamos la excepción
        var optional = repositorio.findByMatricula(profesor.getMatricula());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe un profesor con la matrícula: \{profesor.getMatricula()}");
        }

        repositorio.save(profesor);
        return profesor;
    }

    /**
     * Método para eliminar un profesor por ID
     * @param id del profesor que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un profesor con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe un profesor con ese ID y si no lanzamos la excepción
        validarProfesorExistente(id);
        repositorio.deleteById(id);
    }

    /**
     * Método para obtener un profesor por ID
     * @param id del profesor que queremos obtener
     * @return Profesor con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    public Profesor obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos el profesor
        return validarProfesorExistente(id);
    }

    /**
     * Método para modificar un profesor
     * @param profesor modificado
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra al profesor
     */
    public void modificar(Profesor profesor) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el profesor con ese ID exista y lo modificamos
        repositorio.modify(validarProfesorExistente(profesor.getId()));
    }


    // Validaciones

    /**
     * Método para validar la existencia de un Profesor por ID
     * @param id del profesor que se quiere verificar
     * @return Profesor si existe
     * @throws NotFoundException Si no se encuentra el profesor con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Profesor validarProfesorExistente(Integer id) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(id)
                .orElseThrow(()-> new NotFoundException(STR."No existe un profesor con el id: \{id}"));
    }
}
