package gestores;

import excepciones.AsignaturaNoEncontradaException;
import excepciones.AsignaturaYaExisteException;
import interfaces.Service;
import model.Asignatura;

import java.util.List;

public class ServicioAsignatura implements Service<Asignatura> {
    RepositorioAsignatura repositorioAsignatura;

    public ServicioAsignatura() {
        this.repositorioAsignatura = new RepositorioAsignatura();
    }

    @Override
    public void agregar(Asignatura entidad) throws Exception {
        List<Asignatura> lista = repositorioAsignatura.listar();
        for (Asignatura a : lista) {
            if(a.getCodigo() == entidad.getCodigo()) {
                throw new AsignaturaYaExisteException("La asignatura con código " + entidad.getCodigo() + " ya existe.");
            }
        }
        repositorioAsignatura.guardar(entidad);
    }

    @Override
    public Asignatura obtener(int id) throws Exception {
        Asignatura asignatura;
        try {
            asignatura = repositorioAsignatura.leer(id);
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
            repositorioAsignatura.actualizar(entidad);
        } catch (AsignaturaNoEncontradaException e) {
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
            repositorioAsignatura.eliminar(id);
        } catch (AsignaturaNoEncontradaException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Asignatura> obtenerTodos() {
        return repositorioAsignatura.listar();
    }

    public void clear() {
        repositorioAsignatura.clear();
    }
}
