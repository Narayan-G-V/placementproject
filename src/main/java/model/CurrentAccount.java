package model;

import exception.InsufficientBalanceException;

import java.math.BigDecimal;

/**
 * Current Account implementation with Overdraft facility.
 * Demonstrates Inheritance and Polymorphism.
 */
public class CurrentAccount extends Account {
    private final BigDecimal overdraftLimit;

    public CurrentAccount(String accountNumber, String accountHolderName, BigDecimal initialBalance,
            BigDecimal overdraftLimit, String pin) {
        super(accountNumber, accountHolderName, initialBalance, pin);
        this.overdraftLimit = requireMoney(overdraftLimit, "Overdraft limit");
        if (this.overdraftLimit.signum() < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        }
    }

    @Override
    public void withdraw(BigDecimal amount) throws InsufficientBalanceException {
        BigDecimal value = requirePositiveMoney(amount, "Withdrawal amount");
        if (getBalance().add(overdraftLimit).compareTo(value) < 0) {
            throw new InsufficientBalanceException(
                "Transaction failed! Exceeds available balance and overdraft limit of $" + overdraftLimit
            );
        }
        setBalance(getBalance().subtract(value));
    }

    @Override
    public String toString() {
        return "[Current] " + super.toString() + " | Overdraft Limit: $" + overdraftLimit;
    }
}
