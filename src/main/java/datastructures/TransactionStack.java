package datastructures;

import model.Transaction;

import java.util.Objects;

/**
 * Custom fixed-capacity linked stack that keeps the newest transactions for a mini-statement.
 */
public class TransactionStack {
    private Node top;
    private int size;
    private final int capacity;

    private static class Node {
        private final Transaction data;
        private Node next;

        Node(Transaction data) {
            this.data = data;
        }
    }

    /** Creates a stack that retains at most {@code capacity} newest entries. */
    public TransactionStack(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Transaction stack capacity must be positive.");
        }
        this.capacity = capacity;
    }

    /** Pushes a transaction and removes the oldest entry when the stack is full. */
    public void push(Transaction transaction) {
        Node newTop = new Node(Objects.requireNonNull(transaction, "Transaction is required."));
        newTop.next = top;
        top = newTop;
        size++;

        if (size > capacity) {
            Node current = top;
            for (int i = 1; i < capacity; i++) {
                current = current.next;
            }
            current.next = null;
            size = capacity;
        }
    }

    /** Returns whether the stack contains any transactions. */
    public boolean isEmpty() {
        return top == null;
    }

    /** Returns the number of retained transactions. */
    public int size() {
        return size;
    }

    /** Prints retained transactions from newest to oldest. */
    public void display() {
        if (isEmpty()) {
            System.out.println("  No transactions recorded yet.");
            return;
        }
        Node current = top;
        while (current != null) {
            System.out.println("   " + current.data);
            current = current.next;
        }
    }
}
