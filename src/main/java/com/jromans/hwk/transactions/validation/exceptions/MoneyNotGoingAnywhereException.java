package com.jromans.hwk.transactions.validation.exceptions;

public class MoneyNotGoingAnywhereException extends Exception {
    @Override
    public String getMessage() {
        return "Source and destination accounts cannot be the same";
    }
}
