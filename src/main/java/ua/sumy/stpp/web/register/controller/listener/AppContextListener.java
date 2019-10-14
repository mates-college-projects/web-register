package ua.sumy.stpp.web.register.controller.listener;

import ua.sumy.stpp.web.register.DbDataSourceProvider;

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

        DbDataSourceProvider dbManager = new DbDataSourceProvider(":resource:database.db");
        context.setAttribute("DbDataSourceProvider", dbManager);

        log.info("Database connection initialized.");
    }
}
