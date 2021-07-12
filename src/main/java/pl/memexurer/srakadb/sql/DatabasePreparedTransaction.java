package pl.memexurer.srakadb.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabasePreparedTransaction extends
    DatabaseUpdateTransaction<PreparedStatement> {
    private final DatabaseTable<?> table;

    public DatabasePreparedTransaction(PreparedStatement statement, DatabaseTable<?> parentTable) {
        super(statement);
        this.table = parentTable;
    }

    @Override
    public void close() throws DatabaseTransactionError {
        try {
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
        super.close();
    }

    public void set(int columnPosition, Object object) throws DatabaseTransactionError {
        try {
            statement.setObject(columnPosition, object);
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }

    public void set(String columnName, Object object) throws DatabaseTransactionError {
        try {
            statement.setObject(table.getColumnIndex(columnName) + 1, object);
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }
}
