package pl.memexurer.srakadb.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseTable<T> {
    private final String name;
    private final Map<String, DatabaseTableRow> datatypeTableMap;
    private final ResultSetDeserializer<T> resultSetDeserializer;

    private Connection connection;

    private DatabaseTable(String name, Map<String, DatabaseTableRow> datatypeTableMap, ResultSetDeserializer<T> resultSetDeserializer) {
        this.name = name;
        this.datatypeTableMap = datatypeTableMap;
        this.resultSetDeserializer = resultSetDeserializer;
    }

    void initializeTable(Connection connection) throws SQLException {
        if(this.connection != null)
            throw new IllegalArgumentException("Table already initialized!");

        this.connection = connection;

        try (Statement statement = connection.createStatement()) {
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            stringBuilder.append(name);
            stringBuilder.append("(");

            int counter = 0;
            for (DatabaseTableRow tableRow : datatypeTableMap.values()) {
                stringBuilder.append(tableRow.rowName());
                stringBuilder.append(' ');
                stringBuilder.append(tableRow.datatype().sqlString());
                if (tableRow.isPrimary()) {
                    stringBuilder.append(' ');
                    stringBuilder.append("PRIMARY KEY");
                }
                if (!tableRow.nullable()) {
                    stringBuilder.append(' ');
                    stringBuilder.append("NOT NULL");
                }
                if (++counter != datatypeTableMap.size())
                    stringBuilder.append(',');
                else
                    stringBuilder.append(')');
            }
            statement.executeUpdate(stringBuilder.toString());
        }
    }

    public DatabasePreparedTransaction createUpdateAllColumnsTransaction() throws DatabaseTransactionError {
        String preparedFields = "?,".repeat(datatypeTableMap.values().size());
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("REPLACE INTO " + name + " VALUES (" + preparedFields.substring(0, preparedFields.length() - 1) + ")");
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
        return new DatabasePreparedTransaction(statement, this);
    }

    public DatabaseQueryTransaction<T> createQueryAllRowsTransaction() throws DatabaseTransactionError {
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM " + name);
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
        return new DatabaseQueryTransaction<>(statement, resultSetDeserializer);
    }

    public DatabaseQueryTransaction<T> createQuery(String columnName, Object value) throws DatabaseTransactionError {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement("SELECT * FROM " + name + " WHERE " + columnName + "=?");
            statement.setObject(1, value);
            statement.executeQuery();
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
        return new DatabaseQueryTransaction<>(statement, resultSetDeserializer);
    }

    int getColumnIndex(String columnName) {
        int counter = 0;
        for (String str : datatypeTableMap.keySet()) {
            if (str.equals(columnName))
                return counter;
            counter++;
        }
        throw new IllegalArgumentException("Unknown column " + columnName);
    }

    public String getName() {
        return name;
    }

    public static class Builder<T> {
        private final String name;
        private final Map<String, DatabaseTableRow> datatypeTableMap;
        private final ResultSetDeserializer<T> resultSetDeserializer;

        public Builder(Map<String, DatabaseTableRow> datatypeTableMap, String name, ResultSetDeserializer<T> resultSetDeserializer) {
            this.datatypeTableMap = datatypeTableMap;
            this.name = name;
            this.resultSetDeserializer = resultSetDeserializer;
        }

        public Builder(String name, ResultSetDeserializer<T> deserializer) {
            this(new LinkedHashMap<>(), name, deserializer);
        }

        public Builder<T> addPrimaryColumn(String name, DatabaseDatatype databaseDatatype) {
            this.datatypeTableMap.put(name, new DatabaseTableRow(name, databaseDatatype, true, false));
            return this;
        }

        public Builder<T> addNullablePrimaryColumn(String name, DatabaseDatatype databaseDatatype) {
            this.datatypeTableMap.put(name, new DatabaseTableRow(name, databaseDatatype, true, true));
            return this;
        }

        public Builder<T> addNullableColumn(String name, DatabaseDatatype databaseDatatype) {
            this.datatypeTableMap.put(name, new DatabaseTableRow(name, databaseDatatype, false, true));
            return this;
        }

        public Builder<T> addColumn(String name, DatabaseDatatype databaseDatatype) {
            this.datatypeTableMap.put(name, new DatabaseTableRow(name, databaseDatatype, false, false));
            return this;
        }

        public DatabaseTable<T> build() {
            return new DatabaseTable<>(name, this.datatypeTableMap, resultSetDeserializer);
        }
    }
}
