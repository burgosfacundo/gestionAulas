package gestores;

import excepciones.InscripcionNoEncontradaException;
import excepciones.InscripcionYaExisteException;
import interfaces.Service;
import model.Inscripcion;

import java.io.IOException;
import java.util.List;

public class ServicioInscripcion implements Service<Inscripcion> {
    RepositorioInscripcion repositorioInscripcion;

    public ServicioInscripcion() {
        this.repositorioInscripcion = new RepositorioInscripcion();
    }

    @Override
    public void agregar(Inscripcion entidad) throws Exception {
        List<Inscripcion> lista = repositorioInscripcion.listar();
        for (Inscripcion inscripcion : lista) {
            if(inscripcion.getId() == entidad.getId()) {
                throw new InscripcionYaExisteException("La inscripción con ID " + entidad.getId() + " ya existe.");
            }
        }
        repositorioInscripcion.guardar(entidad);
    }

    @Override
    public Inscripcion obtener(int id) throws Exception {
        Inscripcion inscripcion;
        try {
            inscripcion = repositorioInscripcion.leer(id);
        } catch (InscripcionNoEncontradaException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
        return inscripcion;
    }

    @Override
    public void modificar(Inscripcion entidad) throws Exception {
        try {
            repositorioInscripcion.actualizar(entidad);
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
            repositorioInscripcion.eliminar(id);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Inscripcion> obtenerTodos() {
        return repositorioInscripcion.listar();
    }

    public void clear() {
        repositorioInscripcion.clear();
    }
}
