package service;

import exception.AccountNotFoundException;
import exception.InvalidTransactionException;
import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import util.SystemConstants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Handles account creation, storage, and retrieval using Java Collections Framework (HashMap).
 */
public class AccountService {
    private final Map<String, Account> accounts = new HashMap<>();

    /** Validates and registers a savings account with its required minimum opening balance. */
    public SavingsAccount createSavingsAccount(String accNum, String name, BigDecimal initialBalance, String pin)
            throws InvalidTransactionException {
        validateNewAccount(accNum, name, initialBalance, pin);
        if (initialBalance.compareTo(SystemConstants.MINIMUM_SAVINGS_BALANCE) < 0) {
            throw new InvalidTransactionException("Savings accounts require an initial balance of at least $"
                    + SystemConstants.MINIMUM_SAVINGS_BALANCE + ".");
        }
        String accountNumber = normalizeAccountNumber(accNum);
        ensureAccountNumberAvailable(accountNumber);
        SavingsAccount account = new SavingsAccount(accountNumber, name.trim(), initialBalance, pin);
        accounts.put(accountNumber, account);
        return account;
    }

    /** Validates and registers a current account and its overdraft limit. */
    public CurrentAccount createCurrentAccount(String accNum, String name, BigDecimal initialBalance,
            BigDecimal overdraftLimit, String pin) throws InvalidTransactionException {
        validateNewAccount(accNum, name, initialBalance, pin);
        validateMoney(overdraftLimit, "Overdraft limit");
        String accountNumber = normalizeAccountNumber(accNum);
        ensureAccountNumberAvailable(accountNumber);
        CurrentAccount account = new CurrentAccount(accountNumber, name.trim(), initialBalance, overdraftLimit, pin);
        accounts.put(accountNumber, account);
        return account;
    }

    /** Looks up an account without regard to account-number letter case. */
    public Account getAccount(String accNum) throws AccountNotFoundException {
        String accountNumber = normalizeAccountNumber(accNum);
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account number '" + accountNumber + "' does not exist.");
        }
        return account;
    }

    private void validateNewAccount(String accountNumber, String name, BigDecimal initialBalance, String pin)
            throws InvalidTransactionException {
        if (accountNumber == null || !accountNumber.trim().matches("[A-Za-z0-9-]{3,20}")) {
            throw new InvalidTransactionException(
                    "Account number must contain 3 to 20 letters, digits, or hyphens.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidTransactionException("Account holder name cannot be empty.");
        }
        validateMoney(initialBalance, "Initial balance");
        if (pin == null || !pin.matches("[0-9]{4}")) {
            throw new InvalidTransactionException("PIN must contain exactly four digits.");
        }
    }

    private void ensureAccountNumberAvailable(String accountNumber) throws InvalidTransactionException {
        if (accounts.containsKey(accountNumber)) {
            throw new InvalidTransactionException("Account number '" + accountNumber + "' is already in use.");
        }
    }

    private static void validateMoney(BigDecimal amount, String label)
            throws InvalidTransactionException {
        if (amount == null || amount.scale() > 2 || amount.signum() < 0) {
            throw new InvalidTransactionException(label
                    + " must be zero or greater and have no more than two decimal places.");
        }
    }

    private static String normalizeAccountNumber(String accountNumber) {
        return accountNumber == null ? "" : accountNumber.trim().toUpperCase(Locale.ROOT);
    }
}
