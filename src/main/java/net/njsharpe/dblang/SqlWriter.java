package net.njsharpe.dblang;

import org.jetbrains.annotations.Contract;

import java.sql.*;
import java.util.Map;

public class SqlWriter {

    @Contract(mutates = "param1")
    public static void writeObject(PreparedStatement statement, Map<Integer, Object> values) throws SQLException {
        for(Map.Entry<Integer, Object> entry : values.entrySet()) {
            int key = entry.getKey();
            Object value = entry.getValue();
            writeSingleValue(statement, key, value);
        }
    }

    @Contract(mutates = "param1")
    private static void writeSingleValue(PreparedStatement statement, Integer key, Object value) throws SQLException {
        if(value instanceof String s) {
            statement.setString(key, s);
        } else if(value instanceof Character c) {
            statement.setString(key, c.toString());
        } else if(value instanceof Byte b) {
            statement.setByte(key, b);
        } else if(value instanceof Short s) {
            statement.setShort(key, s);
        } else if(value instanceof Integer i) {
            statement.setInt(key, i);
        } else if(value instanceof Long l) {
            statement.setLong(key, l);
        } else if(value instanceof Float f) {
            statement.setFloat(key, f);
        } else if(value instanceof Double d) {
            statement.setDouble(key, d);
        } else if(value instanceof Date d) {
            statement.setDate(key, d);
        } else if(value instanceof Time t) {
            statement.setTime(key, t);
        } else if(value instanceof Timestamp t) {
            statement.setTimestamp(key, t);
        } else {
            statement.setObject(key, value);
        }
    }

}
