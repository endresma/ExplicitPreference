/*
 *  Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package flatlc.inputrelations;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class RandomResultSetMetaData implements ResultSetMetaData, Serializable {
    String[] columns;

    public RandomResultSetMetaData(int[] maxValues) {
        this(maxValues.length);
    }

    public RandomResultSetMetaData(int columnCount) {
        columns = new String[columnCount + 1];
        for (int i = columnCount; --i >= 0; ) {
            columns[i] = new StringBuffer(8).append("myCol").append(i)
                    .toString();
        }
        columns[columnCount] = "id";
    }

    public String getCatalogName(int column) {
        return null;
    }

    public String getColumnClassName(int column) {
        return "int";
    }

    public int getColumnCount() {
        return columns.length + 1;
    }

    public int getColumnDisplaySize(int column) {
        return 6;
    }

    public String getColumnLabel(int column) {
        return columns[column];
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getColumnType(int column) {
        return java.sql.Types.INTEGER;
    }

    public String getColumnTypeName(int column) {
        return "int";
    }

    public int getPrecision(int column) {
        return 0;
    }

    public int getScale(int column) {
        return 0;
    }

    public String getSchemaName(int column) {
        return "randomSchema";
    }

    public String getTableName(int column) {
        return "randomTable";
    }

    public boolean isAutoIncrement(int column) {
        return column == 1;
    }

    public boolean isCaseSensitive(int column) {
        return false;
    }

    public boolean isCurrency(int column) {
        return false;
    }

    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    public int isNullable(int column) {
        return ResultSetMetaData.columnNoNulls;
    }

    public boolean isReadOnly(int column) {
        return true;
    }

    public boolean isSearchable(int column) {
        return false;
    }

    public boolean isSigned(int column) {
        return true;
    }

    public boolean isWritable(int column) {
        return false;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}