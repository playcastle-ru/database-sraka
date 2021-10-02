package pl.memexurer.srakadb.sql.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface TableInformationProvider<T> {

  T deserialize(ResultSet set) throws SQLException;

  void generateTable(Map<String, DatabaseTableColumn> map);

  void fillAllRows(T tValue, DatabasePreparedTransaction transaction);
}
