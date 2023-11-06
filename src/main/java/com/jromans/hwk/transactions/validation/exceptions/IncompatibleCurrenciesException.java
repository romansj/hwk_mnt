package com.jromans.hwk.transactions.validation.exceptions;

public class IncompatibleCurrenciesException extends Exception {
    @Override
    public String getMessage() {
        return "Destination account does not accept specified currency";
    }
}
