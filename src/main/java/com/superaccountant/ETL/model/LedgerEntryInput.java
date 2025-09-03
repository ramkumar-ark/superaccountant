package com.superaccountant.ETL.model;

public class LedgerEntryInput {

    private String ledgerName;
    private Boolean isDeemedPositive;
    private Double amount;

    // Getters and Setters

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public Boolean getIsDeemedPositive() {
        return isDeemedPositive;
    }

    public void setIsDeemedPositive(Boolean isDeemedPositive) {
        this.isDeemedPositive = isDeemedPositive;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}