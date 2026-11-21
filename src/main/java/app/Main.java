package app;

import exception.AccountNotFoundException;
import exception.InsufficientBalanceException;
import exception.InvalidTransactionException;
import model.Account;
import service.AccountService;
import service.AuthService;
import service.StatementService;
import service.TransactionService;
import util.InputValidator;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Runs the Scanner-based console menu for the Team B banking application.
 */
public class Main {
    private final Scanner scanner;
    private final AccountService accountService;
    private final AuthService authService;
    private final TransactionService transactionService;
    private final StatementService statementService;

    public Main(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "Scanner is required.");
        this.accountService = new AccountService();
        this.authService = new AuthService(accountService);
        this.transactionService = new TransactionService(accountService);
        this.statementService = new StatementService();
        seedDemoAccounts();
    }

    public static void main(String[] args) {
        Main application = new Main(new Scanner(System.in));
        application.run();
    }

    private void run() {
        System.out.println("=============== WELCOME TO TEAM B BANKING SYSTEM ===============");
        try {
            while (true) {
                printMainMenu();
                int choice = InputValidator.readInt(scanner, "Enter your choice: ");
                switch (choice) {
                    case 1:
                        createAccountMenu();
                        break;
                    case 2:
                        depositMenu();
                        break;
                    case 3:
                        withdrawMenu();
                        break;
                    case 4:
                        transferMenu();
                        break;
                    case 5:
                        miniStatementMenu();
                        break;
                    case 6:
                        displayAccountDetails();
                        break;
                    case 7:
                        System.out.println("Thank you for using the Banking System. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option! Please choose a number from 1 to 7.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nConsole input ended. Goodbye!");
        }
    }

    private void printMainMenu() {
        System.out.println("\n---------------- MENU ----------------");
        System.out.println("1. Create New Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Print Mini-Statement (Custom DSA)");
        System.out.println("6. Account Info & Balance");
        System.out.println("7. Exit");
        System.out.println("--------------------------------------");
    }

    private void createAccountMenu() {
        System.out.println("\n--- Account Creation ---");
        String accountNumber = InputValidator.readString(scanner, "Enter Account Number: ");
        String name = InputValidator.readString(scanner, "Enter Account Holder Name: ");
        BigDecimal initialBalance = InputValidator.readNonNegativeAmount(scanner, "Enter Initial Deposit: $");
        String pin = InputValidator.readPin(scanner, "Set 4-digit PIN: ");

        System.out.println("Select Account Type: 1. Savings | 2. Current");
        int type = InputValidator.readInt(scanner, "Choice: ");
        try {
            if (type == 1) {
                Account account = accountService.createSavingsAccount(
                        accountNumber, name, initialBalance, pin);
                System.out.println("Savings account created successfully: " + account.getAccountNumber());
            } else if (type == 2) {
                BigDecimal overdraftLimit =
                        InputValidator.readNonNegativeAmount(scanner, "Enter Overdraft Limit: $");
                Account account = accountService.createCurrentAccount(
                        accountNumber, name, initialBalance, overdraftLimit, pin);
                System.out.println("Current account created successfully: " + account.getAccountNumber());
            } else {
                System.out.println("Invalid account type selection. No account was created.");
            }
        } catch (InvalidTransactionException e) {
            printError(e);
        }
    }

    private void depositMenu() {
        try {
            Account account = authenticateAccount("Enter Account Number: ");
            if (account == null) {
                return;
            }
            BigDecimal amount = InputValidator.readAmount(scanner, "Enter Amount to Deposit: $");
            transactionService.deposit(account.getAccountNumber(), amount);
            statementService.recordTransaction(account.getAccountNumber(), "DEPOSIT", amount);
            System.out.printf(Locale.ROOT, "Deposit successful! Updated balance: $%.2f%n", account.getBalance());
        } catch (AccountNotFoundException | InvalidTransactionException e) {
            printError(e);
        }
    }

    private void withdrawMenu() {
        try {
            Account account = authenticateAccount("Enter Account Number: ");
            if (account == null) {
                return;
            }
            BigDecimal amount = InputValidator.readAmount(scanner, "Enter Amount to Withdraw: $");
            transactionService.withdraw(account.getAccountNumber(), amount);
            statementService.recordTransaction(account.getAccountNumber(), "WITHDRAWAL", amount);
            System.out.printf(Locale.ROOT, "Withdrawal successful! Remaining balance: $%.2f%n",
                    account.getBalance());
        } catch (AccountNotFoundException | InsufficientBalanceException | InvalidTransactionException e) {
            printError(e);
        }
    }

    private void transferMenu() {
        try {
            Account source = authenticateAccount("Enter Source Account Number: ");
            if (source == null) {
                return;
            }
            String destinationNumber = InputValidator.readString(scanner, "Enter Destination Account Number: ");
            BigDecimal amount = InputValidator.readAmount(scanner, "Enter Amount to Transfer: $");

            transactionService.transfer(source.getAccountNumber(), destinationNumber, amount);
            Account destination = accountService.getAccount(destinationNumber);
            statementService.recordTransaction(source.getAccountNumber(),
                    "TRANSFER TO " + destination.getAccountNumber(), amount);
            statementService.recordTransaction(destination.getAccountNumber(),
                    "TRANSFER FROM " + source.getAccountNumber(), amount);
            System.out.printf(Locale.ROOT, "Transfer of $%.2f from %s to %s completed successfully.%n",
                    amount, source.getAccountNumber(), destination.getAccountNumber());
        } catch (AccountNotFoundException | InsufficientBalanceException | InvalidTransactionException e) {
            printError(e);
        }
    }

    private void miniStatementMenu() {
        try {
            Account account = authenticateAccount("Enter Account Number: ");
            if (account != null) {
                statementService.printMiniStatement(account.getAccountNumber());
            }
        } catch (AccountNotFoundException e) {
            printError(e);
        }
    }

    private void displayAccountDetails() {
        try {
            Account account = authenticateAccount("Enter Account Number: ");
            if (account != null) {
                System.out.println(account);
            }
        } catch (AccountNotFoundException e) {
            printError(e);
        }
    }

    private Account authenticateAccount(String prompt) throws AccountNotFoundException {
        String accountNumber = InputValidator.readString(scanner, prompt);
        String pin = InputValidator.readPin(scanner, "Enter 4-digit PIN: ");
        if (!authService.authenticate(accountNumber, pin)) {
            System.out.println("Error: Authentication failed. Incorrect PIN.");
            return null;
        }
        return accountService.getAccount(accountNumber);
    }

    private void seedDemoAccounts() {
        try {
            accountService.createSavingsAccount("ACC1001", "Alice", new BigDecimal("1000.00"), "1234");
            accountService.createCurrentAccount("ACC1002", "Bob", new BigDecimal("2000.00"),
                    new BigDecimal("5000.00"), "5678");
        } catch (InvalidTransactionException e) {
            throw new IllegalStateException("Could not initialize demo accounts.", e);
        }
    }

    private static void printError(Exception exception) {
        System.out.println("Error: " + exception.getMessage());
    }
}
