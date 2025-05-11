package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.TransactionStates;
import jdrb.banco.simulador.model.enums.TransactionType;

import java.util.Date;

public class Transaction {

    private String id;
    private String originAccountId;
    private String destinationAccountId;
    private float amount;
    private TransactionType type;
    private long timestamp;
    private TransactionStates state;

    public Transaction() {}

    public Transaction(String id,
                       String originAccountId,
                       String destinationAccountId,
                       float transactionAmount,
                       TransactionType type,
                       long timestamp,
                       TransactionStates state) {
        this.id = id;
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = transactionAmount;
        this.type = type;
        this.timestamp = timestamp;
        this.state = state;
    }

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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Date getFechaDate() {
        return new Date(timestamp);
    }

    public TransactionStates getState() {
        return state;
    }

    public void setState(TransactionStates state) {
        this.state = state;
    }
}
