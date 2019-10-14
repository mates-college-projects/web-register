package ua.sumy.stpp.web.register.dao;

import ua.sumy.stpp.web.register.model.Subject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class SubjectDao {
    private static final Logger log = Logger.getLogger(GroupDao.class.getName());

    private final DataSource dataSource;

    public SubjectDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int createSubject(String name) {
        Optional<Subject> existingSubject = getSubject(name);
        if (existingSubject.isPresent()) {
            log.warning(String.format("Trying to create already existing subject %s.", name));
            return existingSubject.get().getId();
        }

        int createdSubjectId = -1;

        String query = "INSERT INTO subjects (name) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error creating new subject, no rows affected!");
            } else {
                log.fine("Subject successfully created!");
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        createdSubjectId = generatedKeys.getInt("id");
                    } else {
                        log.severe("Error creating subject, got no id.");
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return createdSubjectId;
    }

    public Optional<Subject> getSubject(int id) {
        Subject foundSubject = null;

        String query = "SELECT id, name FROM subjects WHERE id=?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.first()) {
                    foundSubject = new Subject();
                    foundSubject.setId(result.getInt("id"));
                    foundSubject.setName(result.getString("name"));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(foundSubject);
    }

    public Optional<Subject> getSubject(String name) {
        Subject foundSubject = null;

        String query = "SELECT id, name FROM subjects WHERE name=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);

            try (ResultSet result = statement.executeQuery()) {
                if (result.first()) {
                    foundSubject = new Subject();
                    foundSubject.setId(result.getInt("id"));
                    foundSubject.setName(result.getString("name"));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(foundSubject);
    }

    public boolean deleteSubject(int id) {
        Optional<Subject> existingSubject = getSubject(id);
        if (existingSubject.isEmpty()) {
            log.warning(String.format("Trying to delete not existing subject by id %d.", id));
            return false;
        }

        boolean deleted = false;

        String query = "DELETE FROM subjects WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error deleting subject, no rows affected.");
                deleted = false;
            } else {
                log.fine("Successfully deleted subject.");
                deleted = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return deleted;
    }

    public boolean deleteSubject(String name) {
        Optional<Subject> existingSubject = getSubject(name);
        AtomicBoolean deleted = new AtomicBoolean(false);
        existingSubject.ifPresentOrElse(
                subject -> deleted.set(deleteSubject(subject.getId())),
                () -> log.warning(String.format("Trying to delete not existing subject by name %s.", name)));
        return deleted.get();
    }
}
