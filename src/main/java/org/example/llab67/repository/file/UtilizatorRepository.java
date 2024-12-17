package org.example.llab67.repository.file;


import org.example.llab67.domain.Utilizator;
import org.example.llab67.domain.validators.Validator;

public class UtilizatorRepository extends AbstractFileRepository<Long, Utilizator> {
    public UtilizatorRepository(Validator<Utilizator> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Utilizator lineToEntity(String line) {
        String[] splited = line.split(";");
        Utilizator u = new Utilizator(splited[1], splited[2], splited[3],splited[4]);
        u.setId(Long.parseLong(splited[0]));
        return u;
    }

    @Override
    public String entityToLine(Utilizator entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
