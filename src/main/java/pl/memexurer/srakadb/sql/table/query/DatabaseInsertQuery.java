package pl.memexurer.srakadb.sql.table.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.DatabaseTableColumn;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseUpdateTransaction;

public class DatabaseInsertQuery implements DatabaseQuery {

  private final UpdateType updateType;
  private DatabaseQueryPair[] values;
  private DatabaseQueryPair[] preconditions;

  public DatabaseInsertQuery(
      UpdateType updateType) {
    this.updateType = updateType;
  }

  public DatabaseInsertQuery preconditions(DatabaseQueryPair... pairs) {
    this.preconditions = pairs;
    return this;
  }

  public DatabaseInsertQuery values(DatabaseQueryPair... pairs) {
    this.values = pairs;
    return this;
  }

  public DatabaseUpdateTransaction<?> execute(DatabaseTable<?> table) throws DatabaseTransactionError {
    if (this.values == null) {
      throw new IllegalArgumentException("DatabaseInsertQuery values should not be null!");
    }

    StringBuilder builder = new StringBuilder(this.updateType.name());

    builder.append(" INTO ").append(table.getTableName())
        .append("(").append(Arrays.stream(this.values)
            .map(DatabaseQueryPair::column)
            .map(DatabaseTableColumn::getColumnName)
            .collect(Collectors.joining(",")))
        .append(") VALUES (").append("?,".repeat(this.values.length))
        .deleteCharAt(builder.length() - 1).append(')');

    if (this.preconditions != null) {
      builder.append("WHERE ").append(
          Arrays.stream(this.preconditions)
              .map(pair -> pair.column().getColumnName() + "=?")
              .collect(Collectors.joining(" AND "))
      );
    }

    PreparedStatement statement;
    try {
      statement = table.prepareStatement(builder.toString());
      for (int i = 1; i < this.preconditions.length; i++) {
        statement.setObject(i, this.preconditions[i - 1]);
      }

      statement.executeQuery();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }

    return table.updateTransaction(statement);
  }

  public enum UpdateType {
    INSERT,
    REPLACE,
  }
}
