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

  public <T> DatabaseQueryTransaction<T> executeFetchQuery(DatabaseTable<T> databaseTable)
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
    PreparedStatement statement;
    try {
      statement = databaseTable.prepareStatement(builder.toString());
      System.out.println(builder.toString());
      if (this.preconditions
          != null) { //dlaczego tu bylo startIndex=1? kurwa, wez mi ktos przypomnij
        //juz wiem! bo setObject przyjmuje tylko indeksy od 1
        //ale chyba cos poszlo nie tak z moim sposobem myslenia?!
        for (int i = 0; i < this.preconditions.length; i++) {
          statement.setObject(i + 1, databaseTable.getModelMapper()
              .serializeItem(this.preconditions[i].column(), this.preconditions[i].value()));
        }
      }

      statement.executeQuery();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }

    return databaseTable.queryTransaction(statement);
  }

}
