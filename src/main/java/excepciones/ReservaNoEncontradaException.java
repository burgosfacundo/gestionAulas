package excepciones;

public class ReservaNoEncontradaException extends ElementoNoEncontradoException{
    public ReservaNoEncontradaException(int numeroId) {
        super(numeroId);
    }

    public ReservaNoEncontradaException(String message, int numeroId) {
        super(message, numeroId);
    }
}
