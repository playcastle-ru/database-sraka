package pl.memexurer.srakadb.sql.table.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class DatabaseFetchQuery implements DatabaseQuery {

  private final List<QueryPair> preconditions = new ArrayList<>();

  public DatabaseFetchQuery and(DatabaseQueryPair precondition) {
    this.preconditions.add(new QueryPair(precondition, QueryType.AND));
    return this;
  }

  public DatabaseFetchQuery or(DatabaseQueryPair precondition) {
    this.preconditions.add(new QueryPair(precondition, QueryType.OR));
    return this;
  }

  public <T> List<T> executeFetchQuery(DatabaseTable<T> databaseTable)
      throws DatabaseTransactionError {
    StringBuilder builder = new StringBuilder("SELECT *");

    builder.append(" FROM ").append(databaseTable.getTableName()).append(' ');

    if (!this.preconditions.isEmpty()) {
      boolean first = true;

      builder.append("WHERE ");
      for (QueryPair pair : preconditions) {
        if(first) {
          builder.append(pair.queryPair().column().getColumnName()).append("=?");
          first = false;
        } else {
          builder.append(' ').append(pair.type().name()).append(' ');
          builder.append(pair.queryPair().column().getColumnName()).append("=?");
        }
      }
    }

    try (Connection connection = databaseTable.getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.toString());) {

      if (!this.preconditions.isEmpty()) {
        int index = 1;
        for (QueryPair pair : this.preconditions) {
          statement.setObject(index++, databaseTable.getModelMapper()
              .serializeItem(pair.queryPair().column(), pair.queryPair().value()));
        }
      }

      List<T> fetchQuery = new ArrayList<>();
      ResultSet set = statement.executeQuery();

      while (set.next()) {
        fetchQuery.add(databaseTable.getModelMapper().mapResultSet(set));
      }

      return fetchQuery;
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
  }

  public boolean isEmpty() {
    return preconditions.isEmpty();
  }

  public <T> Optional<T> executeFetchQuerySingle(DatabaseTable<T> databaseTable)
      throws DatabaseTransactionError {
    List<T> response = executeFetchQuery(databaseTable);
    if (response.size() == 1) {
      return Optional.of(response.iterator().next());
    } else {
      return Optional.empty();
    }
  }

  private enum QueryType {
    AND,
    OR
  }

  private record QueryPair(DatabaseQueryPair queryPair, QueryType type) {

  }
}
