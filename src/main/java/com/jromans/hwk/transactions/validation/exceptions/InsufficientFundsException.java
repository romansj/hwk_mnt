package com.jromans.hwk.transactions.validation.exceptions;

public class InsufficientFundsException extends Exception {
    @Override
    public String getMessage() {
        return "Not enough funds in account";
    }
}
