package net.njsharpe.dblang.query;

import lombok.Getter;
import net.njsharpe.dblang.DialectProvider;
import net.njsharpe.dblang.condition.Condition;
import net.njsharpe.dblang.condition.FromSelector;
import net.njsharpe.dblang.condition.JoinCondition;
import net.njsharpe.dblang.condition.WhereCondition;
import net.njsharpe.dblang.condition.node.Node;
import net.njsharpe.dblang.condition.node.SupplierNode;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SqlQuery<T> implements Query<T> {

    @Getter
    private final Class<T> type;

    @Getter
    private final Class<?> sourceType;

    @Getter
    private final DialectProvider provider;

    @Getter
    private int limit;

    @Getter
    private List<JoinCondition> joins = new ArrayList<>();

    @Getter
    private WhereCondition where;

    @Getter
    private List<OrderBy> orders = new ArrayList<>();

    public SqlQuery(Class<T> type, SqlQuery<?> query) {
        this.provider = query.provider;
        this.type = type;
        this.sourceType = query.sourceType;
        this.limit = query.limit;
        this.joins = query.joins;
        this.where = query.where;
        this.orders = query.orders;
    }

    public SqlQuery(DialectProvider provider, Class<T> type) {
        this.provider = provider;
        this.type = type;
        this.sourceType = type;
    }

    @Override
    public Query<T> where(Function<SupplierNode, Node> function) {
        WhereCondition condition = Condition.where(this.type, function);
        this.where = this.where == null ? condition : this.where.join(condition);
        return this;
    }

    @Override
    public <U> Query<T> where(Class<U> type, Function<SupplierNode, Node> function) {
        WhereCondition condition = Condition.where(type, function);
        this.where = this.where == null ? condition : this.where.join(condition);
        return this;
    }

    @Override
    public Query<T> limit(int n) {
        Validate.inclusiveBetween(1, Integer.MAX_VALUE, n);
        this.limit = n;
        return this;
    }

    @Override
    public <U> Query<T> join(Class<U> type, Function<FromSelector, JoinCondition> function) {
        FromSelector condition = Condition.join(this.type, type);
        this.joins.add(function.apply(condition));
        return this;
    }

    @Override
    public <R> Query<R> select(Class<R> type) {
        return new SqlQuery<>(type, this);
    }

    @Override
    public Query<T> orderBy(String... fieldNames) {
        for(String field : fieldNames) {
            OrderBy orderBy = new OrderBy(this.type, field, OrderBy.Direction.ASC);
            this.orders.add(orderBy);
        }
        return this;
    }

    @Override
    public <R> Query<T> orderBy(Class<R> type, String... fieldNames) {
        for(String field : fieldNames) {
            OrderBy orderBy = new OrderBy(type, field, OrderBy.Direction.ASC);
            this.orders.add(orderBy);
        }
        return this;
    }

    @Override
    public Query<T> orderByDescending(String... fieldNames) {
        for(String field : fieldNames) {
            OrderBy orderBy = new OrderBy(this.type, field, OrderBy.Direction.DESC);
            this.orders.add(orderBy);
        }
        return this;
    }

    @Override
    public <R> Query<T> orderByDescending(Class<R> type, String... fieldNames) {
        for(String field : fieldNames) {
            OrderBy orderBy = new OrderBy(type, field, OrderBy.Direction.DESC);
            this.orders.add(orderBy);
        }
        return this;
    }

}
