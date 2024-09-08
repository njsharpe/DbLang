package net.njsharpe.dblang.condition.node;

import net.njsharpe.dblang.object.TableDefinition;

import java.util.function.Function;

public interface ConditionSelector {

    TableDefinition getFrom();
    Node getSelf();

    default ConditionNode and(Function<SupplierNode, Node> function) {
        SupplierNode node = new SupplierNode(this.getFrom());
        return new ConditionNode(this.getFrom(), this.getSelf(), Node.Condition.AND, function.apply(node));
    }

    default ConditionNode or(Function<SupplierNode, Node> function) {
        SupplierNode node = new SupplierNode(this.getFrom());
        return new ConditionNode(this.getFrom(), this.getSelf(), Node.Condition.OR, function.apply(node));
    }

}
