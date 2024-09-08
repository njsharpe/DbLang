package net.njsharpe.dblang;

import net.njsharpe.dblang.annotation.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Reflect {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reflect.class);

    public static String getAliasOr(AnnotatedElement element, String fallback) {
        if(element.isAnnotationPresent(Alias.class)) {
            Alias alias = element.getAnnotation(Alias.class);
            return alias.value();
        }
        return fallback;
    }

    public static String getSchemaOr(AnnotatedElement element, DialectProvider provider) {
        if(element.isAnnotationPresent(Schema.class)) {
            Schema schema = element.getAnnotation(Schema.class);
            return schema.value();
        }
        return provider.getDefaultSchemaName();
    }

    public static String getFieldNameFromAlias(Class<?> type, String name) {
        for(Field field : type.getDeclaredFields()) {
            if(field.isAnnotationPresent(Alias.class)) {
                Alias alias = field.getAnnotation(Alias.class);
                if(alias.value().equals(name)) {
                    return field.getName();
                }
            }
        }
        return name;
    }

    public static boolean isAutoIncrement(Field field) {
        return field.isAnnotationPresent(AutoIncrement.class);
    }

    public static boolean isNullable(Field field) {
        Class<?> type = field.getType();
        return !field.isAnnotationPresent(Required.class) && (!type.isPrimitive() && !type.isEnum());
    }

    public static List<Field> getSerializableFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        try {
            for(Field field : clazz.getDeclaredFields()) {
                if(field.isAnnotationPresent(Ignore.class) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                fields.add(field);
            }
            return fields;
        } catch (Exception ex) {
            LOGGER.error("Could not get serializable fields", ex);
        }

        return new ArrayList<>();
    }

    public static Class<?> getFieldDataSource(Field field, Class<?> defaultSource) {
        if(field.isAnnotationPresent(BelongsTo.class)) {
            BelongsTo belongsTo = field.getAnnotation(BelongsTo.class);
            return belongsTo.value();
        }
        return defaultSource;
    }

    public static boolean isPrimaryKey(Field field) {
        return field.isAnnotationPresent(PrimaryKey.class) || field.getName().equalsIgnoreCase("id");
    }

}
