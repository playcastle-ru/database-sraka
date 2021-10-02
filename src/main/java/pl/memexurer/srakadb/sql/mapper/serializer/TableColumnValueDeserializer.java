package pl.memexurer.srakadb.sql.mapper.serializer;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import pl.memexurer.srakadb.sql.mapper.TableColumnInfo;
import pl.memexurer.srakadb.sql.mapper.TypedTableColumn;

public interface TableColumnValueDeserializer<T> {

  @SuppressWarnings("unchecked")
  static TableColumnValueDeserializer<?> getDeserializer(Field field) {
    TableColumnInfo rowInfo = field.getAnnotation(TableColumnInfo.class);

    if (rowInfo.serialized().value() != PlaceholderColumnValueDeserializer.class) {
      if (rowInfo.serialized().value() == BasicColumnValueDeserializer.class) {
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
      TypedTableColumn typedTableColumn = field.getAnnotation(TypedTableColumn.class);
      return new BasicColumnValueDeserializer(
          typedTableColumn.value()
      );
    } else {
      throw new IllegalArgumentException("Sus!");
    }
  }

  T deserialize(ResultSet set, String column) throws SQLException;

  Object serialize(T fieldValue);

  String getDataType();
}
