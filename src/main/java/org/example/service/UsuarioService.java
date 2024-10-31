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
 * y aplicar la lógica de negocio para manipular usuarios
 */
public class UsuarioService implements Service<Integer,Usuario>{
    private final UsuarioRepository repositorioUsuario = new UsuarioRepository();
    private final RolRepository repositorioRol = new RolRepository();


    /**
     * Método para getAll todos los usuarios
     * @return List<Usuario>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<Usuario> listar() throws JsonNotFoundException {
        List<Usuario> usuarios = new ArrayList<>();
        var dtoList = repositorioUsuario.getAll();
        for (UsuarioDTO dto : dtoList){
            var rolOptional = repositorioRol.findById(dto.idRol());
            rolOptional.ifPresent(rol -> usuarios.add(toUsuario(dto, rol)));
        }
        return usuarios;
    }


    /**
     * Método para save un usuario
     * @param usuario que queremos save
     * @return Usuario que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si el usuario tiene un id de rol que no existe
     * @throws BadRequestException si existe un usuario con ese username
     */
    @Override
    public Usuario guardar(Usuario usuario) throws JsonNotFoundException, BadRequestException {
        //Verificamos que no existe un usuario con ese username, sino lanzamos excepción
        var optional = repositorioUsuario.findByUsername(usuario.getUsername());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe un usuario con el nombre de usuario: \{usuario}");
        }

        //Tomamos el ID del rol y verificamos que existe, si no lanzamos excepción
        var idRol = usuario.getRol().getId();
        var optionalRol = repositorioRol.findById(idRol);
        if (optionalRol.isEmpty()){
            throw new BadRequestException(STR."No existe un rol con el id: \{idRol}");
        }

        repositorioUsuario.save(toDTO(usuario));
        return usuario;
    }


    /**
     * Método para eliminar un usuario por ID
     * @param id del usuario que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    @Override
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe un usuario con ese ID, si no lanzamos excepción
        var optional = repositorioUsuario.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe un usuario con el id: \{id}");
        }

        repositorioUsuario.deleteById(id);
    }

    /**
     * Método para obtener Usuario por ID
     * @param id del usuario a obtener
     * @return Usuario con el ID del parámetro
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario con ese ID
     */
    @Override
    public Usuario obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        var optional = repositorioUsuario.findById(id);
        if (optional.isEmpty()){
            throw new NotFoundException(STR."No existe un usuario con el id: \{id}");
        }
        var dto = optional.get();

        var optionalRol = repositorioRol.findById(dto.idRol());
        if (optionalRol.isEmpty()){
            throw new NotFoundException(STR."No existe un rol con el id: \{id}");
        }
        var rol = optionalRol.get();

        return toUsuario(dto,rol);
    }

    /**
     * Método para modificar un usuario
     * @param usuario que se quiere modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario que se quiere modificar
     */
    @Override
    public void modificar(Usuario usuario) throws JsonNotFoundException, NotFoundException {
        var dto = repositorioUsuario.findById(usuario.getId());
        if(dto.isEmpty()){
            throw new NotFoundException(STR."No existe un usuario con el id: \{usuario.getId()}");
        }
        var rol = repositorioRol.findById(usuario.getRol().getId());

        if(rol.isEmpty()){
            throw new NotFoundException("No existe ese rol");
        }
        
        repositorioUsuario.save(toDTO(usuario));
    }

    /**
     * Método para mapear un dto a Usuario
     * @param dto que queremos mapear
     * @return Usuario mapeado desde dto
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
     * Método para mapear un Usuario a dto
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
