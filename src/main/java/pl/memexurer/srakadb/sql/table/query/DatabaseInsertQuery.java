package pl.memexurer.srakadb.sql.table.query;

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

  public UpdateType getUpdateType() {
    return updateType;
  }

  public DatabaseQueryPair[] getPreconditions() {
    return preconditions;
  }

  public DatabaseQueryPair[] getValues() {
    return values;
  }

  public enum UpdateType {
    INSERT,
    REPLACE,
  }
}
