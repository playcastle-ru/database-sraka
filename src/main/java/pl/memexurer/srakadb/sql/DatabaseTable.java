package pl.memexurer.srakadb.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseTable<T> {

  private final String name;
  private final Map<String, DatabaseTableRow> datatypeTableMap;
  private final TableInformationProvider<T> tableInformationProvider;

  private Connection connection;

  public DatabaseTable(String name, TableInformationProvider<T> tableInformationProvider) {
    this.name = name;
    this.datatypeTableMap = new LinkedHashMap<>();
    this.tableInformationProvider = tableInformationProvider;
  }

  void initializeTable(Connection connection) throws SQLException {
    if (this.connection != null) {
      throw new IllegalArgumentException("Table already initialized!");
    }

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
        if (++counter != datatypeTableMap.size()) {
          stringBuilder.append(',');
        } else {
          stringBuilder.append(')');
        }
      }
      statement.executeUpdate(stringBuilder.toString());
    }
  }

  public DatabasePreparedTransaction createUpdateAllColumnsTransaction()
      throws DatabaseTransactionError {
    String preparedFields = "?,".repeat(datatypeTableMap.values().size());
    PreparedStatement statement;
    try {
      statement = connection.prepareStatement(
          "REPLACE INTO " + name + " VALUES (" + preparedFields.substring(0,
              preparedFields.length() - 1) + ")");
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    return new DatabasePreparedTransaction(statement, this);
  }

  public DatabasePreparedTransaction createFiledUpdateAllColumnsTransaction(T tValue) {
    DatabasePreparedTransaction transaction = createUpdateAllColumnsTransaction();
    tableInformationProvider.fillAllRows(tValue, transaction);
    return transaction;
  }

  public DatabaseQueryTransaction<T> createQueryAllRowsTransaction()
      throws DatabaseTransactionError {
    Statement statement;
    try {
      statement = connection.createStatement();
      statement.executeQuery("SELECT * FROM " + name);
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    return new DatabaseQueryTransaction<>(statement, tableInformationProvider);
  }

  public DatabaseQueryTransaction<T> createQuery(String columnName, Object value)
      throws DatabaseTransactionError {
    PreparedStatement statement;
    try {
      statement = connection.prepareStatement(
          "SELECT * FROM " + name + " WHERE " + columnName + "=?");
      statement.setObject(1, value);
      statement.executeQuery();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    return new DatabaseQueryTransaction<>(statement, tableInformationProvider);
  }

  int getColumnIndex(String columnName) {
    int counter = 0;
    for (String str : datatypeTableMap.keySet()) {
      if (str.equals(columnName)) {
        return counter;
      }
      counter++;
    }
    throw new IllegalArgumentException("Unknown column " + columnName);
  }

  public String getName() {
    return name;
  }

  public static class TableBuilder {
    private final Map<String, DatabaseTableRow> tableRowMap;

    public TableBuilder(
        Map<String, DatabaseTableRow> tableRowMap) {
      this.tableRowMap = tableRowMap;
      //rows.name(), rows.deserializer().getDataType(), rows.primary(), rows.nullable()
    }

    public void addColumn(String name, String datatype, boolean primary, boolean nullable) {
      this.tableRowMap.put(name, new DatabaseTableRow(name, () -> datatype, primary, nullable));
    }
  }
}
