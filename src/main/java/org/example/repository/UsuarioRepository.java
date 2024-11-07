package org.example.repository;

import com.google.gson.JsonArray;
import org.example.model.dto.UsuarioDTO;
import org.example.exception.JsonNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;

/**
 * Repositorio de usuario
 * Implementa JSONRepository
 * Su responsabilidad es interactuar con el JSON
 */
public class UsuarioRepository implements JSONRepository<Integer, UsuarioDTO> {
    private final String ruta = "./json/usuarios.json";

    /**
     * Método para retornar la ruta al json
     * que se quiere utilizar en el método default
     * de la interfaz
     * @return la ruta al JSON
     */
    @Override
    public String getRuta() {
        return ruta;
    }

    /**
     * Método que guarda un nuevo usuario en el JSON llamando al método write
     * @param dto el nuevo usuario que se guarda en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void save(UsuarioDTO dto) throws JsonNotFoundException {
        // Listamos todos los usuarios (DTO)
        var dtos = getAll();

        // Verificamos el último ID y generamos el próximo
        var lastId = dtos.isEmpty() ? 0 : dtos.getLast().id();
        dto = new UsuarioDTO(lastId + 1, dto.username(), dto.password(), dto.idRol());

        // Agregamos el nuevo usuario
        dtos.add(dto);

        // Convertimos la lista a JSON y la escribimos en el archivo
        var jsonArray = new JsonArray();
        dtos.forEach(d -> jsonArray.add(gson.toJsonTree(d)));
        write(jsonArray);
    }

    /**
     * Método que devuelve la lista de usuarios que tenemos en el JSON
     * @return List<UsuarioDTO> lista de los DTO de usuarios en el JSON
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public List<UsuarioDTO> getAll() throws JsonNotFoundException {
        try (FileReader reader = new FileReader(ruta)) {
            //Usamos UsuarioDTO porque guardamos solo el ID del Rol que corresponde al usuario en este json
            var usuarioListType = new TypeToken<List<UsuarioDTO>>() {}.getType();
            return gson.fromJson(reader, usuarioListType);
        } catch (IOException e) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
    }


    /**
     * Método para buscar un usuario por id
     * @param id para buscar y devolver el usuario
     * @return Optional<UsuarioDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public Optional<UsuarioDTO> findById(Integer id) throws JsonNotFoundException {
        //Usamos stream para filtrar por id
        //Devuelve el usuario si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> dto.id() == id)
                .findFirst();
    }


    /**
     * Método para borrar un usuario por ID
     * @param id para buscar y borrar el usuario
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    @Override
    public void deleteById(Integer id) throws JsonNotFoundException {
        //Traemos todos los usuarios y borramos el que tenga ese id
        var usuarios = getAll();
        usuarios.removeIf(dto -> dto.id() == id);

        //guardamos la lista sin el usuario con ese id
        var jsonArray = new JsonArray();
        usuarios.forEach(dto -> jsonArray.add(gson.toJsonTree(dto)));
        write(jsonArray);
    }

    /**
     * Método para modificar un usuario
     * @param dto que queremos modificar
     * @throws JsonNotFoundException si no encuentra el archivo JSON
     */
    @Override
    public void modify(UsuarioDTO dto) throws JsonNotFoundException {
        // Obtén todos los usuarios del JSON
        var dtos = getAll();

        // Busca el índice del usuario que deseas modificar
        int index = -1;
        for (int i = 0; i < dtos.size(); i++) {
            if (dtos.get(i).id() == dto.id()) {
                index = i;
                break;
            }
        }
        // Si no se encuentra el índice, lanza la excepción
        if (index == -1) {
            throw new JsonNotFoundException(STR."No se encontró el archivo JSON: \{ruta}");
        }
        // Reemplaza el objeto en la lista con una nueva instancia actualizada
        var nuevoDTO = new UsuarioDTO(dto.id(),dto.username(),dto.password(),dto.idRol());
        dtos.set(index, nuevoDTO);

        // Crea el array JSON actualizado
        var jsonArray = new JsonArray();
        dtos.forEach(d -> jsonArray.add(gson.toJsonTree(d)));

        // Guarda los cambios en el archivo JSON
        write(jsonArray);
    }

    /**
     * Método para buscar un usuario por username
     * @param username para buscar y devolver el usuario
     * @return Optional<UsuarioDTO> el DTO si lo encuentra u Optional.empty() si no
     * @throws JsonNotFoundException si no se encuentra el archivo JSON
     */
    public Optional<UsuarioDTO> findByUsername(String username) throws JsonNotFoundException {
        //Usamos stream para filtrar por username
        //Devuelve el usuario si existe
        //Devuelve optional.empty() sino
        return getAll().stream()
                .filter(dto -> dto.username().equals(username))
                .findFirst();
    }
}

