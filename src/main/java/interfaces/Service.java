package interfaces;

import java.util.List;

public interface Service<T> {
    void agregar(T entidad) throws Exception;
    T obtener(int id) throws Exception;
    void modificar(T entidad) throws Exception;
    void eliminar(int id) throws Exception;
    List<T> obtenerTodos();
    void clear();
}

