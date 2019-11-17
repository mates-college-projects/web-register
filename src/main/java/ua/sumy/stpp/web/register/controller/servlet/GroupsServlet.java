package ua.sumy.stpp.web.register.controller.servlet;

import ua.sumy.stpp.web.register.DbDataSourceProvider;
import ua.sumy.stpp.web.register.dao.SubjectDao;
import ua.sumy.stpp.web.register.model.Group;
import ua.sumy.stpp.web.register.model.Subject;
import ua.sumy.stpp.web.register.service.GroupService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.logging.Logger;

@WebServlet("/groups")
public class GroupsServlet extends HttpServlet {
    private final static Logger log = Logger.getLogger(GroupsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/groups.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        setSubjects(dataSourceProvider.getDataSource(), request);
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/groups.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        DataSource dataSource = dataSourceProvider.getDataSource();
        GroupService groupService = new GroupService(dataSource);

        // always show subjects
        setSubjects(dataSource, request);

        String groupCode = request.getParameter("code");
        String[] subjectIdsStrings = request.getParameterValues("subjects");
        if (Objects.isNull(groupCode) || Objects.isNull(subjectIdsStrings)) {
            log.warning("Got null group code or (and) subjects parameter.");
            request.setAttribute("error", "Expected parameters not specified!");
            requestDispatcher.forward(request, response);
            return;
        }

        Set<Integer> subjectIds = new HashSet<>(subjectIdsStrings.length);
        for (String subjectIdString : subjectIdsStrings) {
            subjectIds.add(Integer.parseInt(subjectIdString));
        }

        Optional<Group> createdGroup = groupService.createGroup(groupCode, subjectIds);
        if (createdGroup.isEmpty()) {
            log.severe(String.format("Error creating group with code %s,", groupCode));
        }

        request.setAttribute("group", createdGroup.orElse(null));
        requestDispatcher.forward(request, response);
    }

    private void setSubjects(DataSource dataSource, HttpServletRequest request) {
        SubjectDao subjectDao = new SubjectDao(dataSource);
        Set<Subject> subjects = subjectDao.getAllSubjects();
        if (Objects.isNull(subjects)) {
            log.severe("Error getting subjects!");
        }
        List<Subject> listSubjects = new ArrayList<>(subjects.size());
        listSubjects.addAll(subjects);
        request.setAttribute("subjects", listSubjects);
    }
}
