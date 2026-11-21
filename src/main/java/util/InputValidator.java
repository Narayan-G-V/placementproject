package util;

import java.util.Scanner;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

/**
 * Reads complete console lines and repeats prompts until input has the required format.
 */
public class InputValidator {
    /** Reads a non-empty line, preserving spaces within the value. */
    public static String readString(Scanner scanner, String prompt) {
        while (true) {
            String value = readLine(scanner, prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    /** Reads an integer and repeats the prompt when the line is not numeric. */
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String value = readLine(scanner, prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid integer.");
            }
        }
    }

    /** Reads a positive monetary amount with at most two decimal places. */
    public static BigDecimal readAmount(Scanner scanner, String prompt) {
        while (true) {
            BigDecimal amount = readDecimal(scanner, prompt);
            if (amount.signum() > 0) {
                return amount;
            }
            System.out.println("Amount must be greater than zero.");
        }
    }

    /** Reads a non-negative monetary amount with at most two decimal places. */
    public static BigDecimal readNonNegativeAmount(Scanner scanner, String prompt) {
        while (true) {
            BigDecimal amount = readDecimal(scanner, prompt);
            if (amount.signum() >= 0) {
                return amount;
            }
            System.out.println("Amount cannot be negative.");
        }
    }

    /** Reads a four-digit PIN as text so leading zeroes are preserved. */
    public static String readPin(Scanner scanner, String prompt) {
        while (true) {
            String pin = readLine(scanner, prompt).trim();
            if (pin.matches("[0-9]{4}")) {
                return pin;
            }
            System.out.println("PIN must contain exactly four digits.");
        }
    }

    private static BigDecimal readDecimal(Scanner scanner, String prompt) {
        while (true) {
            String value = readLine(scanner, prompt);
            try {
                BigDecimal amount = new BigDecimal(value);
                if (amount.scale() <= 2) {
                    return amount;
                }
                System.out.println("Enter an amount with no more than two decimal places.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid decimal amount.");
            }
        }
    }

    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException("Console input has ended.");
        }
        return scanner.nextLine();
    }
}
