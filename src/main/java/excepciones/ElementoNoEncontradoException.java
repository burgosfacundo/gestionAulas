package excepciones;

public class ElementoNoEncontradoException extends Exception{
    private int numeroId;
    public ElementoNoEncontradoException(int numeroId){
        super("No se encontró el elemento con id " + numeroId);
        this.numeroId = numeroId;
    }

    public ElementoNoEncontradoException(String message, int numeroId) {
        super(message);
        this.numeroId = numeroId;
    }

    public int getNumeroId() {
        return numeroId;
    }
}
