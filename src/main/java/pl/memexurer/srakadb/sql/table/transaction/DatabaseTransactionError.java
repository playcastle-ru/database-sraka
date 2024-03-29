package pl.memexurer.srakadb.sql.table.transaction;

public class DatabaseTransactionError extends RuntimeException {
    public DatabaseTransactionError(String message) {
        super(message);
    }

    public DatabaseTransactionError(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseTransactionError(Throwable cause) {
        super(cause);
    }
}
