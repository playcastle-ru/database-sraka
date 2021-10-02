package pl.memexurer.srakadb.sql.table.query;

public class DatabaseFetchQuery implements DatabaseQuery {

  private DatabaseQueryColumn[] columns;
  private DatabaseQueryPair[] preconditions;

  public DatabaseFetchQuery columnsAll() {
    this.columns = null; //madre
    return this;
  }

  public DatabaseFetchQuery columns(DatabaseQueryColumn... columns) {
    this.columns = columns;
    return this;
  }

  public DatabaseFetchQuery precondition(DatabaseQueryPair... preconditions) {
    this.preconditions = preconditions;
    return this;
  }

  public DatabaseQueryColumn[] getColumns() {
    return columns;
  }

  public DatabaseQueryPair[] getPreconditions() {
    return preconditions;
  }
}
