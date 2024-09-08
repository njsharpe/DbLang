package net.njsharpe.dblang;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ResultSetWrapper {

    private final Map<String, Integer> columnMap;

    public ResultSetWrapper(ResultSet resultSet) throws SQLException {
        this.columnMap = new HashMap<>();
        this.init(resultSet);
    }

    private void init(ResultSet resultSet) throws SQLException {
        ResultSetMetaData md = resultSet.getMetaData();
        int columnCount = md.getColumnCount();
        for (int index = 1; index <= columnCount; index++) {
            String columnName = md.getColumnLabel(index);
            if (!columnMap.containsKey(columnName)) {
                columnMap.put(columnName, index);
            }

            String tableAlias = md.getTableName(index);
            if (tableAlias != null && !tableAlias.trim().isEmpty()) {
                columnMap.put(tableAlias + "." + columnName, index);
            }
        }
    }

    public Integer getColumnIndex(String columnName) {
        return columnMap.get(columnName);
    }

    public Integer getColumnIndex(String tableAlias, String columnName) {
        return columnMap.get(tableAlias + "." + columnName);
    }

}
