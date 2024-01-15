package org.example.entity;

public enum TransactionStatus {

    PENDIND("Pending"),
    SUCCESS("Success"),
    FAILED("Failed");

    private final String value;
    TransactionStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
