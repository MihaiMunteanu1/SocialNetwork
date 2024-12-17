package org.example.llab67.domain.validators;

import org.example.llab67.domain.Prietenie;
import org.example.llab67.exceptions.ValidationException;

import java.util.Objects;

public class PrietenieValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity.getId1() == null || entity.getId2() == null)
            throw new ValidationException("ID-ul nu poate fi null");

        if (Objects.equals(entity.getId1(), entity.getId2()))
            throw new ValidationException("Nu te poti adauga pe tine ca prieten.");
    }
}