package org.example.service;


import org.example.model.Rol;
import org.example.model.dto.UsuarioDTO;
import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;
import org.example.model.Usuario;
import org.example.repository.RolRepository;
import org.example.repository.UsuarioRepository;
import org.example.utils.Mapper;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase que se encarga de comunicarse con el repositorio
 * y aplicar la lógica de negocio para manipular usuarios
 */
public class UsuarioService{
    private final UsuarioRepository repositorioUsuario = new UsuarioRepository();
    private final RolRepository repositorioRol = new RolRepository();


    /**
     * Método para getAll todos los usuarios
     * @return List<Usuario>
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public List<Usuario> listar() throws JsonNotFoundException {
        List<Usuario> usuarios = new ArrayList<>();
        var dtoList = repositorioUsuario.getAll();
        for (UsuarioDTO dto : dtoList){
            var rolOptional = repositorioRol.findById(dto.idRol());
            rolOptional.ifPresent(rol -> usuarios.add(Mapper.toUsuario(dto, rol)));
        }
        return usuarios;
    }


    /**
     * Método para save un usuario
     * @param usuario que queremos save
     * @return Usuario que se guarda
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws BadRequestException si existe un usuario con ese username
     */
    public Usuario guardar(Usuario usuario) throws JsonNotFoundException, NotFoundException, BadRequestException {
        //Verificamos que no existe un usuario con ese username, sino lanzamos excepción
        var optional = repositorioUsuario.findByUsername(usuario.getUsername());
        if (optional.isPresent()){
            throw new BadRequestException(STR."Ya existe un usuario con el nombre de usuario: \{usuario}");
        }

        //Tomamos el ID del rol y verificamos que existe, si no lanzamos excepción
        validarRolExistente(usuario.getRol().getId());

        // Codifica la contraseña usando bcrypt
        usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));


        repositorioUsuario.save(Mapper.usuarioToDto(usuario));
        return usuario;
    }


    /**
     * Método para eliminar un usuario por ID
     * @param id del usuario que queremos eliminar
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     * @throws NotFoundException si no se encuentra un usuario con ese id
     */
    public void eliminar(Integer id) throws JsonNotFoundException, NotFoundException {
        //Verificamos que existe un usuario con ese ID, si no lanzamos excepción
        validarUsuarioExistente(id);

        repositorioUsuario.deleteById(id);
    }

    /**
     * Método para obtener Usuario por ID
     * @param id del usuario a obtener
     * @return Usuario con el ID del parámetro
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario con ese ID
     */
    public Usuario obtener(Integer id) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista el usuario
        var dto = validarUsuarioExistente(id);

        //Validamos que exista el rol del usuario
        var rol = validarRolExistente(dto.idRol());

        //Mapeamos y retornamos el usuario con su rol
        return Mapper.toUsuario(dto,rol);
    }

    /**
     * Método para modificar un usuario
     * @param usuario que se quiere modificar
     * @throws JsonNotFoundException si ocurre un error con el archivo JSON
     * @throws NotFoundException si no encuentra el usuario que se quiere modificar
     */
    public void modificar(Usuario usuario) throws JsonNotFoundException, NotFoundException {
        //Validamos que exista el usuario
        validarUsuarioExistente(usuario.getId());

        //Validamos que exista su rol
        validarRolExistente(usuario.getRol().getId());

        //Mapeamos y modificamos el usuario
        repositorioUsuario.modify(Mapper.usuarioToDto(usuario));
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
        return repositorioRol.findById(idRol)
                .orElseThrow(()-> new NotFoundException(STR."No existe un rol con el id: \{idRol}"));
    }

    /**
     * Método para validar que exista un usuario con ese ID
     * @param idUsuario del usuario a validar
     * @return UsuarioDTO si existe el usuario
     * @throws NotFoundException si no existe usuario con ese ID
     * @throws JsonNotFoundException sí existe un problema con el archivo JSON
     */
    private UsuarioDTO validarUsuarioExistente(Integer idUsuario) throws NotFoundException, JsonNotFoundException {
        return repositorioUsuario.findById(idUsuario)
                .orElseThrow(()-> new NotFoundException(STR."No existe un usuario con el id: \{idUsuario}"));
    }
}
