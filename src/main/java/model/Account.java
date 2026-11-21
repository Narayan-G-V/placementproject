package model;

import exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

/**
 * Abstract base class representing a generic Bank Account.
 * Demonstrates Abstraction & Encapsulation OOPS pillars.
 */
public abstract class Account {
    private final String accountNumber;
    private final String accountHolderName;
    private BigDecimal balance;
    private final String pin;

    protected Account(String accountNumber, String accountHolderName, BigDecimal initialBalance, String pin) {
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number is required.");
        this.accountHolderName = Objects.requireNonNull(accountHolderName, "Account holder name is required.");
        this.balance = requireMoney(initialBalance, "Initial balance");
        if (this.balance.signum() < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        if (pin == null || !pin.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("PIN must contain exactly four digits.");
        }
        this.pin = pin;
    }

    /** Returns the normalized account number used by the account registry. */
    public String getAccountNumber() {
        return accountNumber;
    }

    /** Returns the account holder's display name. */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /** Returns the exact decimal balance. */
    public BigDecimal getBalance() {
        return balance;
    }

    /** Checks whether the supplied PIN matches this account's PIN. */
    public boolean validatePin(String inputPin) {
        return pin.equals(inputPin);
    }

    /** Credits a positive, two-decimal-place amount to the account. */
    public void deposit(BigDecimal amount) {
        balance = balance.add(requirePositiveMoney(amount, "Deposit amount"));
    }

    protected final void setBalance(BigDecimal newBalance) {
        balance = requireMoney(newBalance, "Balance");
    }

    protected static BigDecimal requireMoney(BigDecimal amount, String label) {
        Objects.requireNonNull(amount, label + " is required.");
        if (amount.scale() > 2) {
            throw new IllegalArgumentException(label + " must have no more than two decimal places.");
        }
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    protected static BigDecimal requirePositiveMoney(BigDecimal amount, String label) {
        BigDecimal value = requireMoney(amount, label);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
        return value;
    }

    /** Debits an amount when the concrete account's balance rules allow it. */
    public abstract void withdraw(BigDecimal amount) throws InsufficientBalanceException;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Acc No: %s | Holder: %s | Balance: $%.2f",
                accountNumber, accountHolderName, balance);
    }
}
