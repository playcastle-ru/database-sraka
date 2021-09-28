package pl.memexurer.srakadb.sql.mapper.serializer;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import pl.memexurer.srakadb.sql.mapper.TableRowInfo;
import pl.memexurer.srakadb.sql.mapper.TypedTableRow;

public interface TableRowValueDeserializer<T> {

  @SuppressWarnings("unchecked")
  static TableRowValueDeserializer<?> getDeserializer(Field field) {
    TableRowInfo rowInfo = field.getAnnotation(TableRowInfo.class);

    if (rowInfo.serialized().value() != PlaceholderRowValueDeserializer.class) {
      if (rowInfo.serialized().value() == BasicRowValueDeserializer.class) {
        if (field.getType().isEnum()) {
          return new DefaultEnumValueDeserializer<>((Class<? extends Enum<?>>) field.getType());
        } else if(field.getType().isArray()) {
          return new ArrayValueDeserializer();
        } else {
          throw new IllegalArgumentException("Unsupported field type!");
        }
      } else {
        try {
          return rowInfo.serialized().value().getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
          throw new RuntimeException(e);
        }
      }
    } else if (rowInfo.typed().value().length() != 0) {
      TypedTableRow typedTableRow = field.getAnnotation(TypedTableRow.class);
      return new BasicRowValueDeserializer(
          typedTableRow.value()
      );
    } else {
      throw new IllegalArgumentException("Sus!");
    }
  }

  T deserialize(ResultSet set, String row) throws SQLException;

  Object serialize(T fieldValue);

  String getDataType();
}
