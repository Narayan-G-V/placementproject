package service;

import datastructures.TransactionStack;
import model.Transaction;
import util.SystemConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages mini-statements using the custom TransactionStack DSA per account.
 */
public class StatementService {
    private final Map<String, TransactionStack> transactionLogs = new HashMap<>();

    /** Adds a transaction to the account's bounded mini-statement history. */
    public void recordTransaction(String accNum, String type, java.math.BigDecimal amount) {
        transactionLogs.computeIfAbsent(accNum,
                key -> new TransactionStack(SystemConstants.TRANSACTION_HISTORY_LIMIT))
                .push(new Transaction(type, amount));
    }

    /** Prints the recent history for an account. */
    public void printMiniStatement(String accNum) {
        System.out.println("\n=== Mini Statement for Account: " + accNum + " ===");
        TransactionStack stack = transactionLogs.get(accNum);
        if (stack == null) {
            System.out.println("   No recent transactions found.");
        } else {
            stack.display();
        }
        System.out.println("=================================================");
    }

    /** Returns the number of retained transactions for an account. */
    public int getTransactionCount(String accNum) {
        TransactionStack stack = transactionLogs.get(accNum);
        return stack == null ? 0 : stack.size();
    }
}
