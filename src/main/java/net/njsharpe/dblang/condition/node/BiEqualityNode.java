package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import net.njsharpe.dblang.object.ColumnDefinition;

@Getter
public class BiEqualityNode extends Node implements ConditionSelector {

    private final ColumnDefinition field;
    private final Operator operator;
    private final Object minimum;
    private final Object maximum;

    public BiEqualityNode(ColumnDefinition field, Operator operator, Object minimum, Object maximum) {
        super(field.getTableDefinition());
        this.field = field;
        this.operator = operator;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public Node getSelf() {
        return this;
    }

    @Override
    public String toString() {
        return "%s.%s %s %s and %s".formatted(this.getFrom().getName(), this.field.getName(),
                this.operator.getTranslation(),
                (this.isQuoted(this.minimum) ? "'%s'" : "%s").formatted(this.minimum),
                (this.isQuoted(this.maximum) ? "'%s'" : "%s").formatted(this.maximum));
    }
}
