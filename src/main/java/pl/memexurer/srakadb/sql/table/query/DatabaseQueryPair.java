package pl.memexurer.srakadb.sql.table.query;

import pl.memexurer.srakadb.sql.table.query.DatabaseFetchQuery.QueryPrecondition;

public record DatabaseQueryPair(DatabaseQueryColumn column, Object value) {
  public static DatabaseQueryPair of(DatabaseQueryColumn column, Object value) {
    return new DatabaseQueryPair(column, value);
  }
}
