package org.example.llab67.repository.file;

import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.domain.validators.Validator;
import org.example.llab67.repository.Repository;

import java.time.LocalDateTime;

public class PrietenieRepository extends AbstractFileRepository<Long, Prietenie> {
    Repository<Long, Utilizator> repo;

    public PrietenieRepository(Validator<Prietenie> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public Prietenie lineToEntity(String line) {
        String[] split = line.split(";");
        Long id = Long.parseLong(split[0]);
        Long id1 = Long.parseLong(split[1]);
        Long id2 = Long.parseLong(split[2]);
        LocalDateTime date = LocalDateTime.parse(split[3]);

        Prietenie prietenie = new Prietenie(id1, id2, date);
        prietenie.setId(id);


        return prietenie;
    }

    @Override
    public String entityToLine(Prietenie entity) {
        return entity.getId() + ";" + entity.getId1() + ";" + entity.getId2();
    }
}