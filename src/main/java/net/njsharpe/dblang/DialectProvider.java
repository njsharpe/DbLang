package net.njsharpe.dblang;

import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import net.njsharpe.dblang.object.TableDefinition;
import net.njsharpe.dblang.query.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.function.Function;

public interface DialectProvider {

    String getDefaultSchemaName();

    <T> String toSelectStatement(Query<T> query);
    <T> String toJoinStatement(Query<T> query);
    <T> String toWhereStatement(Query<T> query);
    <T> String toOrderByStatement(Query<T> query);

    String toInsertStatement(TableDefinition tableDef);
    String toUpdateStatement(TableDefinition tableDef, Function<SupplierNode, Node> where, String[] fields);
    String toDeleteStatement(TableDefinition tableDef, Function<SupplierNode, Node> where);

    <T> String toCreateTableStatement(Class<T> type);
    <T> String toCreateTableStatement(Class<T> type, boolean ifNotExists);
    <T> String toDropTableStatement(Class<T> type);

    int execute(Connection connection, String sql);
    int execute(Connection connection, String sql, Map<Integer, Object> values);
    ResultSet executeQuery(Connection connection, String sql);
    ResultSet executeQuery(Connection connection, String sql, Map<Integer, Object> values);
    long[] executeUpdate(Connection connection, String sql);
    long[] executeUpdate(Connection connection, String sql, Map<Integer, Object> values);

    String getSqlTypeFromType(Class<?> type);

}
