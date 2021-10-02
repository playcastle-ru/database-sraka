package pl.memexurer.srakadb.sql.table;

import pl.memexurer.srakadb.sql.DatabaseDatatype;

record DatabaseTableColumn(String columnName, DatabaseDatatype datatype,
                           boolean isPrimary, boolean nullable) {
}
