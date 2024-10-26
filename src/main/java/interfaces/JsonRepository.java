package interfaces;

import java.util.List;

public interface JsonRepository<T> {
    void guardar(T entidad) throws Exception;
    T leer(int id) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(int id) throws Exception;
    List<T> listar();
    void clear();
}
