package exception;

/**
 * Custom exception thrown when an account lookup fails.
 */
public class AccountNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
