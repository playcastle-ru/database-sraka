package pl.memexurer.srakadb.sql.table.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.transaction.DatabaseTransactionError;

public class DatabaseFetchQuery implements DatabaseQuery {

  private DatabaseQueryPair[] preconditions;

  public DatabaseFetchQuery precondition(DatabaseQueryPair... preconditions) {
    this.preconditions = preconditions;
    return this;
  }

  public <T> List<T> executeFetchQuery(DatabaseTable<T> databaseTable)
      throws DatabaseTransactionError {
    StringBuilder builder = new StringBuilder("SELECT *");

    builder.append(" FROM ").append(databaseTable.getTableName()).append(' ');

    if (this.preconditions != null) {
      builder.append("WHERE ").append(
          Arrays.stream(this.preconditions)
              .map(pair -> pair.column().getColumnName() + "=?")
              .collect(Collectors.joining(" AND "))
      );
    }

    try (Connection connection = databaseTable.getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.toString());) {

      if (this.preconditions
          != null) { //dlaczego tu bylo startIndex=1? kurwa, wez mi ktos przypomnij
        //juz wiem! bo setObject przyjmuje tylko indeksy od 1
        //ale chyba cos poszlo nie tak z moim sposobem myslenia?!
        for (int i = 0; i < this.preconditions.length; i++) {
          statement.setObject(i + 1, databaseTable.getModelMapper()
              .serializeItem(this.preconditions[i].column(), this.preconditions[i].value()));
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

  public <T> Optional<T> executeFetchQuerySingle(DatabaseTable<T> databaseTable)
      throws DatabaseTransactionError {
    List<T> response = executeFetchQuery(databaseTable);
    if (response.size() == 1) {
      return Optional.of(response.iterator().next());
    } else {
      return Optional.empty();
    }
  }
}
