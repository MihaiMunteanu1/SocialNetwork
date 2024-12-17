package org.example.llab67.domain.validators;

import org.example.llab67.exceptions.ValidationException;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
