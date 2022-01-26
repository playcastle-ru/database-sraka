package pl.memexurer.srakadb.sql.table.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.DatabaseTableColumn;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class DatabaseBulkInsertQuery implements DatabaseQuery {

  private final UpdateType updateType;
  private Collection<DatabaseQueryPair[]> values;

  public DatabaseBulkInsertQuery(
      UpdateType updateType) {
    this.updateType = updateType;
  }

  public DatabaseBulkInsertQuery values(Collection<DatabaseQueryPair[]> values) {
    this.values = values;
    return this;
  }

  public void execute(DatabaseTable<?> table)
      throws DatabaseTransactionError {
    if (this.values == null) {
      throw new IllegalArgumentException("DatabaseInsertQuery values should not be null!");
    }

    DatabaseTableColumn[] columns = getColumns();

    StringBuilder builder = new StringBuilder(this.updateType.name());
    builder.append(" INTO ").append(table.getTableName())
        .append("(").append(Arrays.stream(columns)
            .map(DatabaseTableColumn::getColumnName)
            .collect(Collectors.joining(",")))
        .append(") VALUES (").append("?,".repeat(columns.length))
        .deleteCharAt(builder.length() - 1).append(") ");

    try (Connection connection = table.getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.toString())) {
      for(DatabaseQueryPair[] values: this.values) {
        for (int i = 0; i < values.length; i++) {
          var serialized = table.getModelMapper()
              .serializeItem(values[i].column(), values[i].value());
          statement.setObject(i + 1, serialized);
        }
        statement.addBatch();
      }

      statement.executeBatch();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
  }

  private DatabaseTableColumn[] getColumns() {
    return Arrays.stream(values.iterator().next())
        .map(DatabaseQueryPair::column)
        .toArray(DatabaseTableColumn[]::new);
  }

  public enum UpdateType {
    INSERT,
    REPLACE,
  }
}
