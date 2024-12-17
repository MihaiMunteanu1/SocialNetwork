package org.example.llab67.repository.database;

import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.validators.Validator;
import org.example.llab67.repository.Repository;
import org.example.llab67.repository.paging.FriendRequestPagingRepository;
import org.example.llab67.utils.paging.Page;
import org.example.llab67.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDbRepository implements FriendRequestPagingRepository<Long, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private Validator<Prietenie> validator;

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        findAll();
    }


    @Override
    public Optional<Prietenie> findOne(Long aLong) {
        Prietenie prietenie = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE id = ?");
             ResultSet resultSet = statement.executeQuery()) {

            Long id = resultSet.getLong("id");
            Long id_Utilizator1 = resultSet.getLong("id_Utilizator1");
            Long id_Utilizator2 = resultSet.getLong("id_Utilizator2");
            boolean pending = resultSet.getBoolean("pending");
            LocalDateTime friendsFrom = resultSet.getDate("friendsFrom").toLocalDate().atStartOfDay();

            prietenie = new Prietenie(id_Utilizator1, id_Utilizator2, friendsFrom);
            prietenie.setId(id);
            prietenie.setPending(pending);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(prietenie);
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id_Utilizator1");
                Long id2 = resultSet.getLong("id_Utilizator2");
                boolean pending = resultSet.getBoolean("pending");
                LocalDateTime friendsFrom = resultSet.getDate("friendsFrom").toLocalDate().atStartOfDay();

                Prietenie prietenie = new Prietenie(id1, id2, friendsFrom);
                prietenie.setId(id);
                prietenie.setPending(pending);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }


    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        validator.validate(entity);
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO friendships (id, id_Utilizator1, id_Utilizator2, friendsfrom, pending) VALUES (?, ?, ?, ?, ?)");
        ) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getId1());
            statement.setLong(3, entity.getId2());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setBoolean(5, entity.isPending());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0)
            return Optional.empty();
        else
            return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Prietenie> delete(Long aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id = ?");) {

            statement.setLong(1, aLong);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Prietenie prietenieToDelete = null;
        for (Prietenie prietenie : findAll()) {
            if (Objects.equals(prietenie.getId(), aLong))
                prietenieToDelete = prietenie;
        }
        return Optional.ofNullable(prietenieToDelete);
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        validator.validate(entity);
        int rez = -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE friendships SET id_Utilizator1 = ?, id_Utilizator2 = ?, friendsfrom = ?, pending = ? WHERE id = ?");
        ) {
            statement.setLong(1, entity.getId1());
            statement.setLong(2, entity.getId2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setBoolean(4, entity.isPending());
            statement.setLong(5, entity.getId());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }



    @Override
    public Page<Prietenie> findAllOnPage(Pageable pageable, Long idU) {
        List<Prietenie> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE (pending = false AND (id_utilizator1 = ? OR id_utilizator2 = ?)) ";
        query += "LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, idU);
            statement.setLong(2, idU);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, pageable.getPageSize() * pageable.getPageNumber());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id_utilizator1");
                Long id2 = resultSet.getLong("id_utilizator2");
                boolean pending = resultSet.getBoolean("pending");
                LocalDateTime friendsFrom = resultSet.getTimestamp("friendsfrom").toLocalDateTime();
                Prietenie prietenie = new Prietenie(id1, id2, friendsFrom);
                prietenie.setId(id);
                prietenie.setPending(pending);
                friendships.add(prietenie);
            }

            return new Page<>(friendships, sizeWithFilter(connection, idU));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int sizeWithFilter(Connection connection, Long idU) {
        String query = "SELECT COUNT(*) AS count FROM friendships WHERE (pending = false AND (id_utilizator1 = ? OR id_utilizator2 = ?))";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, idU);
            statement.setLong(2, idU);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public Page<Prietenie> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, 0L);
    }
}
