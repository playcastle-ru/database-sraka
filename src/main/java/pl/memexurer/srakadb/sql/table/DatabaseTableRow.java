package pl.memexurer.srakadb.sql.table;

import pl.memexurer.srakadb.sql.DatabaseDatatype;

record DatabaseTableRow(String rowName, DatabaseDatatype datatype,
                               boolean isPrimary, boolean nullable) {
}
