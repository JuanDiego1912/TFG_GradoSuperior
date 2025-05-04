package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.TransactionType;

import java.util.Date;

public class Transaction {

    private String id;
    private String originAccountId;
    private String destinationAccountId;
    private float transactionAmount;
    private TransactionType type;
    private Long date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginAccountId() {
        return originAccountId;
    }

    public void setOriginAccountId(String originAccountId) {
        this.originAccountId = originAccountId;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(String destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Date getFechaDate() {
        return new Date(date);
    }
}
