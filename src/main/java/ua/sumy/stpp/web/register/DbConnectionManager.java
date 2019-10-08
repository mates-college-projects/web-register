package ua.sumy.stpp.web.register;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbConnectionManager {
    private final static Logger log = Logger.getLogger(DbConnectionManager.class.getName());

    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    public DbConnectionManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
