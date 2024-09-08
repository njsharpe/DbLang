package net.njsharpe.dblang.query;

import lombok.Getter;
import net.njsharpe.dblang.Reflect;
import net.njsharpe.dblang.condition.FromSelector;
import net.njsharpe.dblang.condition.JoinCondition;
import net.njsharpe.dblang.condition.WhereCondition;
import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import org.apache.commons.lang3.function.TriFunction;

import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Query<T> {

    Class<T> getType();
    Class<?> getSourceType();

    WhereCondition getWhere();
    Query<T> where(Function<SupplierNode, Node> function);
    <U> Query<T> where(Class<U> type, Function<SupplierNode, Node> function);

    int getLimit();
    Query<T> limit(int n);

    List<JoinCondition> getJoins();
    <U> Query<T> join(Class<U> type, Function<FromSelector, JoinCondition> function);

    <R> Query<R> select(Class<R> type);

    List<OrderBy> getOrders();
    Query<T> orderBy(String... fieldNames);
    <R> Query<T> orderBy(Class<R> type, String... fieldNames);

    Query<T> orderByDescending(String... fieldNames);
    <R> Query<T> orderByDescending(Class<R> type, String... fieldNames);

    static String getQualifiedField(String table, Field field) {
        return "%s.%s".formatted(table, Reflect.getAliasOr(field, field.getName()));
    }

    static String getQualifiedField(Class<?> type, Field field) {
        return "%s.%s".formatted(Reflect.getAliasOr(type, type.getSimpleName()), Reflect.getAliasOr(field, field.getName()));
    }

    static <T> Map<Integer, Object> toValuesMap(Class<T> type, T t) {
        return toValuesMap(t, Reflect.getSerializableFields(type).stream()
                .filter(f -> !Reflect.isPrimaryKey(f)).toList());
    }

    static <T> Map<Integer, Object> toValuesMap(Class<T> type, T t, String[] fieldNames) {
        Map<Integer, Object> map = new HashMap<>();

        for(int i = 0; i < fieldNames.length; i++) {
            try {
                String fieldName = Reflect.getFieldNameFromAlias(type, fieldNames[i]);
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                map.put(i + 1, field.get(t));
            } catch (Exception ex) {
                continue;
            }
        }

        return map;
    }

    static <T> Map<Integer, Object> toValuesMap(T t, List<Field> fields) {
        Map<Integer, Object> map = new HashMap<>();

        for(int i = 0; i < fields.size(); i++) {
            try {
                Field field = fields.get(i);
                field.setAccessible(true);
                map.put(i + 1, field.get(t));
            } catch (Exception ex) {
                continue;
            }
        }

        return map;
    }

}
