package net.njsharpe.dblang;

import net.njsharpe.dblang.object.TableDefinition;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static final Map<Class<?>, TableDefinition> TYPE_TABLE_MAP = new HashMap<>();

    public static TableDefinition getOrAddTable(Class<?> type) {
        return TYPE_TABLE_MAP.computeIfAbsent(type, TableDefinition::new);
    }

}
