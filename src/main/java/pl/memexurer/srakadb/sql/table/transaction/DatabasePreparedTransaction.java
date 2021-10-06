package pl.memexurer.srakadb.sql.table.transaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import pl.memexurer.srakadb.sql.mapper.DataModelMapper;

public class DatabasePreparedTransaction<T> extends
    DatabaseUpdateTransaction<PreparedStatement> {

  private final DataModelMapper<T> mapper;

  public DatabasePreparedTransaction(PreparedStatement statement, DataModelMapper<T> mapper) {
    super(statement);
    this.mapper = mapper;
  }

  @Override
  public void close() throws DatabaseTransactionError {
    try {
      statement.executeUpdate();
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
    super.close();
  }

  public void set(int columnPosition, Object object) throws DatabaseTransactionError {
    try {
      statement.setObject(columnPosition, object);
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
  }

  public void set(String columnName, Object object) throws DatabaseTransactionError {
    try {
      statement.setObject(mapper.getColumnIndex(columnName) + 1, object);
    } catch (SQLException throwable) {
      throw new DatabaseTransactionError(throwable);
    }
  }
}
