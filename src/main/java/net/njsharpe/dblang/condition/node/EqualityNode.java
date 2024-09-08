package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import net.njsharpe.dblang.object.ColumnDefinition;

@Getter
public class EqualityNode extends Node implements ConditionSelector {

    private final ColumnDefinition field;
    private final Operator operator;
    private final Object value;

    public EqualityNode(ColumnDefinition field, Operator operator, Object value) {
        super(field.getTableDefinition());
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public Node getSelf() {
        return this;
    }

    @Override
    public String toString() {
        return "%s.%s %s %s".formatted(this.getFrom().getName(), this.field.getName(), this.operator.getTranslation(),
                (this.isQuoted(this.value) ? "'%s'" : "%s").formatted(this.value == null ? "null" : this.value));
    }
}
