package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BasicColumnValueDeserializer implements TableColumnValueDeserializer<Object> {

  private final String dataType;

  public BasicColumnValueDeserializer(String dataType) {
    this.dataType = dataType;
  }

  @Override
  public Object deserialize(ResultSet set, String column) throws SQLException {
    return set.getObject(column);
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
