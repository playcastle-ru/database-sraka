package pl.memexurer.srakadb.sql.table.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseQueryTransaction;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class DatabaseFetchQuery implements DatabaseQuery {

  private DatabaseQueryPair[] preconditions;

  public DatabaseFetchQuery precondition(DatabaseQueryPair... preconditions) {
    this.preconditions = preconditions;
    return this;
  }

  public <T> DatabaseQueryTransaction<T> executeFetchQuery(DatabaseTable<T> databaseTable) throws DatabaseTransactionError {
    StringBuilder builder = new StringBuilder("SELECT *");

    builder.append(" FROM ").append(databaseTable.getTableName()).append(' ');

    if (this.preconditions != null) {
      builder.append("WHERE ").append(
          Arrays.stream(this.preconditions)
              .map(pair -> pair.column().getColumnName() + "=?")
              .collect(Collectors.joining(" AND "))
      );
    }

    PreparedStatement statement;
    try {
      statement = databaseTable.prepareStatement(builder.toString());
      for (int i = 1; i < this.preconditions.length; i++) {
        statement.setObject(i, this.preconditions[i - 1]);
      }

      statement.executeQuery();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }

    return databaseTable.queryTransaction(statement);
  }

}
