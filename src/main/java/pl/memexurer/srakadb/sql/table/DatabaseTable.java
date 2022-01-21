package pl.memexurer.srakadb.sql.table;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import pl.memexurer.srakadb.sql.mapper.DataModelMapper;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class DatabaseTable<T> {

  private final String tableName;
  private final DataModelMapper<T> modelMapper;

  private final HikariDataSource dataSource;

  public DatabaseTable(String tableName, HikariDataSource dataSource,
      Class<T> modelClass) {
    this.tableName = tableName;
    this.dataSource = dataSource;
    this.modelMapper = new DataModelMapper<>(modelClass);
  }

  public void initializeTable() throws DatabaseTransactionError {
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

    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    stringBuilder.append(')');

    executeUpdate(stringBuilder.toString());
  }

  public Connection getConnection() {
    try {
      return Objects.requireNonNull(dataSource.getConnection());
    } catch (SQLException ex) {
      throw new DatabaseTransactionError(ex);
    }
  }

  private void executeUpdate(String query) throws DatabaseTransactionError {
    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException ex) {
      throw new DatabaseTransactionError(ex);
    }
  }

  public DataModelMapper<T> getModelMapper() {
    return modelMapper;
  }

  public String getTableName() {
    return tableName;
  }
}
