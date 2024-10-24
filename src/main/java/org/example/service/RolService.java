package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Rol;
import org.example.repository.RolRepository;

import java.util.List;

/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la logica de negocio para manipular roles
 */
public class RolService {
    private final RolRepository repositorioRol = new RolRepository();

    /**
     * Metodo para getAll todos los roles
     * @return List<Rol>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Rol> listar() throws JsonNotFoundException {
        return repositorioRol.getAll();
    }


    /**
     * Metodo para save un rol
     * @param rol que queremos save
     * @return Rol que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un rol con ese nombre
     */
    public Rol guardar(Rol rol) throws JsonNotFoundException, BadRequestException {
        // Verificamos que no existe ese rol y si existe lanzamos la excepcion
        var optional = repositorioRol.findByNombre(rol.getNombre());
        if (optional != null){
            throw new BadRequestException(STR."Ya existe un rol: \{rol.getNombre()}");
        }

        repositorioRol.save(rol);
        return rol;
    }

    /**
     * Metodo para eliminar un rol por id
     * @param id del rol que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un rol con ese id
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        // Verificamos que existe un rol con ese id y sino lanzamos la excepcion
        var optional = repositorioRol.findById(id);
        if (optional == null){
            throw new NotFoundException(STR."No existe un rol con el id: \{id}");
        }

        repositorioRol.deleteById(id);
    }

}
