package exception;

public class DebitException extends RuntimeException {

    public DebitException() {}

    public DebitException(String message) {
        super(message);
    }
}
