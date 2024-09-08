package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import net.njsharpe.dblang.object.TableDefinition;

@Getter
public class BlockNode extends Node implements ConditionSelector {

    private final Node inner;

    public BlockNode(TableDefinition from, Node inner) {
        super(from);
        this.inner = inner;
    }

    @Override
    public Node getSelf() {
        return this;
    }

    @Override
    public String toString() {
        return "(%s)".formatted(this.inner.toString());
    }
}
