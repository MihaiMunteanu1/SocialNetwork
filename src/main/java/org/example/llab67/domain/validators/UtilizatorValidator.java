package org.example.llab67.domain.validators;

import org.example.llab67.domain.Utilizator;
import org.example.llab67.exceptions.ValidationException;
import org.example.llab67.repository.database.UtilizatorDbRepository;
import org.example.llab67.service.Service;

import java.util.Objects;
import java.util.Optional;

public class UtilizatorValidator implements Validator<Utilizator> {

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        StringBuilder builder = new StringBuilder();

        if (entity.getPassword().isEmpty())
            builder.append("Parola nu poate fi vida\n");
        if(entity.getPassword().length()<5)
            builder.append("Parola trebuie sa aiba cel putin 5 caractere\n");
        String mail= entity.getEmail().trim();
        System.out.println(mail);
        if (mail.isEmpty())
            builder.append("Email nu poate fi null\n");
        else if (!mail.endsWith("@yahoo.com") && !mail.endsWith("@gmail.com"))
            builder.append("Emailul trebuie sa fie de tipul @yahoo.com sau @gmail.com\n");
        if (entity.getFirstName().isEmpty())
            builder.append("Utilizatorul nu este valid");
        if (entity.getLastName().isEmpty())
            builder.append("Utilizatorul nu este valid");

        if (!builder.isEmpty())
            throw new ValidationException(String.valueOf(builder));

    }
}