package pl.memexurer.srakadb.sql;

public interface DatabaseTransaction extends AutoCloseable {
    @Override
    void close() throws DatabaseTransactionError;
}
