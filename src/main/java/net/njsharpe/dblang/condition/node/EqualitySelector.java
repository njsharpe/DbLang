package net.njsharpe.dblang.condition.node;

import net.njsharpe.dblang.object.ColumnDefinition;
import net.njsharpe.dblang.object.TableDefinition;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public interface EqualitySelector {

    TableDefinition getFrom();

    default EqualityNode equal(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.EQUAL, value);
    }

    default EqualityNode notEqual(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.NOT_EQUAL, value);
    }

    default EqualityNode lessThan(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.LESS_THAN, value);
    }

    default EqualityNode greaterThan(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.GREATER_THAN, value);
    }

    default EqualityNode lessThanOrEqual(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.LESS_THAN_OR_EQUAL, value);
    }

    default EqualityNode greaterThanOrEqual(String field, Object value) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.GREATER_THAN_OR_EQUAL, value);
    }

    default EqualityNode isNull(String field) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.NULL, null);
    }

    default EqualityNode isNotNull(String field) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new EqualityNode(colDef, Node.Operator.NOT_NULL, null);
    }

    default BiEqualityNode between(String field, byte minimum, byte maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, short minimum, short maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, int minimum, int maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, long minimum, long maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, Date minimum, Date maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, Time minimum, Time maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    default BiEqualityNode between(String field, Timestamp minimum, Timestamp maximum) {
        ColumnDefinition colDef = this.getColumnDefinitionFromField(field);
        return new BiEqualityNode(colDef, Node.Operator.BETWEEN, minimum, maximum);
    }

    private ColumnDefinition getColumnDefinitionFromField(String field) {
        return this.getFrom().getColumnDefinitions().stream()
                .filter(cd -> cd.getName().equalsIgnoreCase(field))
                .findFirst().orElseThrow();
    }

}
