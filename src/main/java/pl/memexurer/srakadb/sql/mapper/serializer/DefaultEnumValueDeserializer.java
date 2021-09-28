package pl.memexurer.srakadb.sql.mapper.serializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultEnumValueDeserializer<T extends Enum<?>> implements TableRowValueDeserializer<T> {

  private final Class<T> tClass;

  public DefaultEnumValueDeserializer(Class<T> tClass) {
    this.tClass = tClass;
  }

  @Override
  public T deserialize(ResultSet set, String row) throws SQLException {
    String rowValue = set.getString(row);
    for (T t : tClass.getEnumConstants()) {
      if (t.name().equalsIgnoreCase(rowValue)) {
        return t;
      }
    }

    throw new IllegalArgumentException(rowValue);
  }

  @Override
  public Object serialize(T fieldValue) {
    return fieldValue.name();
  }

  @Override
  public String getDataType() {
    return "ENUM(" + Arrays.stream(tClass.getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.joining(",")) + ");";
  }
}
