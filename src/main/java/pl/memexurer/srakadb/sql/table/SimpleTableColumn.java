package pl.memexurer.srakadb.sql.table;

import lombok.Builder;
import lombok.Data;
import pl.memexurer.srakadb.sql.DatabaseDatatype;

@Builder
@Data
public class SimpleTableColumn implements DatabaseTableColumn {
  private final String columnName;
  private final DatabaseDatatype datatype;
  private final boolean primary;
  private final boolean nullable;
}
