package org.example.service;

import excepciones.AsignaturaNoEncontradaException;
import excepciones.AsignaturaYaExisteException;
import org.example.model.Asignatura;
import org.example.repository.AsignaturaRepository;

import java.io.IOException;
import java.util.List;

public class ServicioAsignatura implements Service<Asignatura> {
    AsignaturaRepository asignaturaRepository;

    public ServicioAsignatura() {
        this.asignaturaRepository = new AsignaturaRepository();
    }


    @Override
    public void agregar(Asignatura entidad) throws Exception {
        List<Asignatura> lista = asignaturaRepository.listar();
        for (Asignatura a : lista) {
            if(a.getCodigo() == entidad.getCodigo()) {
                throw new AsignaturaYaExisteException("La asignatura con código " + entidad.getCodigo() + " ya existe.");
            }
        }
        asignaturaRepository.guardar(entidad);
    }

    @Override
    public Asignatura obtener(int id) throws Exception {
        Asignatura asignatura;
        try {
            asignatura = asignaturaRepository.leer(id);
        } catch (AsignaturaNoEncontradaException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
        return asignatura;
    }

    @Override
    public void modificar(Asignatura entidad) throws Exception {
        try {
            asignaturaRepository.actualizar(entidad);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void eliminar(int id) throws Exception {
        try {
            asignaturaRepository.eliminar(id);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Asignatura> obtenerTodos() {
        return asignaturaRepository.listar();
    }

    public void clear() {
        asignaturaRepository.clear();
    }
}
