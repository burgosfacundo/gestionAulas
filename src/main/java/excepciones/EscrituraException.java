package excepciones;

import java.io.IOException;

public class EscrituraException extends IOException {
    public EscrituraException() {
    }

    public EscrituraException(String message) {
        super(message);
    }
}