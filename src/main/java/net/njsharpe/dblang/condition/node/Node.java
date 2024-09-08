package net.njsharpe.dblang.condition.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.njsharpe.dblang.object.TableDefinition;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Node {

    private final TableDefinition from;

    public Node append(Node node) {
        return new ConditionNode(this.from, this, Condition.AND, node);
    }

    public static SupplierNode from(TableDefinition from) {
        return new SupplierNode(from);
    }

    public enum Condition {

        AND("and"),
        OR("or")

        ;

        @Getter
        private final String translation;

        Condition(String translation) {
            this.translation = translation;
        }

    }

    public enum Operator {

        EQUAL("="),
        NOT_EQUAL("<>"),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        LESS_THAN_OR_EQUAL("<="),
        GREATER_THAN_OR_EQUAL(">="),
        NULL("is"),
        NOT_NULL("is not"),
        BETWEEN("between")

        ;

        @Getter
        private final String translation;

        Operator(String translation) {
            this.translation = translation;
        }

    }

    final boolean isQuoted(Object value) {
        return value instanceof CharSequence || value instanceof Character || value instanceof Date
                || value instanceof Time || value instanceof Timestamp;
    }

}
