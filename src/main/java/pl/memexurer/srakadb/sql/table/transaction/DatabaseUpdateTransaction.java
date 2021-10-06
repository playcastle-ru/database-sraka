package pl.memexurer.srakadb.sql.table.transaction;

import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUpdateTransaction<T extends Statement> implements
    DatabaseTransaction {
    protected final T statement;

    public DatabaseUpdateTransaction(T statement) {
        this.statement = statement;
    }

    @Override
    public void close() throws DatabaseTransactionError {
        try {
            statement.close();
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }

    public T getStatement() {
        return statement;
    }
}
