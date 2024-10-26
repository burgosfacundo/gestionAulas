package excepciones;

public class InscripcionYaExisteException extends Exception {
    public InscripcionYaExisteException(String mensaje) {
        super(mensaje);
    }
}
