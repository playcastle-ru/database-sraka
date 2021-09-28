package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceholderRowValueDeserializer implements TableRowValueDeserializer<Object> {

  private PlaceholderRowValueDeserializer() {
  }

  @Override
  public Object deserialize(ResultSet set, String row) throws SQLException {
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
