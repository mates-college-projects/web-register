package ua.sumy.stpp.web.register;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.logging.Logger;

public class DbDataSourceProvider {
    private final static Logger log = Logger.getLogger(DbDataSourceProvider.class.getName());

    private final String dbPath;

    private SQLiteDataSource dataSource;

    public DbDataSourceProvider(String dbPath) {
        this.dbPath = dbPath;
    }

    public DataSource getDataSource() {
        if (Objects.nonNull(dataSource)) {
            return dataSource;
        }

        String url = "jdbc:sqlite:" + dbPath;

        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        log.fine("Data source successfully created!");

        return dataSource;
    }
}
