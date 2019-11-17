package ua.sumy.stpp.web.register.controller.servlet;

import ua.sumy.stpp.web.register.DbDataSourceProvider;
import ua.sumy.stpp.web.register.dao.SubjectDao;
import ua.sumy.stpp.web.register.model.Subject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@WebServlet("/subjects")
public class SubjectsServlet extends HttpServlet {
    private final static Logger log = Logger.getLogger(SubjectsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher("/subjects.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/subjects.jsp");
        DbDataSourceProvider dataSourceProvider = (DbDataSourceProvider) context.getAttribute("DbDataSourceProvider");
        SubjectDao subjectDao = new SubjectDao(dataSourceProvider.getDataSource());

        String subjectName = request.getParameter("subject_name");
        if (Objects.isNull(subjectName) || subjectName.isEmpty()) {
            log.warning("Got null or empty subject name parameter.");
            request.setAttribute("error", "Subject name parameter not specified!");
            requestDispatcher.forward(request, response);
            return;
        }

        subjectDao.createSubject(subjectName);
        Optional<Subject> createdSubject = subjectDao.getSubject(subjectName);
        if (createdSubject.isEmpty()) {
            log.severe(String.format("Error getting created subject: \"%s\".", subjectName));
        }

        request.setAttribute("subject", createdSubject.orElse(null));
        requestDispatcher.forward(request, response);
    }
}
