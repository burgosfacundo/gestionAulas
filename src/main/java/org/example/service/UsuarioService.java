package org.example.service;


import org.example.model.Rol;
import org.example.model.dto.UsuarioDTO;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.repository.RolRepository;
import org.example.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la logica de negocio para manipular usuarios
 */
public class UsuarioService implements Service<Usuario>{
    private final UsuarioRepository repositorioUsuario = new UsuarioRepository();
    private final RolRepository repositorioRol = new RolRepository();


    /**
     * Metodo para getAll todos los usuarios
     * @return List<Usuario>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Usuario> listar() throws JsonNotFoundException {
        List<Usuario> usuarios = new ArrayList<>();
        var dtos = repositorioUsuario.getAll();
        for (UsuarioDTO dto : dtos){
            var rol = repositorioRol.findById(dto.idRol());
            usuarios.add(toUsuario(dto,rol));
        }
        return usuarios;
    }


    /**
     * Metodo para save un usuario
     * @param usuario que queremos save
     * @return Usuario que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si el usuario tiene un id de rol que no existe
     * @throws BadRequestException si existe un usuario con ese username
     */
    public Usuario guardar(Usuario usuario) throws JsonNotFoundException, NotFoundException, BadRequestException {
        //Verificamos que no existe un usuario con ese username, sino lanzamos excepcion
        var optional = repositorioUsuario.findByUsername(usuario.getUsername());
        if (optional != null){
            throw new BadRequestException(STR."Ya existe un usuario con el nombre de usuario: \{usuario}");
        }

        //Tomamos el id del rol y verificamos que existe, sino lanzamos excepcion
        var idRol = usuario.getRol().getId();
        var optionalRol = repositorioRol.findById(idRol);
        if (optionalRol == null){
            throw new NotFoundException(STR."No existe un rol con el id: \{idRol}");
        }

        repositorioUsuario.save(toDTO(usuario));
        return usuario;
    }


    /**
     * Metodo para eliminar un usuario por id
     * @param id del usuario que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe un usuario con ese id, sino lanzamos excepcion
        var optional = repositorioUsuario.findById(id);
        if (optional == null){
            throw new NotFoundException(STR."No existe un usuario con el id: \{id}");
        }

        repositorioUsuario.deleteById(id);
    }

    @Override
    public Usuario obtener(int id) throws JsonNotFoundException, NotFoundException {
        var dto = repositorioUsuario.findById(id);
        if (dto == null){
            throw new NotFoundException(STR."No existe un usuario con el id: \{id}");
        }

        var rol = repositorioRol.findById(dto.idRol());
        if (rol == null){
            throw new NotFoundException(STR."No existe un rol con el id: \{id}");
        }

        return toUsuario(dto,rol);
    }

    @Override
    public void modificar(Usuario usuario) throws JsonNotFoundException, NotFoundException {
        var dto = repositorioUsuario.findById(usuario.getId());
        if(dto == null){
            throw new NotFoundException(STR."No existe un usuario con el id: \{usuario.getId()}");
        }
        var rol = repositorioRol.findById(usuario.getRol().getId());

        if(rol == null){
            throw new NotFoundException("No existe ese rol");
        }
        
        repositorioUsuario.save(toDTO(usuario));
    }

    /**
     * Metodo para mapear un dto a Usuario
     * @param dto que queremos mapear
     * @return Usuario mapeado desde dto
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    private Usuario toUsuario(UsuarioDTO dto, Rol rol){
        //Retornamos el usuario mapeado desde dto, incluyendo su rol
        return new Usuario(
                dto.id(),
                dto.username(),
                dto.password(),
                rol
        );
    }

    /**
     * Metodo para mapear un Usuario a dto
     * @param usuario que queremos mapear
     * @return UsuarioDTO mapeado desde Usuario
     */
    private UsuarioDTO toDTO(Usuario usuario) {
        //Retornamos el dto mapeado desde Usuario
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol().getId()
        );
    }
}
