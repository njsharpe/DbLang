package net.njsharpe.dblang;

import net.njsharpe.dblang.query.Query;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Conversion {

    public static <T> List<T> fromResultSet(Class<T> resultType, Class<?> rootType, ResultSet resultSet) {
        List<T> results = new ArrayList<>();

        try(resultSet) {
            ResultSetWrapper set = new ResultSetWrapper(resultSet);
            Constructor<T> constructor = resultType.getDeclaredConstructor();
            List<Field> fields = Reflect.getSerializableFields(resultType);
            while(resultSet.next()) {
                T t = constructor.newInstance();
                for(Field field : fields) {
                    Class<?> source = Reflect.getFieldDataSource(field, rootType);

                    field.setAccessible(true);

                    String qualifiedName = Query.getQualifiedField(source, field);
                    int index = set.getColumnIndex(qualifiedName);

                    Object value;
                    if(field.getType() == Date.class) {
                        value = resultSet.getDate(index);
                    } else if(field.getType() == Time.class) {
                        value = resultSet.getTime(index);
                    } else if(field.getType() == Timestamp.class) {
                        value = resultSet.getTimestamp(index);
                    } else {
                        value = resultSet.getObject(index);
                    }

                    field.set(t, value);
                }
                results.add(t);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return results;
    }

}
