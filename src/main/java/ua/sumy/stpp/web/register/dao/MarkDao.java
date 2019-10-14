package ua.sumy.stpp.web.register.dao;

import ua.sumy.stpp.web.register.model.Mark;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class MarkDao {
    private static final Logger log = Logger.getLogger(MarkDao.class.getName());
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/mm/yy");

    private final DataSource dataSource;

    public MarkDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createMark(int studentId, int groupId, int subjectId, Date setDate, int value) {
        createMarks(Set.of(new Mark(studentId, groupId, subjectId, setDate, value)));
    }

    public void createMarks(Set<Mark> marks) {
        String queue = "INSERT INTO marks (student, group, subject, date, value) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(queue)) {
            connection.setAutoCommit(false);

            for (Mark mark : marks) {
                statement.setInt(1, mark.getStudentId());
                statement.setInt(2, mark.getGroupId());
                statement.setInt(3, mark.getSubjectId());
                statement.setString(4, dateFormat.format(mark.getSetDate()));
                statement.setInt(5, mark.getValue());
                statement.addBatch();
            }

            int[] rowsAffectedArray = statement.executeBatch();
            for (int rowsAffected : rowsAffectedArray) {
                if (rowsAffected == 0) {
                    log.severe("Error happened during adding new marks to database: no rows affected!");
                } else {
                    log.fine("Created new marks.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }
    }

    public Optional<Mark> getMark(int markId) {
        Mark mark = null;

        String query = "SELECT id, student, group, subject, date, value FROM marks WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, markId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.first()) {
                    mark = new Mark();
                    mark.setId(markId);
                    mark.setStudentId(result.getInt("student"));
                    mark.setGroupId(result.getInt("group"));
                    mark.setSubjectId(result.getInt("subject"));
                    mark.setSetDate(dateFormat.parse(result.getString("date")));
                    mark.setValue(result.getInt("value"));
                }
            } catch (ParseException e) {
                log.warning("Error parsing mark set date.");
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(mark);
    }

    private void fetchMarks(Set<Mark> marks, PreparedStatement statement) throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Mark mark = new Mark();
                mark.setId(result.getInt("id"));
                mark.setStudentId(result.getInt("student"));
                mark.setGroupId(result.getInt("group"));
                mark.setSubjectId(result.getInt("subject"));
                mark.setSetDate(dateFormat.parse(result.getString("date")));
                mark.setValue(result.getInt("value"));
                marks.add(mark);
            }
        } catch (ParseException e) {
            log.warning("Error parsing mark set date.");
        }
    }

    private Set<Mark> getMarks(int queryId, Set<Mark> marks, String query) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, queryId);
            fetchMarks(marks, statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return marks;
    }

    public Set<Mark> getStudentMarks(int studentId) {
        Set<Mark> marks = new HashSet<>();
        String query = "SELECT id, student, group, subject, date, value FROM marks WHERE student=?";
        return getMarks(studentId, marks, query);
    }

    public Set<Mark> getGroupMarks(int groupId) {
        Set<Mark> marks = new HashSet<>();
        String query = "SELECT id, student, group, subject, date, value FROM marks WHERE group=?";
        return getMarks(groupId, marks, query);
    }

    public Set<Mark> getSubjectMarks(int subjectId, int groupId) {
        Set<Mark> marks = new HashSet<>();

        String query = "SELECT id, student, group, subject, date, value FROM marks WHERE subject=? AND group=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, subjectId);
            statement.setInt(2, groupId);
            fetchMarks(marks, statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return marks;
    }

    public boolean updateMark(int markId, int value) {
        boolean updated = false;

        String query = "UPDATE marks SET value=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, markId);
            statement.setInt(2, value);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error updating mark, no rows affected.");
                updated = false;
            } else {
                log.fine("Successfully updated mark.");
                updated = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return updated;
    }

    public boolean deleteMark(int markId) {
        Optional<Mark> existingMark = getMark(markId);
        if (existingMark.isEmpty()) {
            log.warning(String.format("Trying to delete not existing mark %d.", markId));
            return false;
        }

        boolean deleted = false;

        String query = "DELETE FROM marks WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, markId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.warning("Error deleting mark, no rows affected.");
                deleted = false;
            } else {
                log.fine("Mark successfully deleted.");
                deleted = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return deleted;
    }
}
