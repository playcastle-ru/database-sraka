package pl.memexurer.srakadb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.SneakyThrows;

public class DatabaseQueryTransaction<T> extends DatabaseUpdateTransaction<Statement> {
    private final TableInformationProvider<T> deserializer;

    public DatabaseQueryTransaction(Statement statement, TableInformationProvider<T> deserializer) {
        super(statement);
        this.deserializer = deserializer;
    }

    @SneakyThrows
    private ResultSet getResultSet() {
        if (this.statement.isClosed())
            throw new IllegalArgumentException("Statement is closed.");

        return this.statement.getResultSet();
    }

    public T readNextResult() throws DatabaseTransactionError{
        ResultSet set = getResultSet();
        try {
            if(set.next())
                return deserializer.deserialize(set);
            else
                return null;
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }

    public boolean isEmpty() throws DatabaseTransactionError{
        try {
            return !getResultSet().isBeforeFirst();
        } catch (SQLException throwable) {
            throw new DatabaseTransactionError(throwable);
        }
    }
}
