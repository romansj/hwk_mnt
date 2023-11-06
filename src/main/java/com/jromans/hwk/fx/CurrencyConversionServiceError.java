package com.jromans.hwk.fx;

import org.springframework.http.HttpStatusCode;

import java.text.MessageFormat;

public class CurrencyConversionServiceError extends Exception {
    private HttpStatusCode httpStatusCode;

    public CurrencyConversionServiceError(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Error occurred fetching exchange rate from external service. HTTP code {0}", httpStatusCode);
    }
}
