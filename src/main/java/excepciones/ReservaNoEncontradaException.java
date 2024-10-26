package excepciones;

public class ReservaNoEncontradaException extends ElementoNoEncontradoException{
    public ReservaNoEncontradaException(String mensaje){
        super(mensaje);
    }
}
