package exception;

/**
 * Custom exception thrown for illegal amounts or invalid operation parameters.
 */
public class InvalidTransactionException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidTransactionException(String message) {
        super(message);
    }
}
