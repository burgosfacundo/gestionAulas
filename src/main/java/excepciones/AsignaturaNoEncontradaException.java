package excepciones;

public class AsignaturaNoEncontradaException extends ElementoNoEncontradoException {
    public AsignaturaNoEncontradaException(int numeroId) {
        super(numeroId);
    }

    public AsignaturaNoEncontradaException(String message, int numeroId) {
        super(message, numeroId);
    }

}
