package ua.sumy.stpp.web.register.controller.servlet;

import ua.sumy.stpp.web.register.DbDataSourceProvider;
import ua.sumy.stpp.web.register.dao.GroupDao;
import ua.sumy.stpp.web.register.dao.StudentDao;
import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Mark;
import ua.sumy.stpp.web.register.model.Student;
import ua.sumy.stpp.web.register.model.Subject;
import ua.sumy.stpp.web.register.service.GroupService;
import ua.sumy.stpp.web.register.service.ReportService;

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

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private final static Logger log = Logger.getLogger(ReportServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/report.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        DataSource dataSource = dataSourceProvider.getDataSource();
        GroupDao groupDao = new GroupDao(dataSource);

        setGroups(groupDao, request);

        String groupCode = request.getParameter("group");
        if (Objects.isNull(groupCode) || groupCode.isEmpty() || groupCode.isBlank()) {
            requestDispatcher.forward(request, response);
            return;
        }

        Optional<Group> group = groupDao.getGroup(groupCode);
        if (group.isEmpty()) {
            String errorText = String.format("Error getting group by code %s.", groupCode);
            log.warning(errorText);
            request.setAttribute("error", errorText);
            requestDispatcher.forward(request, response);
            return;
        }

        setTableData(dataSource, request, groupCode);

        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/report.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        DataSource dataSource = dataSourceProvider.getDataSource();
        StudentDao studentDao = new StudentDao(dataSource);

        setGroups(new GroupDao(dataSource), request);

        String groupCode = request.getParameter("group");
        if (Objects.isNull(groupCode) || groupCode.isEmpty() || groupCode.isBlank()) {
            String errorText = "Trying to calculate report without specified group!";
            log.warning(errorText);
            request.setAttribute("error", errorText);
            requestDispatcher.forward(request, response);
            return;
        }

        setTableData(dataSource, request, groupCode);

        String[] studentsIds = request.getParameterValues("students");
        Set<Student> students = new HashSet<>(studentsIds.length);
        for (String studentId : studentsIds) {
            Optional<Student> student = studentDao.getStudent(Integer.parseInt(studentId));
            if (student.isEmpty()) {
                log.warning(String.format("Trying to calculate report for not existing student by id: %s.", studentId));
            } else {
                students.add(student.get());
            }
        }

        Map<String, List<Mark>> studentsMarks = new HashMap<>(students.size());
        for (Student student : students) {
            String[] studentMarks = request.getParameterValues("marks_" + student.getId());
            List<Mark> marks = new ArrayList<>(studentMarks.length);
            for (String stringMark : studentMarks) {
                marks.add(new Mark(Integer.parseInt(stringMark)));
            }
            studentsMarks.put(student.getName(), marks);
        }

        ReportService reportService = new ReportService();

        List<Mark> averageMarks = new ArrayList<>(students.size());
        List<Mark> qualitativeMarks = new ArrayList<>(students.size());
        List<Mark> absoluteMarks = new ArrayList<>(students.size());

        for (Student student : students) {
            List<Mark> studentMarks = studentsMarks.get(student.getName());
            averageMarks.add(reportService.calculateAverageMark(studentMarks));
            qualitativeMarks.add(reportService.calculateQualitativeMark(studentMarks));
            absoluteMarks.add(reportService.calculateAbsoluteMark(studentMarks));
        }

        request.setAttribute("marks", studentsMarks);
        request.setAttribute("average_marks", averageMarks);
        request.setAttribute("qualitative_marks", qualitativeMarks);
        request.setAttribute("absolute_marks", absoluteMarks);

        requestDispatcher.forward(request, response);
    }

    private void setGroups(GroupDao groupDao, HttpServletRequest request) {
        Set<Group> groups = groupDao.getAllGroups();
        if (Objects.isNull(groups)) {
            log.severe("Error getting groups!");
        }
        request.setAttribute("groups", groups);
    }

    private void setTableData(DataSource dataSource, HttpServletRequest request, String groupCode) {
        GroupService groupService = new GroupService(dataSource);
        Set<Subject> subjects = groupService.getGroupSubjects(groupCode);
        Set<Student> students = groupService.getGroupStudents(groupCode);

        request.setAttribute("subjects", subjects);
        request.setAttribute("students", setToList(students));
    }

    private <T> List<T> setToList(Set<T> set) {
        List<T> list = new ArrayList<>(set.size());
        list.addAll(set);
        return list;
    }
}
