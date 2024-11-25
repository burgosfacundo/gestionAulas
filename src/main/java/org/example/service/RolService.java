package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Rol;
import org.example.repository.RolRepository;

import java.util.List;

/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular roles
 */
public class RolService{
    private final RolRepository repositorio = new RolRepository();

    /**
     * Método para listar todos los roles
     * @return List<Rol>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Rol> listar() throws JsonNotFoundException {
        return repositorio.getAll();
    }


    /**
     * Método parar guardar un rol
     * @param rol que queremos guarde
     * @return Rol que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un rol con ese nombre
     */
    public Rol guardar(Rol rol) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe ese rol y si existe lanzamos la excepción
        var optional = repositorio.findByNombre(rol.getNombre());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe un rol: \{rol.getNombre()}");
        }

        repositorio.save(rol);
        return rol;
    }

    /**
     * Método para eliminar un rol por ID
     * @param id del rol que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe un rol con ese ID y si no lanzamos la excepción
        validarRolExistente(id);

        //Borramos el rol con ese ID
        repositorio.deleteById(id);
    }

    /**
     * Método para obtener un rol por ID
     * @param id del rol que queremos obtener
     * @return Rol con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    public Rol obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        // Validamos y retornamos el rol
        return validarRolExistente(id);
    }

    /**
     * Método para modificar un rol
     * @param rol modificado
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra el rol
     */
    public void modificar(Rol rol) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el rol con ese ID exista
        validarRolExistente(rol.getId());

        //Lo modificamos
        repositorio.modify(rol);
    }


    // Validaciones

    /**
     * Método para validar que exista un rol con ese ID
     * @param idRol del rol a validar
     * @return Rol si existe el rol
     * @throws NotFoundException si no existe rol con ese ID
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    private Rol validarRolExistente(Integer idRol) throws NotFoundException, JsonNotFoundException {
        return repositorio.findById(idRol)
                .orElseThrow(()-> new NotFoundException(STR."No existe un rol con el id: \{idRol}"));
    }
}
