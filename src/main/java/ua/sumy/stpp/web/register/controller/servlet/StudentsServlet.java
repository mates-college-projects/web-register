package ua.sumy.stpp.web.register.controller.servlet;

import ua.sumy.stpp.web.register.DbDataSourceProvider;
import ua.sumy.stpp.web.register.dao.GroupDao;
import ua.sumy.stpp.web.register.dao.StudentDao;
import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Student;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@WebServlet("/students")
public class StudentsServlet extends HttpServlet {
    private final static Logger log = Logger.getLogger(StudentsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/students.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        setGroups(dataSourceProvider.getDataSource(), request);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/students.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        DataSource dataSource = dataSourceProvider.getDataSource();
        GroupDao groupDao = new GroupDao(dataSource);
        StudentDao studentDao = new StudentDao(dataSource);

        setGroups(dataSource, request);

        String studentName = request.getParameter("name");
        String groupCode = request.getParameter("group");
        if (Objects.isNull(studentName) || Objects.isNull(groupCode)) {
            log.warning("Got null group code or (and) student name parameter.");
            request.setAttribute("error", "Expected parameters not specified!");
            requestDispatcher.forward(request, response);
            return;
        }

        Optional<Group> group = groupDao.getGroup(groupCode);
        if (group.isEmpty()) {
            String errorText = String.format("Cannot get group by code %s.", groupCode);
            log.warning(errorText);
            request.setAttribute("error", errorText);
            requestDispatcher.forward(request, response);
            return;
        }

        studentDao.createStudent(studentName, group.get());
        Optional<Student> createdStudent = studentDao.getStudent(studentName, group.get());
        if (createdStudent.isEmpty()) {
            String errorText = String.format("Cannot create student %s in group %s.", studentName, groupCode);
            log.warning(errorText);
            request.setAttribute("error", errorText);
            requestDispatcher.forward(request, response);
            return;
        }

        request.setAttribute("student", createdStudent.get().getName());
        requestDispatcher.forward(request, response);
    }

    private void setGroups(DataSource dataSource, HttpServletRequest request) {
        GroupDao groupDao = new GroupDao(dataSource);
        Set<Group> groups = groupDao.getAllGroups();
        if (Objects.isNull(groups)) {
            log.severe("Error getting groups!");
        }
        request.setAttribute("groups", groups);
    }
}
