package excepciones;

public class InscripcionNoEncontradaException extends ElementoNoEncontradoException {
    public InscripcionNoEncontradaException(int numeroId) {
        super(numeroId);
    }

    public InscripcionNoEncontradaException(String message, int numeroId) {
        super(message, numeroId);
    }
}
