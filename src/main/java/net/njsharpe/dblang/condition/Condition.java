package net.njsharpe.dblang.condition;

import net.njsharpe.dblang.Cache;
import net.njsharpe.dblang.condition.node.*;
import net.njsharpe.dblang.object.TableDefinition;

import java.util.function.Function;

public interface Condition {

    static FromSelector join(Class<?> to, Class<?> from) {
        return new JoinCondition(to, from);
    }

    static WhereCondition where(Class<?> from, Function<SupplierNode, Node> function) {
        TableDefinition tableDef = Cache.getOrAddTable(from);
        SupplierNode node = new SupplierNode(tableDef);
        Node result = function.apply(node);
        return new WhereCondition(tableDef, result);
    }

}
