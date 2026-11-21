package tests;

import datastructures.TransactionStack;
import exception.AccountNotFoundException;
import exception.InsufficientBalanceException;
import exception.InvalidTransactionException;
import model.Account;
import model.Transaction;
import service.AccountService;
import service.StatementService;
import service.TransactionService;
import util.InputValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Lightweight regression tests that run without third-party test libraries.
 */
public final class BankingSystemSelfTest {
    private BankingSystemSelfTest() {
    }

    public static void main(String[] args) throws Exception {
        testAccountCreationAndValidation();
        testDepositAndSavingsWithdrawal();
        testCurrentAccountOverdraft();
        testTransferValidationAndAtomicity();
        testTransactionStackRetainsNewestEntries();
        testInputParsing();
        System.out.println("All banking system self-tests passed.");
    }

    private static void testAccountCreationAndValidation() throws Exception {
        AccountService accounts = new AccountService();
        Account account = accounts.createSavingsAccount(
                "acc2001", "Jordan Lee", new BigDecimal("800.00"), "0042");
        check("ACC2001".equals(account.getAccountNumber()), "Account numbers should be normalized.");
        check(account.validatePin("0042"), "PINs should preserve leading zeroes.");
        expectInvalid(() -> accounts.createSavingsAccount(
                "ACC2001", "Another Holder", new BigDecimal("800.00"), "1234"));
        expectInvalid(() -> accounts.createSavingsAccount(
                "ACC2002", "Holder", new BigDecimal("499.99"), "1234"));
        expectInvalid(() -> accounts.createCurrentAccount(
                "ACC2003", "Holder", new BigDecimal("0.001"), BigDecimal.ZERO, "1234"));
    }

    private static void testDepositAndSavingsWithdrawal() throws Exception {
        AccountService accounts = new AccountService();
        TransactionService transactions = new TransactionService(accounts);
        Account savings = accounts.createSavingsAccount(
                "SAV1001", "Savings User", new BigDecimal("1000.00"), "1111");
        transactions.deposit("SAV1001", new BigDecimal("25.10"));
        transactions.withdraw("SAV1001", new BigDecimal("525.10"));
        checkMoney("500.00", savings.getBalance(), "Savings withdrawal should retain the minimum balance.");
        expectInsufficient(() -> transactions.withdraw("SAV1001", new BigDecimal("0.01")));
        expectInvalid(() -> transactions.deposit("SAV1001", BigDecimal.ZERO));
        expectInvalid(() -> transactions.deposit("SAV1001", new BigDecimal("1.001")));
    }

    private static void testCurrentAccountOverdraft() throws Exception {
        AccountService accounts = new AccountService();
        TransactionService transactions = new TransactionService(accounts);
        Account current = accounts.createCurrentAccount(
                "CUR1001", "Current User", new BigDecimal("100.00"), new BigDecimal("50.00"), "2222");
        transactions.withdraw("CUR1001", new BigDecimal("150.00"));
        checkMoney("-50.00", current.getBalance(), "Current account should use its overdraft limit.");
        expectInsufficient(() -> transactions.withdraw("CUR1001", new BigDecimal("0.01")));
    }

    private static void testTransferValidationAndAtomicity() throws Exception {
        AccountService accounts = new AccountService();
        TransactionService transactions = new TransactionService(accounts);
        Account source = accounts.createCurrentAccount(
                "SRC1001", "Source User", new BigDecimal("300.00"), BigDecimal.ZERO, "3333");
        Account destination = accounts.createCurrentAccount(
                "DST1001", "Destination User", BigDecimal.ZERO, BigDecimal.ZERO, "4444");

        expectNotFound(() -> transactions.transfer("SRC1001", "MISSING", new BigDecimal("50.00")));
        checkMoney("300.00", source.getBalance(), "A missing destination must not debit the source.");
        expectInvalid(() -> transactions.transfer("SRC1001", "src1001", new BigDecimal("50.00")));
        transactions.transfer("SRC1001", "DST1001", new BigDecimal("50.00"));
        checkMoney("250.00", source.getBalance(), "Transfer should debit the source.");
        checkMoney("50.00", destination.getBalance(), "Transfer should credit the destination.");

        StatementService statements = new StatementService();
        for (int i = 0; i < 6; i++) {
            statements.recordTransaction("SRC1001", "DEPOSIT", BigDecimal.ONE);
        }
        check(statements.getTransactionCount("SRC1001") == 5,
                "Mini-statements should keep only the configured number of transactions.");
    }

    private static void testTransactionStackRetainsNewestEntries() throws Exception {
        TransactionStack stack = new TransactionStack(5);
        for (int amount = 1; amount <= 6; amount++) {
            stack.push(new Transaction("DEPOSIT", BigDecimal.valueOf(amount)));
        }
        check(stack.size() == 5, "Stack should be trimmed to its capacity.");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8.name()));
            stack.display();
        } finally {
            System.setOut(originalOut);
        }
        String output = captured.toString(StandardCharsets.UTF_8.name());
        check(output.contains("$6.00") && output.contains("$2.00"),
                "Stack should display the five most recent transactions.");
        check(!output.contains("$1.00"), "Stack should discard the oldest transaction.");
    }

    private static void testInputParsing() {
        Scanner scanner = new Scanner("  Jordan Lee  \nnot-a-number\n7\n1.234\n2.50\n0012\n");
        check("Jordan Lee".equals(InputValidator.readString(scanner, "")),
                "String input should preserve spaces and trim edges.");
        check(InputValidator.readInt(scanner, "") == 7, "Integer input should retry invalid text.");
        check(new BigDecimal("2.50").equals(InputValidator.readAmount(scanner, "")),
                "Money input should reject more than two fractional digits.");
        check("0012".equals(InputValidator.readPin(scanner, "")),
                "PIN input should preserve leading zeroes.");
    }

    private static void checkMoney(String expected, BigDecimal actual, String message) {
        check(new BigDecimal(expected).compareTo(actual) == 0, message);
    }

    private static void expectInvalid(CheckedAction action) throws Exception {
        expect(InvalidTransactionException.class, action);
    }

    private static void expectInsufficient(CheckedAction action) throws Exception {
        expect(InsufficientBalanceException.class, action);
    }

    private static void expectNotFound(CheckedAction action) throws Exception {
        expect(AccountNotFoundException.class, action);
    }

    private static void expect(Class<? extends Exception> expected, CheckedAction action) throws Exception {
        try {
            action.run();
        } catch (Exception actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("Expected " + expected.getSimpleName() + " but got "
                    + actual.getClass().getSimpleName(), actual);
        }
        throw new AssertionError("Expected " + expected.getSimpleName() + " but no exception was thrown.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    @FunctionalInterface
    private interface CheckedAction {
        void run() throws Exception;
    }
}
