package net.njsharpe.dblang;

public class SqlRuntimeException extends RuntimeException {

    public SqlRuntimeException(String sql) {
        this(sql, null);
    }

    public SqlRuntimeException(String sql, Throwable throwable) {
        this(sql, "Error executing SQL", throwable);
    }

    public SqlRuntimeException(String sql, String message, Throwable throwable) {
        super("%s => %s".formatted(message, sql), throwable);
    }

}
