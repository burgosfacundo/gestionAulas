package excepciones;

public class AulaYaExisteException extends ElementoYaExisteException{
    public AulaYaExisteException(String mensaje){
        super(mensaje);
    }
}
