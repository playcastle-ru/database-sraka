package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UuidValueDeserializer implements TableRowValueDeserializer<UUID> {

  @Override
  public UUID deserialize(ResultSet set, String row) throws SQLException {
    return UUID.fromString(set.getString(row));
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
