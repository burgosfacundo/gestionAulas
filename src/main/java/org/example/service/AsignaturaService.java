package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Asignatura;
import org.example.repository.AsignaturaRepository;

import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular asignaturas
 */
public class AsignaturaService implements Service<Integer,Asignatura> {
    AsignaturaRepository repositorio = new AsignaturaRepository();


    /**
     * Método para listar todas las asignaturas
     * @return List<Asignatura>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Asignatura> listar() throws JsonNotFoundException {
        return repositorio.getAll();
    }


    /**
     * Método parar guardar una asignatura
     * @param asignatura que queremos guarde
     * @return Asignatura que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException sí existe una asignatura con ese código
     */
    @Override
    public Asignatura guardar(Asignatura asignatura) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe esa asignatura y si existe lanzamos la excepción
        var optional = repositorio.findByCodigo(asignatura.getCodigo());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe una asignatura con el código: \{asignatura.getCodigo()}");
        }

        repositorio.save(asignatura);
        return asignatura;
    }

    /**
     * Método para eliminar una asignatura por ID
     * @param id de la asignatura que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    @Override
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una asignatura con el id: \{id}");
        }

        repositorio.deleteById(id);
    }

    /**
     * Método para obtener una asignatura por ID
     * @param id de la asignatura que queremos obtener
     * @return Aula con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    @Override
    public Asignatura obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una asignatura con el id: \{id}");
        }
        return optional.get();
    }

    /**
     * Método para modificar una asignatura
     * @param asignatura modificado
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra la asignatura
     */
    @Override
    public void modificar(Asignatura asignatura) throws JsonNotFoundException, NotFoundException {
        //Verificamos que la asignatura con ese ID exista
        var optional = repositorio.findById(asignatura.getId());
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe una asignatura con el id: \{asignatura.getId()}");
        }

        //la modificamos
        repositorio.modify(asignatura);
    }
}
