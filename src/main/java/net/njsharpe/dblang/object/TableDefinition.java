package net.njsharpe.dblang.object;

import lombok.Getter;
import lombok.Setter;
import net.njsharpe.dblang.Defaults;
import net.njsharpe.dblang.DialectProvider;
import net.njsharpe.dblang.Reflect;

import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class TableDefinition {

    private final Class<?> type;
    private final String schema;
    private final String name;
    private final Collection<ColumnDefinition> columnDefinitions;

    @Setter
    private DialectProvider dialectProvider;

    public TableDefinition(Class<?> type) {
        this.type = type;
        this.schema = Reflect.getSchemaOr(type, this.getDialectProvider());
        this.name = Reflect.getAliasOr(type, type.getSimpleName());
        this.columnDefinitions = this.initColumnDefinitions();
    }

    private Collection<ColumnDefinition> initColumnDefinitions() {
        try {
            Collection<ColumnDefinition> columns = new ArrayList<>();
            for(Field field : Reflect.getSerializableFields(this.type)) {
                ColumnDefinition columnDefinition = new ColumnDefinition(this, field);
                columns.add(columnDefinition);
            }
            return columns;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public DialectProvider getDialectProvider() {
        return this.dialectProvider = (this.dialectProvider == null
                ? Defaults.getDialectProvider()
                : this.dialectProvider);
    }

}
