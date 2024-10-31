package org.example.service;

import org.example.exception.BadRequestException;
import org.example.exception.JsonNotFoundException;
import org.example.exception.NotFoundException;

import java.util.List;

public interface Service<ID,T> {
    List<T> listar() throws JsonNotFoundException;
    T guardar(T t) throws JsonNotFoundException, BadRequestException;
    void eliminar(ID id) throws JsonNotFoundException, NotFoundException;
    T obtener(ID id)throws JsonNotFoundException, NotFoundException;
    void modificar(T t)throws JsonNotFoundException, NotFoundException;
}

