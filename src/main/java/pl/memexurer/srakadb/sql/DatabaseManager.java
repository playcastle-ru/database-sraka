package pl.memexurer.srakadb.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    public void createTable(DatabaseTable<?> table) throws DatabaseTransactionError {
        try {
            table.initializeTable(connection);
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }
}
