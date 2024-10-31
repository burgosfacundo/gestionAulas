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
public class RolService implements Service<Integer,Rol>{
    private final RolRepository repositorio = new RolRepository();

    /**
     * Método para listar todos los roles
     * @return List<Rol>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
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
    @Override
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
    @Override
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe un rol con ese ID y si no lanzamos la excepción
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe un rol con el id: \{id}");
        }

        repositorio.deleteById(id);
    }

    /**
     * Método para obtener un rol por ID
     * @param id del rol que queremos obtener
     * @return Rol con ese ID
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un rol con ese ID
     */
    @Override
    public Rol obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        var optional = repositorio.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe un rol con el id: \{id}");
        }
        return optional.get();
    }

    /**
     * Método para modificar un rol
     * @param rol modificado
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     * @throws NotFoundException si no encuentra el rol
     */
    @Override
    public void modificar(Rol rol) throws JsonNotFoundException, NotFoundException {
        //Verificamos que el rol con ese ID exista
        var optional = repositorio.findById(rol.getId());
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe un rol con el id: \{rol.getId()}");
        }

        //Lo modificamos
        repositorio.modify(rol);
    }
}
