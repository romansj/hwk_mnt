package com.jromans.hwk.configuration.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to abstract away {@link ConstraintViolationException} to allow adding own errors to list
 */
public class MyConstraintException extends ValidationException {
    List<MyViolation> myViolationList = new ArrayList<>();

    public MyConstraintException(List<MyViolation> myViolations) {
        this.myViolationList = myViolations;
    }

    public List<MyViolation> getConstraintViolations() {
        return myViolationList;
    }

}
