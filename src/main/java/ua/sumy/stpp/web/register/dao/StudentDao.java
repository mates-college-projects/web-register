package ua.sumy.stpp.web.register.dao;

import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Student;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class StudentDao {
    public static final int MAX_STUDENT_COUNT = 120;

    private static final Logger log = Logger.getLogger(StudentDao.class.getName());

    private final DataSource dataSource;

    public StudentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createStudent(String name, Group group) {
        Optional<Student> existingStudent = getStudent(name, group);
        if (existingStudent.isPresent()) {
            log.warning(String.format("Trying to create already existing student %s from group %s.", name, group));
            return;
        }

        String query = "INSERT INTO students (name, group_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);
            statement.setInt(2, group.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error creating new student, no rows affected!");
            } else {
                log.fine("Student successfully created!");
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }
    }

    private Student fetchStudent(PreparedStatement statement) throws SQLException {
        Student student = null;

        try (ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                student = new Student();
                student.setId(result.getInt("student_id"));
                student.setName(result.getString("name"));
                student.setGroupId(result.getInt("group_id"));
            }
        }

        return student;
    }

    public Optional<Student> getStudent(String name, Group group) {
        Student foundStudent = null;

        String query = "SELECT student_id, name, group_id FROM `students` WHERE name=? AND group_id=?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);
            statement.setInt(2, group.getId());
            foundStudent = fetchStudent(statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(foundStudent);
    }

    public Optional<Student> getStudent(int studentId) {
        Student foundStudent = null;

        String query = "SELECT student_id, name, group_id FROM `students` WHERE student_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, studentId);
            foundStudent = fetchStudent(statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return Optional.ofNullable(foundStudent);
    }

    private void fetchStudents(Set<Student> students, PreparedStatement statement) throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Student student = new Student();
                student.setId(result.getInt("student_id"));
                student.setName(result.getString("name"));
                student.setGroupId(result.getInt("group_id"));
                students.add(student);
            }
        }
    }

    public Set<Student> getGroupStudents(int groupId) {
        Set<Student> groupStudents = new HashSet<>();

        String query = "SELECT student_id, name, group_id FROM `students` WHERE group_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, groupId);
            fetchStudents(groupStudents, statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return groupStudents;
    }

    public Set<Student> getStudents(int count, int offset) {
        if (count > MAX_STUDENT_COUNT) {
            log.severe(String.format("Trying to get more than allowed students: %d > %d.", count, MAX_STUDENT_COUNT));
            log.info("Fetching maximum allowed students instead.");
            count = MAX_STUDENT_COUNT;
        }

        Set<Student> students = new HashSet<>(count);

        String query = "SELECT student_id, name, group_id FROM `students` LIMIT ? OFFSET ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, count);
            statement.setInt(2, offset);
            fetchStudents(students, statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return students;
    }

    public boolean updateStudent(Student newStudentInfo) {
        // we assume new student info is really *new*
        boolean updated = false;

        String query = "UPDATE `students` SET name=?, group_id=? WHERE student_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, newStudentInfo.getName());
            statement.setInt(2, newStudentInfo.getGroupId());
            statement.setInt(3, newStudentInfo.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error updating student's info, no rows affected.");
                updated = false;
            } else {
                log.fine("Successfully updated student's info.");
                updated = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return updated;
    }

    public boolean deleteStudent(int studentId) {
        Optional<Student> existingStudent = getStudent(studentId);
        if (existingStudent.isEmpty()) {
            log.severe(String.format("Trying to delete not existing student by id: %d.", studentId));
            return false;
        }

        boolean deleted = false;

        String query = "DELETE FROM `students` WHERE student_id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, studentId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error deleting student, no rows affected.");
                deleted = false;
            } else {
                log.fine("Successfully deleted student.");
                deleted = true;
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return deleted;
    }
}
