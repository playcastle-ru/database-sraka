package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultEnumValueDeserializer<T extends Enum<?>> implements
    TableColumnValueDeserializer<T> {

  private final Class<T> tClass;

  public DefaultEnumValueDeserializer(Class<T> tClass) {
    this.tClass = tClass;
  }

  @Override
  public T deserialize(ResultSet set, String column) throws SQLException {
    String columnValue = set.getString(column);
    if(columnValue == null)
      return null;

    for (T t : tClass.getEnumConstants()) {
      if (t.name().equalsIgnoreCase(columnValue)) {
        return t;
      }
    }

    throw new IllegalArgumentException(column);
  }

  @Override
  public Object serialize(T fieldValue) {
    return fieldValue.name();
  }

  @Override
  public String getDataType() {
    return "ENUM(" + Arrays.stream(tClass.getEnumConstants())
        .map(Enum::name)
        .map(str -> "'" + str + "'")
        .collect(Collectors.joining(",")) + ")";
  }
}
