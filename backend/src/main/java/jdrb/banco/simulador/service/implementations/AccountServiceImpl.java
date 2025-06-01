package jdrb.banco.simulador.service.implementations;

import jdrb.banco.simulador.dao.AccountDAO;
import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private final AccountDAO accountDAO;

    @Autowired
    public AccountServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public boolean registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null or empty");
        }

        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            account.setAccountNumber(generateAccountNumber());
        }

        return accountDAO.registerAccount(account);
    }

    @Override
    public Account getAccountById(Long id) {
        validateId(id, "Account ID");
        return accountDAO.getAccountById(id);
    }

    @Override
    public List<Account> getAccountsByClient(Long customerId) {
        validateId(customerId, "Customer ID");
        return accountDAO.getAccountsByClient(customerId);
    }

    @Override
    public boolean updateAccount(Account account) {
        if (account == null || account.getId() == null || account.getId() <= 0) {
            throw new IllegalArgumentException("Account or account ID cannot be null or empty");
        }
        return accountDAO.updateAccount(account);
    }

    @Override
    public boolean deleteAccount(Long id) {

        Account account = getAccountById(id);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        validateId(id, "Account ID");
        if (account.getBalance() > 0.0) {
            throw new IllegalStateException("Cannot delete account with active balance: " + account.getBalance());
        }

        return accountDAO.deleteAccount(id);
    }

    @Override
    public boolean deleteAccountForClient(Long customerId, Long accountId) {

        validateId(customerId, "Customer ID");
        validateId(accountId, "Account ID");
        Account account = getAccountById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with ID: " + accountId);
        }
        if (account.getBalance() > 0.0) {
            throw new IllegalStateException("Cannot delete account with active balance: " + account.getBalance());
        }

        return accountDAO.deleteAccountForClient(customerId, accountId);
    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder("ES");
        for (int i = 0; i < 22; i++) {
            accountNumber.append((int) (Math.random() * 10));
        }
        return accountNumber.toString();
    }

    private void validateId(Long id, String fieldname) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldname + " must be a positive number");
        }
    }
}
