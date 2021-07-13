package pl.memexurer.srakadb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetDeserializer<T> {
    T deserialize(ResultSet set) throws SQLException;
}
