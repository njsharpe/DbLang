package net.njsharpe.dblang;

import java.util.concurrent.Future;

public interface DbFactory {

    DbConnection open(String connectionString);
    Future<DbConnection> openAsync(String connectionString);

}
