package ua.sumy.stpp.web.register.dao;

import ua.sumy.stpp.web.register.model.Group;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class GroupDao {
    private static final Logger log = Logger.getLogger(GroupDao.class.getName());

    private final DataSource dataSource;

    public GroupDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createGroup(String code) {
        Optional<Group> existingGroup = getGroup(code);
        if (existingGroup.isPresent()) {
            log.warning(String.format("Trying to create a group with already used code %s.", code));
            return;
        }

        String query = "INSERT INTO `groups` (code) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, code);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error creating new student, no rows affected!");
            } else {
                log.fine("Group successfully created!");
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }
    }

    public Optional<Group> getGroup(int id) {
        Group group = null;

        String query = "SELECT group_id, code FROM `groups` WHERE group_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    group = new Group();
                    group.setId(result.getInt("group_id"));
                    group.setCode(result.getString("code"));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(group);
    }

    public Optional<Group> getGroup(String code) {
        Group group = null;

        String query = "SELECT group_id, code FROM `groups` WHERE code=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, code);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    group = new Group();
                    group.setId(result.getInt("group_id"));
                    group.setCode(result.getString("code"));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(group);
    }

    public Set<Group> getAllGroups() {
        Set<Group> groups = new HashSet<>();

        String query = "SELECT * from `groups`";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            while (result.next()) {
                Group group = new Group();
                group.setId(result.getInt("group_id"));
                group.setCode(result.getString("code"));
                groups.add(group);
            }
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return groups;
    }

    public boolean deleteGroup(int id) {
        Optional<Group> existingGroup = getGroup(id);
        if (existingGroup.isEmpty()) {
            log.warning(String.format("Trying to delete not existing group %d.", id));
            return false;
        }

        boolean deleted = false;

        String query = "DELETE FROM `groups` WHERE group_id=?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.warning("Error deleting group, no rows affected.");
                deleted = false;
            } else {
                log.fine("Group successfully deleted.");
                deleted = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return deleted;
    }

    public boolean deleteGroup(String code) {
        Optional<Group> existingGroup = getGroup(code);
        AtomicBoolean deleted = new AtomicBoolean(false);
        existingGroup.ifPresentOrElse(
                group -> deleted.set(deleteGroup(group.getId())),
                () -> log.warning(String.format("Trying to delete not existing group %s.", code)));
        return deleted.get();
    }
}
