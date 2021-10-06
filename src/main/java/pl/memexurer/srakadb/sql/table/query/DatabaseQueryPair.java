package pl.memexurer.srakadb.sql.table.query;

import pl.memexurer.srakadb.sql.table.DatabaseTableColumn;

public record DatabaseQueryPair(DatabaseTableColumn column, Object value) {
  public static DatabaseQueryPair of(DatabaseTableColumn column, Object value) {
    return new DatabaseQueryPair(column, value);
  }
}
