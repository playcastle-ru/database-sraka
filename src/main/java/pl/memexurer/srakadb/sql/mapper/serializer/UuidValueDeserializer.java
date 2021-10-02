package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UuidValueDeserializer implements TableColumnValueDeserializer<UUID> {

  @Override
  public UUID deserialize(ResultSet set, String column) throws SQLException {
    return UUID.fromString(set.getString(column));
  }

  @Override
  public Object serialize(UUID fieldValue) {
    return fieldValue.toString();
  }

  @Override
  public String getDataType() {
    return "varchar(36)";
  }
}
