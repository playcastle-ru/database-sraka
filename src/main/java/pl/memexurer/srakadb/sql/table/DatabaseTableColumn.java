package pl.memexurer.srakadb.sql.table;

import pl.memexurer.srakadb.sql.DatabaseDatatype;

public interface DatabaseTableColumn {
  String getColumnName();

  DatabaseDatatype getDatatype();

  boolean isPrimary();

  boolean isNullable();
}
