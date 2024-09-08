package net.njsharpe.dblang.sqlite;

import lombok.Getter;
import net.njsharpe.dblang.*;
import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import net.njsharpe.dblang.object.TableDefinition;
import net.njsharpe.dblang.query.Query;
import net.njsharpe.dblang.query.SqlQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

public class SqliteDbConnection implements DbConnection {

    private static final String JDBC_LABEL = "sqlite";

    @Getter
    private final Connection connection;

    public SqliteDbConnection() {
        this(":memory:");
    }

    public SqliteDbConnection(String connectionString) {
        this.connection = Maker.makeUnsafe(() -> DriverManager.getConnection("jdbc:%s:%s"
                .formatted(JDBC_LABEL, connectionString)));
    }

    @Override
    public DialectProvider getDialectProvider() {
        return SqliteDialectProvider.INSTANCE;
    }

    @Override
    public <T> Query<T> from(Class<T> type) {
        return new SqlQuery<>(this.getDialectProvider(), type);
    }

    @Override
    public <T> List<T> select(Query<T> query) {
        DialectProvider provider = this.getDialectProvider();
        ResultSet results = provider.executeQuery(this.getConnection(), provider.toSelectStatement(query));
        return Conversion.fromResultSet(query.getType(), query.getSourceType(), results);
    }

    @Override
    public <T> List<T> select(Class<T> type) {
        Query<T> query = this.from(type);
        return this.select(query);
    }

    @Override
    public <T> List<T> select(Class<T> type, Function<SupplierNode, Node> where) {
        Query<T> query = this.from(type)
                .where(where);
        return this.select(query);
    }

    @Override
    public <T> Future<List<T>> selectAsync(Query<T> query) {
        DialectProvider provider = this.getDialectProvider();
        CompletableFuture<ResultSet> results = CompletableFuture.supplyAsync(() ->
                provider.executeQuery(this.getConnection(), provider.toSelectStatement(query)));
        return results.thenApplyAsync(rs -> Conversion.fromResultSet(query.getType(), query.getSourceType(), rs));
    }

    @Override
    public <T> Future<List<T>> selectAsync(Class<T> type, Function<SupplierNode, Node> where) {
        DialectProvider provider = this.getDialectProvider();
        Query<T> query = this.from(type).where(where);
        CompletableFuture<ResultSet> results = CompletableFuture.supplyAsync(() ->
                provider.executeQuery(this.getConnection(), provider.toSelectStatement(query)));
        return results.thenApplyAsync(rs -> Conversion.fromResultSet(query.getType(), query.getSourceType(), rs));
    }

    @Override
    public <T> T single(Query<T> query) {
        DialectProvider provider = this.getDialectProvider();
        query = query.limit(1);
        ResultSet results = provider.executeQuery(this.getConnection(), provider.toSelectStatement(query));
        return Conversion.fromResultSet(query.getType(), query.getSourceType(), results).get(0);
    }

    @Override
    public <T> T single(Class<T> type, Function<SupplierNode, Node> where) {
        Query<T> query = this.from(type).where(where);
        return this.single(query);
    }

    @Override
    public <T> Future<T> singleAsync(Query<T> query) {
        DialectProvider provider = this.getDialectProvider();
        Query<T> limited = query.limit(1);
        CompletableFuture<ResultSet> results = CompletableFuture.supplyAsync(() ->
                provider.executeQuery(this.getConnection(), provider.toSelectStatement(limited)));
        return results.thenApplyAsync(rs -> Conversion.fromResultSet(limited.getType(), limited.getSourceType(), rs))
                .thenApplyAsync(r -> r.get(0));
    }

    @Override
    public <T> void createTable(Class<T> type) {
        DialectProvider provider = this.getDialectProvider();
        provider.execute(this.getConnection(), provider.toCreateTableStatement(type));
    }

    @Override
    public <T> void createTableIfNotExists(Class<T> type) {
        DialectProvider provider = this.getDialectProvider();
        provider.execute(this.getConnection(), provider.toCreateTableStatement(type, true));
    }

    @Override
    public <T> void dropTable(Class<T> type) {
        DialectProvider provider = this.getDialectProvider();
        provider.execute(this.getConnection(), provider.toDropTableStatement(type));
    }

    @Override
    public <T> long insert(Class<T> type, Supplier<T> supplier) {
        T t = supplier.get();
        DialectProvider provider = this.getDialectProvider();
        Map<Integer, Object> values = Query.toValuesMap(type, t);
        TableDefinition tableDef = Cache.getOrAddTable(type);
        long[] keys = provider.executeUpdate(this.getConnection(),
                provider.toInsertStatement(tableDef), values);
        return keys[0];
    }

    @Override
    public <T> void update(Class<T> type, T t) {
        this.update(type, t, c -> c);
    }

    @Override
    public <T> void update(Class<T> type, T t, Function<SupplierNode, Node> where) {
        String[] fieldNames = Reflect.getSerializableFields(type).stream()
                .filter(f -> !Reflect.isPrimaryKey(f))
                .map(f -> Reflect.getAliasOr(f, f.getName()))
                .toArray(String[]::new);
        this.updateOnly(type, t, fieldNames, where);
    }

    @Override
    public <T> void updateOnly(Class<T> type, T t, String[] fieldNames) {
        this.updateOnly(type, t, fieldNames, c -> c);
    }

    @Override
    public <T> void updateOnly(Class<T> type, T t, String[] fieldNames, Function<SupplierNode, Node> where) {
        DialectProvider provider = this.getDialectProvider();
        Map<Integer, Object> values = Query.toValuesMap(type, t, fieldNames);
        TableDefinition tableDef = Cache.getOrAddTable(type);
        provider.executeUpdate(this.getConnection(),
                provider.toUpdateStatement(tableDef, where, fieldNames), values);
    }

    @Override
    public <T> void delete(Class<T> type) {
        this.delete(type, c -> c);
    }

    @Override
    public <T> void delete(Class<T> type, Function<SupplierNode, Node> where) {
        DialectProvider provider = this.getDialectProvider();
        TableDefinition tableDef = Cache.getOrAddTable(type);
        provider.executeUpdate(this.getConnection(), provider.toDeleteStatement(tableDef, where));
    }

}
