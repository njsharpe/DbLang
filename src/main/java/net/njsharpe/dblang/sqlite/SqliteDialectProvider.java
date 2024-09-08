package net.njsharpe.dblang.sqlite;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.njsharpe.dblang.*;
import net.njsharpe.dblang.annotation.AutoIncrement;
import net.njsharpe.dblang.annotation.PrimaryKey;
import net.njsharpe.dblang.condition.JoinCondition;
import net.njsharpe.dblang.condition.WhereCondition;
import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import net.njsharpe.dblang.object.ColumnDefinition;
import net.njsharpe.dblang.object.TableDefinition;
import net.njsharpe.dblang.query.OrderBy;
import net.njsharpe.dblang.query.Query;
import org.apache.commons.lang3.Validate;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SqliteDialectProvider implements DialectProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDialectProvider.class);

    public static final SqliteDialectProvider INSTANCE = new SqliteDialectProvider();

    @Override
    public String getDefaultSchemaName() {
        return "main";
    }

    @Override
    public <T> String toSelectStatement(Query<T> query) {
        StringBuilder sb = StringBuilderCache.acquire()
                .append("select ");

        Class<?> type = query.getType();
        Class<?> sourceType = query.getSourceType();

        List<Field> fields = Reflect.getSerializableFields(type);
        Iterator<Field> iter = fields.iterator();
        while(iter.hasNext()) {
            Field field = iter.next();
            Class<?> source = Reflect.getFieldDataSource(field, sourceType);
            sb.append(Query.getQualifiedField(source, field));
            if(iter.hasNext()) {
                sb.append(", ");
            }
        }

        String from = Reflect.getAliasOr(sourceType, sourceType.getSimpleName());

        sb.append(" from ").append(from);

        sb.append(this.toJoinStatement(query));
        sb.append(this.toWhereStatement(query));
        sb.append(this.toOrderByStatement(query));

        if(query.getLimit() > 0) {
            sb.append(" limit ").append(query.getLimit());
        }

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public <T> String toJoinStatement(Query<T> query) {
        List<JoinCondition> joins = query.getJoins();
        if(joins.isEmpty()) {
            return "";
        }

        StringBuilder sb = StringBuilderCache.acquire();

        for(JoinCondition join : joins) {
            Class<?> from = join.getFrom();
            Class<?> to = join.getTo();

            String fromTable = Reflect.getAliasOr(from, from.getSimpleName());
            String toTable = Reflect.getAliasOr(to, to.getSimpleName());

            sb.append(" join ").append(toTable).append(" on ").append(fromTable).append(".").append(join.getFromField())
                    .append(" = ").append(toTable).append(".").append(join.getToField());
        }

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public <T> String toWhereStatement(Query<T> query) {
        if(query.getWhere() == null) {
            return "";
        }

        StringBuilder sb = StringBuilderCache.acquire()
                .append(" where ").append(query.getWhere().toString());

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public <T> String toOrderByStatement(Query<T> query) {
        List<OrderBy> orders = query.getOrders();

        if(orders.isEmpty()) {
            return "";
        }

        StringBuilder sb = StringBuilderCache.acquire()
                .append(" order by ");

        Iterator<OrderBy> iter = orders.iterator();
        while(iter.hasNext()) {
            OrderBy order = iter.next();

            Class<?> type = order.getType();
            String table = Reflect.getAliasOr(type, type.getSimpleName());

            sb.append(table).append(".").append(order.getField()).append(" ")
                    .append(order.getDirection().getTranslation());

            if(iter.hasNext()) {
                sb.append(", ");
            }
        }

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public String toInsertStatement(TableDefinition tableDef) {
        StringBuilder sb = StringBuilderCache.acquire()
                .append("insert into ");

        sb.append(tableDef.getName()).append(" ");

        List<ColumnDefinition> columns = tableDef.getColumnDefinitions().stream()
                .filter(cd -> !cd.isPrimaryKey())
                .toList();
        int count = columns.size();

        sb.append("(");

        Iterator<ColumnDefinition> iter = columns.iterator();
        while(iter.hasNext()) {
            ColumnDefinition colDef = iter.next();
            sb.append(colDef.getName());
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(") values (");

        for(int i = 0; i < count; i++) {
            sb.append("?");
            if(i + 1 < count) {
                sb.append(", ");
            }
        }

        sb.append(")");

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public String toUpdateStatement(TableDefinition tableDef, Function<SupplierNode, Node> where, String[] fieldNames) {
        StringBuilder sb = StringBuilderCache.acquire()
                .append("update ")
                .append(tableDef.getName())
                .append(" set ");

        Iterator<String> iter = List.of(fieldNames).iterator();

        while(iter.hasNext()) {
            String name = iter.next();
            sb.append(name).append(" = ?");

            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        SupplierNode node = new SupplierNode(tableDef);
        Node result = where.apply(node);

        if(result.getClass() != SupplierNode.class) {
            WhereCondition condition = new WhereCondition(tableDef, result);
            sb.append(" where ").append(condition);
        }

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public String toDeleteStatement(TableDefinition tableDef, Function<SupplierNode, Node> where) {
        StringBuilder sb = StringBuilderCache.acquire()
                .append("delete from ").append(tableDef.getName());

        SupplierNode node = new SupplierNode(tableDef);
        Node result = where.apply(node);

        if(result.getClass() != SupplierNode.class) {
            WhereCondition condition = new WhereCondition(tableDef, result);
            sb.append(" where ").append(condition);
        }

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public <T> String toCreateTableStatement(Class<T> type) {
        return this.toCreateTableStatement(type, false);
    }

    @Override
    public <T> String toCreateTableStatement(Class<T> type, boolean ifNotExists) {
        String table = Reflect.getAliasOr(type, type.getSimpleName());

        StringBuilder sb = StringBuilderCache.acquire()
                .append("create table ");

        if(ifNotExists) {
            sb.append("if not exists ");

        }

        sb.append(table).append(" (");

        List<Field> fields = Reflect.getSerializableFields(type);
        Iterator<Field> iter = fields.iterator();
        while(iter.hasNext()) {
            Field field = iter.next();

            String name = Reflect.getAliasOr(field, field.getName());
            Class<?> t = field.getType();
            String sqlType = this.getSqlTypeFromType(t);

            boolean isKey = field.isAnnotationPresent(PrimaryKey.class)
                    || field.getName().equalsIgnoreCase("id");
            boolean isAutoIncrement = field.isAnnotationPresent(AutoIncrement.class);
            boolean isNullable = Reflect.isNullable(field);

            sb.append(name).append(" ").append(sqlType).append(" ")
                    .append(isKey ? "primary key " : "")
                    .append(isAutoIncrement ? "autoincrement " : "")
                    .append(isNullable ? "null" : "not null");

            if(iter.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(")");

        return StringBuilderCache.getStringAndRelease(sb);
    }

    @Override
    public <T> String toDropTableStatement(Class<T> type) {
        String name = Reflect.getAliasOr(type, type.getSimpleName());
        return "drop table %s".formatted(name);
    }

    @Override
    public int execute(Connection connection, @Language("sqlite") String sql) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("execute(sql = {})", sql);
        try(Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return statement.getUpdateCount();
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public int execute(Connection connection, @Language("sqlite") String sql, Map<Integer, Object> values) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("execute(sql = {}, values = [{}])", sql, values.entrySet().stream()
                .map(e -> "(%s, %s)".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ")));
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            SqlWriter.writeObject(statement, values);
            statement.execute();
            return statement.getUpdateCount();
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public ResultSet executeQuery(Connection connection, @Language("sqlite") String sql) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("executeQuery(sql = {})", sql);
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public ResultSet executeQuery(Connection connection, @Language("sqlite") String sql, Map<Integer, Object> values) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("executeQuery(sql = {}, values = [{}])", sql, values.entrySet().stream()
                .map(e -> "(%s, %s)".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ")));
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            SqlWriter.writeObject(statement, values);
            return statement.executeQuery();
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public long[] executeUpdate(Connection connection, String sql) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("executeUpdate(sql = {})", sql);
        try(Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            if(count <= 0) {
                return new long[0];
            }

            long[] keys = new long[count];
            int index = 0;
            try(ResultSet set = statement.getGeneratedKeys()) {
                while(set.next()) {
                    keys[index] = set.getLong(1);
                    index++;
                }
            }

            return keys;
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public long[] executeUpdate(Connection connection, String sql, Map<Integer, Object> values) {
        Validate.notNull(connection, "Connection cannot be null!");
        Validate.isTrue(Truthy.resolve(() -> !connection.isClosed()), "Connection cannot be closed!");
        LOGGER.info("executeQuery(sql = {}, values = [{}])", sql, values.entrySet().stream()
                .map(e -> "(%s, %s)".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ")));
        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            SqlWriter.writeObject(statement, values);
            int count = statement.executeUpdate();

            if(count <= 0) {
                return new long[0];
            }

            long[] keys = new long[count];
            int index = 0;
            try(ResultSet set = statement.getGeneratedKeys()) {
                while(set.next()) {
                    keys[index] = set.getLong(1);
                    index++;
                }
            }

            return keys;
        } catch (Exception ex) {
            throw new SqlRuntimeException(sql, ex);
        }
    }

    @Override
    public String getSqlTypeFromType(Class<?> type) {
        if(type == null) {
            return "null";
        }

        if(CharSequence.class.isAssignableFrom(type)
                || type == char.class || type == Character.class) {
            return "text";
        }

        if(type == byte.class || type == Byte.class
                || type == short.class || type == Short.class
                || type == int.class || type == Integer.class
                || type == long.class || type == Long.class
                || type == Date.class || type == Time.class
                || type == Timestamp.class) {
            return "integer";
        }

        if(type == float.class || type == Float.class
                || type == double.class || type == Double.class) {
            return "real";
        }

        return "blob";
    }

}
