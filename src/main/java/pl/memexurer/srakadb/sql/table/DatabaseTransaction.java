package pl.memexurer.srakadb.sql.table;

public interface DatabaseTransaction extends AutoCloseable {
    @Override
    void close() throws DatabaseTransactionError;
}
