package pl.memexurer.srakadb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableInformationProvider<T> {

  T deserialize(ResultSet set) throws SQLException;

  void generateTable(DatabaseTable.TableBuilder builder);

  void fillAllRows(T tValue, DatabasePreparedTransaction transaction);
}
