package excepciones;

public class AulaNoEncontradaException extends ElementoNoEncontradoException{
    public AulaNoEncontradaException(int numeroId){
        super(numeroId);
    }

    public AulaNoEncontradaException(String message, int numeroId) {
        super(message, numeroId);
    }
}
