package model;

import exception.InsufficientBalanceException;
import util.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Savings Account implementation with minimum balance enforcement.
 * Demonstrates Inheritance and Polymorphism.
 */
public class SavingsAccount extends Account {
    private final BigDecimal interestRate;

    public SavingsAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance, String pin) {
        super(accountNumber, accountHolderName, initialBalance, pin);
        if (requireMoney(initialBalance, "Initial balance")
                .compareTo(SystemConstants.MINIMUM_SAVINGS_BALANCE) < 0) {
            throw new IllegalArgumentException("Savings accounts require an initial balance of at least $"
                    + SystemConstants.MINIMUM_SAVINGS_BALANCE + ".");
        }
        this.interestRate = SystemConstants.DEFAULT_INTEREST_RATE;
    }

    @Override
    public void withdraw(BigDecimal amount) throws InsufficientBalanceException {
        BigDecimal value = requirePositiveMoney(amount, "Withdrawal amount");
        if (getBalance().subtract(value).compareTo(SystemConstants.MINIMUM_SAVINGS_BALANCE) < 0) {
            throw new InsufficientBalanceException(
                "Transaction failed! Must maintain minimum balance of $"
                        + SystemConstants.MINIMUM_SAVINGS_BALANCE
            );
        }
        setBalance(getBalance().subtract(value));
    }

    /** Adds one period of interest, rounded to the nearest cent. */
    public void applyInterest() {
        BigDecimal interest = getBalance().multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
        setBalance(getBalance().add(interest));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[Savings] %s | Interest Rate: %.0f%%",
                super.toString(), interestRate.multiply(new BigDecimal("100")));
    }
}
