package util;

import java.math.BigDecimal;

/**
 * Centralized constant parameters.
 */
public final class SystemConstants {
    public static final BigDecimal MINIMUM_SAVINGS_BALANCE = new BigDecimal("500.00");
    public static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.04");
    public static final int TRANSACTION_HISTORY_LIMIT = 5;

    private SystemConstants() {
    }
}
