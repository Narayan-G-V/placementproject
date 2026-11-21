package service;

import exception.AccountNotFoundException;
import model.Account;

/**
 * Security service for authenticating transactions via account PIN.
 */
public class AuthService {
    private final AccountService accountService;

    public AuthService(AccountService accountService) {
        this.accountService = accountService;
    }

    /** Returns whether the supplied PIN authenticates the requested account. */
    public boolean authenticate(String accNum, String pin) throws AccountNotFoundException {
        Account account = accountService.getAccount(accNum);
        return account.validatePin(pin);
    }
}
