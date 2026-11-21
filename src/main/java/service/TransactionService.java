package service;

import exception.AccountNotFoundException;
import exception.InsufficientBalanceException;
import exception.InvalidTransactionException;
import model.Account;

import java.math.BigDecimal;

/**
 * Executes core business logic for deposits, withdrawals, and transfers.
 */
public class TransactionService {
    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    /** Validates and credits a deposit. */
    public void deposit(String accNum, BigDecimal amount)
            throws AccountNotFoundException, InvalidTransactionException {
        validateAmount(amount, "Deposit");
        Account account = accountService.getAccount(accNum);
        account.deposit(amount);
    }

    /** Validates and debits a withdrawal according to the account's rules. */
    public void withdraw(String accNum, BigDecimal amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidTransactionException {
        validateAmount(amount, "Withdrawal");
        Account account = accountService.getAccount(accNum);
        account.withdraw(amount);
    }

    /** Transfers funds only after both accounts and the amount have been validated. */
    public void transfer(String srcAccNum, String destAccNum, BigDecimal amount)
            throws AccountNotFoundException, InsufficientBalanceException, InvalidTransactionException {
        validateAmount(amount, "Transfer");
        if (srcAccNum != null && destAccNum != null
                && srcAccNum.trim().equalsIgnoreCase(destAccNum.trim())) {
            throw new InvalidTransactionException("Source and destination accounts cannot be the same.");
        }
        Account source = accountService.getAccount(srcAccNum);
        Account destination = accountService.getAccount(destAccNum);
        // Resolve both accounts before changing either balance so an invalid destination cannot lose funds.
        source.withdraw(amount);
        destination.deposit(amount);
    }

    private static void validateAmount(BigDecimal amount, String operation) throws InvalidTransactionException {
        if (amount == null || amount.scale() > 2 || amount.signum() <= 0) {
            throw new InvalidTransactionException(operation
                    + " amount must be greater than zero and have no more than two decimal places.");
        }
    }
}
