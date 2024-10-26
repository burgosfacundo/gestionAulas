package excepciones;

public class AsignaturaYaExisteException extends Exception {
    public AsignaturaYaExisteException(String mensaje) {
        super(mensaje);
    }
}
