package ua.sumy.stpp.web.register.dao;

import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Student;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

public class StudentDao {
    public static final int MAX_STUDENT_COUNT = 120;

    private static final Logger log = Logger.getLogger(StudentDao.class.getName());
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/mm/yy");

    private final DataSource dataSource;

    public StudentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int createStudent(String name, Date birthDate, String homeAddress, Group group) {
        String query = "INSERT INTO students (name, birth_date, home_address, group) VALUES (?, ?, ?, ?)";

        int createdStudentId = -1;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);
            statement.setString(2, dateFormat.format(dateFormat));
            statement.setString(3, homeAddress);
            statement.setInt(4, group.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.severe("Error creating new student, no rows affected!");
            } else {
                log.fine("Student successfully created!");
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        createdStudentId = generatedKeys.getInt("id");
                    } else {
                        log.severe("Error creating student, got no id.");
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return createdStudentId;
    }

    private Student fetchStudent(PreparedStatement statement) throws SQLException {
        Student student = null;

        try (ResultSet result = statement.executeQuery()) {
            if (result.first()) {
                student = new Student();
                student.setId(result.getInt("id"));
                student.setName(result.getString("name"));
                student.setBirthDate(dateFormat.parse(result.getString("birth_date")));
                student.setHomeAddress(result.getString("home_address"));
                student.setGroupId(result.getInt("group"));
            }
        } catch (ParseException e) {
            log.warning(String.format("Error parsing student's birth date: %s.", e.getMessage()));
        }

        return student;
    }

    public Student getStudent(String name) {
        Student foundStudent = null;

        String query = "SELECT id, name, birth_date, home_address, group FROM students WHERE name=?";
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, name);
            foundStudent = fetchStudent(statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return foundStudent;
    }

    public Student getStudent(int studentId) {
        Student foundStudent = null;

        String query = "SELECT id, name, birth_date, home_address, group FROM students WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setInt(1, studentId);
            foundStudent = fetchStudent(statement);

            connection.commit();
        } catch (SQLException e) {
            log.severe(String.format("Error working with database: %s.", e.getMessage()));
        }

        return foundStudent;
    }

    private void fetchStudents(List<Student> students, PreparedStatement statement) throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Student student = new Student();
                student.setId(result.getInt("id"));
                student.setName(result.getString("name"));
                student.setBirthDate(dateFormat.parse(result.getString("birth_date")));
                student.setHomeAddress(result.getString("home_address"));
                student.setGroupId(result.getInt("group"));
                students.add(student);
            }
        } catch (ParseException e) {
            log.warning(String.format("Error parsing student's birth date: %s.", e.getMessage()));
        }
    }

    public List<Student> getGroupStudents(int groupId) {
        List<Student> groupStudents = new LinkedList<>();

        String query = "SELECT id, name, birth_date, home_address, group FROM students WHERE group=?";
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

    public List<Student> getStudents(int count, int offset) {
        if (count > MAX_STUDENT_COUNT) {
            log.severe(String.format("Trying to get more than allowed students: %d > %d.", count, MAX_STUDENT_COUNT));
            log.info("Fetching maximum allowed students instead.");
            count = MAX_STUDENT_COUNT;
        }

        List<Student> students = new ArrayList<>(count);

        String query = "SELECT id, name, birth_date, home_address, group FROM students LIMIT ? OFFSET ?";
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

        String query = "UPDATE students SET name=?, birth_date=?, home_address=?, group=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            statement.setString(1, newStudentInfo.getName());
            statement.setString(2, dateFormat.format(newStudentInfo.getBirthDate()));
            statement.setString(3, newStudentInfo.getHomeAddress());
            statement.setInt(4, newStudentInfo.getGroupId());
            statement.setInt(5, newStudentInfo.getId());

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
        Student existingStudent = getStudent(studentId);
        if (Objects.isNull(existingStudent)) {
            log.severe(String.format("Trying to delete not existing student by id: %d.", studentId));
            return false;
        }

        boolean deleted = false;

        String query = "DELETE FROM students WHERE id=?";
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
