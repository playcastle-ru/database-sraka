package pl.memexurer.srakadb.sql.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import pl.memexurer.srakadb.sql.mapper.DataModelMapper;
import pl.memexurer.srakadb.sql.table.query.DatabaseFetchQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery;
import pl.memexurer.srakadb.sql.table.transaction.DatabasePreparedTransaction;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseQueryTransaction;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseUpdateTransaction;

public class DatabaseTable<T> {

  private final String tableName;
  private final DataModelMapper<T> modelMapper;

  private Connection connection;

  public DatabaseTable(String tableName, Class<T> modelClass) {
    this.tableName = tableName;
    this.modelMapper = new DataModelMapper<>(modelClass);
  }

  public void initializeTable(Connection connection) throws DatabaseTransactionError {
    if (this.connection != null) {
      throw new IllegalArgumentException("Table already initialized!");
    }

    this.connection = connection;

    StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    stringBuilder.append(tableName);
    stringBuilder.append("(");

    for (DatabaseTableColumn tableRow : modelMapper.getColumns()) {
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

      stringBuilder.append(',');
    }

    stringBuilder.deleteCharAt(stringBuilder.length());
    stringBuilder.append(')');

    executeUpdate(stringBuilder.toString());
  }

  public DatabaseUpdateTransaction<?> executeInsertQuery(DatabaseInsertQuery insertQuery) {
    return insertQuery.execute(this);
  }

  public DatabaseQueryTransaction<T> executeFetchQuery(DatabaseFetchQuery fetchQuery) {
    return fetchQuery.executeFetchQuery(this);
  }

  private void executeUpdate(String query) throws DatabaseTransactionError {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException ex) {
      throw new DatabaseTransactionError(ex);
    }
  }

  public PreparedStatement prepareStatement(String query) {
    try {
      return connection.prepareStatement(query);
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
  }

  public DatabasePreparedTransaction<T> prepareTransaction(PreparedStatement statement) {
    return new DatabasePreparedTransaction<>(statement, modelMapper);
  }

  public <S extends Statement> DatabaseUpdateTransaction<S> updateTransaction(S statement) {
    return new DatabaseUpdateTransaction<>(statement);
  }

  public DatabaseQueryTransaction<T> queryTransaction(Statement statement) {
    return new DatabaseQueryTransaction<>(statement, modelMapper);
  }

  public DataModelMapper<T> getModelMapper() {
    return modelMapper;
  }

  public String getTableName() {
    return tableName;
  }
}
