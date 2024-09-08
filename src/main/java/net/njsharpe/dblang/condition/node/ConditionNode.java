package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import net.njsharpe.dblang.object.TableDefinition;

@Getter
public class ConditionNode extends Node implements EqualitySelector {

    private final Node left;
    private final Condition condition;
    private final Node right;

    public ConditionNode(TableDefinition from, Node left, Condition condition, Node right) {
        super(from);
        this.left = left;
        this.condition = condition;
        this.right = right;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(this.left.toString(), this.condition.getTranslation(), this.right.toString());
    }
}
