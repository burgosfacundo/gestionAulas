package excepciones;

public class ReservaYaExisteException extends ElementoYaExisteException{
    public ReservaYaExisteException(String mensaje){
        super(mensaje);
    }
}
