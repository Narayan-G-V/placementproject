package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Class representing an individual transaction record.
 */
public class Transaction {
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    /** Creates a transaction record with the current local time. */
    public Transaction(String type, BigDecimal amount) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction type is required.");
        }
        Objects.requireNonNull(amount, "Transaction amount is required.");
        if (amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Transaction amount must be positive with at most two decimal places.");
        }
        this.type = type.trim();
        this.amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s] %-24s : $%.2f",
                timestamp.format(TIMESTAMP_FORMAT), type, amount);
    }
}
