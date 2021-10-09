package pl.memexurer.srakadb.sql.table.transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.mapper.DataModelMapper;

public class DatabaseQueryTransaction<T> extends DatabaseUpdateTransaction<Statement> {
    private final DataModelMapper<T> mapper;
    public DatabaseQueryTransaction(Statement statement,
        DataModelMapper<T> mapper) {
        super(statement);
        this.mapper = mapper;
    }

    @SneakyThrows
    private ResultSet getResultSet() {
        if (this.statement.isClosed())
            throw new IllegalArgumentException("Statement is closed.");

        return this.statement.getResultSet();
    }

    public boolean isEmpty() throws DatabaseTransactionError{
        try {
            return !getResultSet().isBeforeFirst();
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }

    public T readResult() throws DatabaseTransactionError {
        ResultSet set = getResultSet();

        try {
            if(!set.next())
                return null;

            return mapper.mapResultSet(set);
        } catch (SQLException e) {
            throw new DatabaseTransactionError(e);
        }
    }
}
