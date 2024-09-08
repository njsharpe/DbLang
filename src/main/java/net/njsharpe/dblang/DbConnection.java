package net.njsharpe.dblang;

import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import net.njsharpe.dblang.query.Query;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DbConnection extends AutoCloseable {

    Logger LOGGER = LoggerFactory.getLogger(DbConnection.class);

    Connection getConnection();

    DialectProvider getDialectProvider();

    <T> Query<T> from(Class<T> type);

    <T> List<T> select(Query<T> query);
    <T> List<T> select(Class<T> type);
    <T> List<T> select(Class<T> type, Function<SupplierNode, Node> where);

    <T> Future<List<T>> selectAsync(Query<T> query);

    <T> Future<List<T>> selectAsync(Class<T> type, Function<SupplierNode, Node> where);

    <T> T single(Query<T> query);
    <T> T single(Class<T> type, Function<SupplierNode, Node> where);

    <T> Future<T> singleAsync(Query<T> query);

    <T> void createTable(Class<T> type);
    <T> void createTableIfNotExists(Class<T> type);
    <T> void dropTable(Class<T> type);

    <T> long insert(Class<T> type, Supplier<T> supplier);

    <T> void update(Class<T> type, T t);
    <T> void update(Class<T> type, T t, Function<SupplierNode, Node> where);
    <T> void updateOnly(Class<T> type, T t, String[] fields);
    <T> void updateOnly(Class<T> type, T t, String[] fields, Function<SupplierNode, Node> where);

    <T> void delete(Class<T> type);
    <T> void delete(Class<T> type, Function<SupplierNode, Node> where);

    default int execute(@Language("sql") String sql) {
        DialectProvider provider = this.getDialectProvider();
        return provider.execute(this.getConnection(), sql);
    }

    default int execute(@Language("sql") String sql, Map<Integer, Object> values) {
        DialectProvider provider = this.getDialectProvider();
        return provider.execute(this.getConnection(), sql, values);
    }

    default ResultSet executeQuery(@Language("sql") String sql) {
        DialectProvider provider = this.getDialectProvider();
        return provider.executeQuery(this.getConnection(), sql);
    }

    default ResultSet executeQuery(@Language("sql") String sql, Map<Integer, Object> values) {
        DialectProvider provider = this.getDialectProvider();
        return provider.executeQuery(this.getConnection(), sql, values);
    }

    default long[] executeUpdate(@Language("sql") String sql) {
        DialectProvider provider = this.getDialectProvider();
        return provider.executeUpdate(this.getConnection(), sql);
    }

    default long[] executeUpdate(@Language("sql") String sql, Map<Integer, Object> values) {
        DialectProvider provider = this.getDialectProvider();
        return provider.executeUpdate(this.getConnection(), sql, values);
    }

    default boolean isClosed() {
        Connection connection = this.getConnection();
        try {
            return connection == null || connection.isClosed();
        } catch (Exception ex) {
            return true;
        }
    }

    default void close() {
        LOGGER.info("close()");
        Connection connection = this.getConnection();
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
