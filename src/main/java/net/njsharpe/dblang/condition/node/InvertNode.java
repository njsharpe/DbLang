package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import net.njsharpe.dblang.object.TableDefinition;

@Getter
public class InvertNode extends Node {

    private final Node inner;

    public InvertNode(TableDefinition from, Node inner) {
        super(from);
        this.inner = inner;
    }

    @Override
    public String toString() {
        return "not %s".formatted(this.inner.toString());
    }
}
