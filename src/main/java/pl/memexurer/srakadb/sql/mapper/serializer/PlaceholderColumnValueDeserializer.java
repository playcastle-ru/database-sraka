package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceholderColumnValueDeserializer implements TableColumnValueDeserializer<Object> {

  private PlaceholderColumnValueDeserializer() {
  }

  @Override
  public Object deserialize(ResultSet set, String column) throws SQLException {
    return null;
  }

  @Override
  public String getDataType() {
    return null;
  }

  @Override
  public Object serialize(Object fieldValue) {
    return null;
  }
}
