package net.njsharpe.dblang.object;

import lombok.Getter;
import net.njsharpe.dblang.DialectProvider;
import net.njsharpe.dblang.Reflect;

import java.lang.reflect.Field;

@Getter
public class ColumnDefinition {

    private final TableDefinition tableDefinition;
    private final DialectProvider dialectProvider;
    private final Class<?> type;
    private final String name;
    private final String sqlType;

    private final boolean isPrimaryKey;
    private final boolean isAutoIncrement;
    private final boolean isNullable;

    public ColumnDefinition(TableDefinition tableDefinition, Field field) {
        this.tableDefinition = tableDefinition;
        this.dialectProvider = tableDefinition.getDialectProvider();
        this.type = field.getType();
        this.name = Reflect.getAliasOr(field, field.getName());
        this.sqlType = this.dialectProvider.getSqlTypeFromType(this.type);
        this.isPrimaryKey = Reflect.isPrimaryKey(field);
        this.isAutoIncrement = Reflect.isAutoIncrement(field);
        this.isNullable = Reflect.isNullable(field);
    }

}
