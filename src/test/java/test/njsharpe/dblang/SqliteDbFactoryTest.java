package test.njsharpe.dblang;

import net.njsharpe.dblang.DbConnection;
import net.njsharpe.dblang.DbFactory;
import net.njsharpe.dblang.sqlite.SqliteDbFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqliteDbFactoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDbFactory.class);

    private static final String CONNECTION_STRING = "junit.db";

    @Test
    public void openTest() {
        LOGGER.info("openTest()");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.open(CONNECTION_STRING)) {
            Assertions.assertNotNull(db);
            Assertions.assertFalse(db.isClosed());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void openAsyncTest() {
        LOGGER.info("openAsyncTest()");
        DbFactory dbFactory = new SqliteDbFactory();
        try(DbConnection db = dbFactory.openAsync(CONNECTION_STRING).get()) {
            Assertions.assertNotNull(db);
            Assertions.assertFalse(db.isClosed());
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

}
