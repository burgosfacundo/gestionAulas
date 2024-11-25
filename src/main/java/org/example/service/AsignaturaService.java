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
public class AsignaturaService{
    private final AsignaturaRepository repositorio = new AsignaturaRepository();


    /**
     * Método para listar todas las asignaturas
     * @return List<Asignatura>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
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
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe una asignatura con ese ID y si no lanzamos la excepción
        validarAsignaturaExistente(id);
        repositorio.deleteById(id);
    }

    /**
     * Método para obtener una asignatura por ID
     * @param id de la asignatura que queremos obtener
     * @return Asignatura con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra una asignatura con ese ID
     */
    public Asignatura obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos la asignatura
        return validarAsignaturaExistente(id);
    }

    /**
     * Método para modificar una asignatura
     * @param asignatura modificado
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra la asignatura
     */
    public void modificar(Asignatura asignatura) throws JsonNotFoundException, NotFoundException {
        //Verificamos que la asignatura con ese ID exista y lo modificamos
        repositorio.modify(validarAsignaturaExistente(asignatura.getId()));
    }


    // Validaciones

    /**
     * Método para validar la existencia de una Asignatura por ID
     * @param idAsignatura de la asignatura que se quiere verificar
     * @return Asignatura si existe
     * @throws NotFoundException Si no se encuentra la asignatura con ese ID
     * @throws JsonNotFoundException Sí ocurre un error con el archivo JSON
     */
    private Asignatura validarAsignaturaExistente(Integer idAsignatura) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(idAsignatura)
                .orElseThrow(()-> new NotFoundException(STR."No existe una asignatura con el id: \{idAsignatura}"));
    }
}
