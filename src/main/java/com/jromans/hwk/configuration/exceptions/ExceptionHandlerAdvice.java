package com.jromans.hwk.configuration.exceptions;

import com.jromans.hwk.configuration.mapping.ConstraintViolationMapper;
import com.jromans.hwk.transactions.validation.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.ServiceUnavailableException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {

    public static final Pattern ENUM_MSG = Pattern.compile(".*not one of the values accepted for Enum class: \\[(.+)\\]");


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNull(NullPointerException e) {
        return ResponseEntity.internalServerError()
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.internalServerError()
                .body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body("Wrong inputs provided");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(MessageFormat.format("Parameter [{0}] is missing", e.getParameterName()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleArgumentException(AccountNotFoundException e) {
        return ResponseEntity.internalServerError()
                .body(MessageFormat.format("Could not find account {0}", e.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> handleValidationExceptions(InsufficientFundsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IncompatibleCurrenciesException.class)
    public ResponseEntity<?> handleValidationExceptions(IncompatibleCurrenciesException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(MoneyNotGoingAnywhereException.class)
    public ResponseEntity<?> handleValidationExceptions(MoneyNotGoingAnywhereException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotConvertedException.class)
    public ResponseEntity<?> handleValidationExceptions(NotConvertedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<?> handleArgumentException(ServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleArgumentException(ConstraintViolationException e) {
        var constraintViolations = ConstraintViolationMapper.getViolations(e);
        var fieldErrors = getFieldErrors(constraintViolations);
        return ResponseEntity.badRequest().body(fieldErrors);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonErrors(HttpMessageNotReadableException exception) {
        var match = ENUM_MSG.matcher(exception.getMessage());
        if (match.matches()) {
            return ResponseEntity.badRequest().body("Value should be either of: " + match.group(1));
        }

        return ResponseEntity.badRequest().body(exception.getMessage());
    }


    @ExceptionHandler(MyConstraintException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MyConstraintException ex) {
        var fieldErrors = getFieldErrors(ex);
        return ResponseEntity.badRequest().body(fieldErrors);
    }

    // Pair would work, but adds "first" and "second" to json reponse
    // Map of String-Map is easily extendable with more items
    @NotNull
    private static Map<String, Map<String, String>> getFieldErrors(List<MyViolation> constraintViolations) {
        Map<String, String> errors = new HashMap<>();
        constraintViolations.forEach(constraintViolation -> {
            errors.put(String.valueOf(constraintViolation.path()), constraintViolation.message());
        });

        // can add more items with subitems as needed
        Map<String, Map<String, String>> body = new HashMap<>();
        body.put("errors", errors);
        return body;
    }

    @NotNull
    private static Map<String, Map<String, String>> getFieldErrors(MyConstraintException ex) {
        return getFieldErrors(ex.getConstraintViolations());
    }


}
