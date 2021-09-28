package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicRowValueDeserializer implements TableRowValueDeserializer<Object> {

  private final String dataType;

  public BasicRowValueDeserializer(String dataType) {
    this.dataType = dataType;
  }

  @Override
  public Object deserialize(ResultSet set, String row) throws SQLException {
    return set.getObject(row);
  }

  @Override
  public Object serialize(Object fieldValue) {
    return fieldValue;
  }

  @Override
  public String getDataType() {
    return dataType;
  }
}
