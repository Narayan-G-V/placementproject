package exception;

/**
 * Custom exception thrown when account funds/overdraft cannot cover a transaction.
 */
public class InsufficientBalanceException extends Exception {
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
