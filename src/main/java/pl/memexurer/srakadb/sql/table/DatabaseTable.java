package pl.memexurer.srakadb.sql.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import pl.memexurer.srakadb.sql.table.query.DatabaseFetchQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseQueryColumn;
import pl.memexurer.srakadb.sql.table.query.DatabaseQueryPair;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery;

public class DatabaseTable<T> {

  private final String tableName;
  private final Map<String, DatabaseTableColumn> datatypeTableMap;
  private final TableInformationProvider<T> tableInformationProvider;

  private Connection connection;

  public DatabaseTable(String tableName, TableInformationProvider<T> tableInformationProvider) {
    this.tableName = tableName;
    this.datatypeTableMap = new LinkedHashMap<>();

    this.tableInformationProvider = tableInformationProvider;
    this.tableInformationProvider.generateTable(datatypeTableMap);
  }

  public void initializeTable(Connection connection) throws DatabaseTransactionError {
    if (this.connection != null) {
      throw new IllegalArgumentException("Table already initialized!");
    }

    this.connection = connection;

    StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    stringBuilder.append(tableName);
    stringBuilder.append("(");

    int counter = 0;
    for (DatabaseTableColumn tableRow : datatypeTableMap.values()) {
      stringBuilder.append(tableRow.getColumnName());
      stringBuilder.append(' ');
      stringBuilder.append(tableRow.getDatatype().sqlString());
      if (tableRow.isPrimary()) {
        stringBuilder.append(' ');
        stringBuilder.append("PRIMARY KEY");
      }
      if (!tableRow.isNullable()) {
        stringBuilder.append(' ');
        stringBuilder.append("NOT NULL");
      }
      if (++counter != datatypeTableMap.size()) {
        stringBuilder.append(',');
      } else {
        stringBuilder.append(')');
      }
    }

    executeUpdate(stringBuilder.toString());
  }

  private void executeUpdate(String query) throws DatabaseTransactionError {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException ex) {
      throw new DatabaseTransactionError(ex);
    }
  }

  private void validateQueryPairs(DatabaseQueryPair[] queryPairs) {
    for (DatabaseQueryPair queryPair : queryPairs) {
      if (!datatypeTableMap.containsKey(queryPair.column().getName())) {
        throw new IllegalArgumentException(
            "Table doesn't contain " + queryPair.column().getName());
      }
    }
  }

  private void validateQueryColumns(DatabaseQueryColumn[] queryPairs) {
    for (DatabaseQueryColumn queryPair : queryPairs) {
      if (!datatypeTableMap.containsKey(queryPair.getName())) {
        throw new IllegalArgumentException(
            "Table doesn't contain " + queryPair.getName());
      }
    }
  }

  public DatabaseQueryTransaction<?> executeFetchQuery(DatabaseFetchQuery fetchQuery) {
    if(fetchQuery.getColumns() == null)
      throw new IllegalArgumentException("DatabaseFetchQuery columns should not be null!");

    validateQueryColumns(fetchQuery.getColumns());
    if(fetchQuery.getPreconditions() != null)
      validateQueryPairs(fetchQuery.getPreconditions());

    StringBuilder builder = new StringBuilder("SELECT ");
    if (fetchQuery.getColumns() == null) {
      builder.append("* ");
    } else {
      builder.append('(').append(
              Arrays.stream(fetchQuery.getColumns())
                  .map(DatabaseQueryColumn::getName)
                  .collect(Collectors.joining(",")))
          .append(")");
    }

    builder.append(" FROM ").append(tableName).append(' ');

    if (fetchQuery.getPreconditions() != null) {
      builder.append("WHERE ").append(
          Arrays.stream(fetchQuery.getPreconditions())
              .map(pair -> pair.column().getName() + "=?")
              .collect(Collectors.joining(" AND "))
      );
    }

    PreparedStatement statement;
    try {
      statement = connection.prepareStatement(builder.toString());
      for (int i = 1; i < fetchQuery.getPreconditions().length; i++) {
        statement.setObject(i, fetchQuery.getPreconditions()[i - 1]);
      }

      statement.executeQuery();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    return new DatabaseQueryTransaction<>(statement, tableInformationProvider);
  }

  public DatabasePreparedTransaction executeInsertQuery(DatabaseInsertQuery updateQuery) {
    if (updateQuery.getValues() == null) {
      throw new IllegalArgumentException("DatabaseInsertQuery values should not be null!");
    }

    validateQueryPairs(updateQuery.getValues());
    if(updateQuery.getPreconditions() != null)
      validateQueryPairs(updateQuery.getPreconditions());

    StringBuilder builder = new StringBuilder(updateQuery.getUpdateType().name());

    builder.append(" INTO ").append(tableName)
        .append("(").append(Arrays.stream(updateQuery.getValues())
            .map(DatabaseQueryPair::column)
            .map(DatabaseQueryColumn::getName)
            .collect(Collectors.joining(",")))
        .append(") VALUES (").append("?,".repeat(updateQuery.getValues().length))
        .deleteCharAt(builder.length()).append(')');

    if (updateQuery.getPreconditions() != null) {
      builder.append("WHERE ").append(
          Arrays.stream(updateQuery.getPreconditions())
              .map(pair -> pair.column().getName() + "=?")
              .collect(Collectors.joining(" AND "))
      );
    }

    PreparedStatement statement;
    try {
      statement = connection.prepareStatement(builder.toString());
      for (int i = 1; i < updateQuery.getValues().length; i++) {
        statement.setObject(i, updateQuery.getValues()[i - 1]);
      }

    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    return new DatabasePreparedTransaction(statement, this);
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
}
