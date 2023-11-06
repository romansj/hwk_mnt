package com.jromans.hwk.configuration.exceptions;

/**
 * Mimicks {@link javax.validation.ConstraintViolation}, storing problem path and relevant error message
 *
 * @param path    Path (field) where problem occurred
 * @param message The message to show to API consumer/user for the error path
 */
public record MyViolation(String path, String message) {

}
