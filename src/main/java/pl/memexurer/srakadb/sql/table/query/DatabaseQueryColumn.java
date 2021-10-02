package pl.memexurer.srakadb.sql.table.query;

import pl.memexurer.srakadb.sql.mapper.ColumnFieldPair;
import pl.memexurer.srakadb.sql.util.ObjectProperty.FieldObjectProperty;

public interface DatabaseQueryColumn {

  static DatabaseQueryColumn from(String name) {
    return () -> name;
  }

  static DatabaseQueryColumn from(FieldObjectProperty property) {
    ColumnFieldPair columnFieldPair = ColumnFieldPair.get(property.field());
    if (columnFieldPair == null) {
      throw new IllegalArgumentException("Invalid field specified!");
    }

    return columnFieldPair::name;
  }

  String getName();
}
