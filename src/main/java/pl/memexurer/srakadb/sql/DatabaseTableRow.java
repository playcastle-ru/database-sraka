package pl.memexurer.srakadb.sql;

record DatabaseTableRow(String rowName, DatabaseDatatype datatype,
                               boolean isPrimary, boolean nullable) {
}
