package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.AccountType;

public class Account {

    private Long id;
    private String accountNumber;
    private Long customerId;
    private float balance;
    private AccountType type;
    private Long creationDate;

    public Account() {}

    public Account(Long id,
                   String accountNumber,
                   Long customerId,
                   float accountBalance,
                   AccountType type,
                   Long creationDate) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = accountBalance;
        this.type = type;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return type;
    }

    public void setAccountType(AccountType tipoCuenta) {
        this.type = tipoCuenta;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}
