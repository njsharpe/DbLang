package net.njsharpe.dblang.sqlite;

import net.njsharpe.dblang.DbConnection;
import net.njsharpe.dblang.DbFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class SqliteDbFactory implements DbFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDbFactory.class);

    @Override
    public DbConnection open(String connectionString) {
        LOGGER.info("open(connectionString = {})", connectionString);
        try {
            return new SqliteDbConnection(connectionString);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not find SQLite connection drivers!");
        }
    }

    @Override
    public Future<DbConnection> openAsync(String connectionString) {
        LOGGER.info("openAsync(connectionString = {})", connectionString);
        return CompletableFuture.supplyAsync(() -> this.open(connectionString));
    }

}
