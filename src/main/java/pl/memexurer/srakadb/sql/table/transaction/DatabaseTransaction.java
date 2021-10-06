package pl.memexurer.srakadb.sql.table.transaction;

public interface DatabaseTransaction extends AutoCloseable {
    @Override
    void close() throws DatabaseTransactionError;
}
