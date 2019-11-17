package ua.sumy.stpp.web.register.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class GroupsSubjectsDao {
    private final static Logger log = Logger.getLogger(GroupsSubjectsDao.class.getName());

    private final DataSource dataSource;

    public GroupsSubjectsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createGroupSubjectsEntry(int groupId, Set<Integer> subjectIds) {
        String query = "INSERT INTO `groups_subjects` (group_id, subject_id) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            for (Integer subjectId : subjectIds) {
                statement.setInt(1, groupId);
                statement.setInt(2, subjectId);
                statement.addBatch();
            }

            int[] rowsAffectedArray = statement.executeBatch();
            for (int rowsAffected : rowsAffectedArray) {
                if (rowsAffected == 0) {
                    log.severe("Error happened during adding new entries to database: no rows affected!");
                } else {
                    log.fine("Created new entries.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error creating new group subject entry: %s", e.getMessage()));
        }
    }

    public Set<Integer> getGroupSubjects(int groupId) {
        Set<Integer> groupSubjects = new HashSet<>();

        String query = "SELECT `groups`.group_id, `subjects`.subject_id FROM `subjects`, `groups`, `groups_subjects` " +
                "WHERE `groups`.group_id=? AND `subjects`.subject_id=`groups_subjects`.subject_id";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, groupId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    groupSubjects.add(result.getInt("subject_id"));
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error creating new group subject entry: %s", e.getMessage()));
        }

        return groupSubjects;
    }
}
