package ua.sumy.stpp.web.register.controller.listener;

import ua.sumy.stpp.web.register.DbConnectionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();

        String url = context.getInitParameter("DBURL");
        String user = context.getInitParameter("DBUSER");
        String password = context.getInitParameter("DBPWD");

        DbConnectionManager dbManager = new DbConnectionManager(url, user, password);
        context.setAttribute("DBManager", dbManager);

        log.info("Database connection initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        DbConnectionManager dbManager = (DbConnectionManager) context.getAttribute("DBManager");
        dbManager.closeConnection();

        log.info("Database connection closed.");
    }
}
