package pl.memexurer.srakadb.sql.mapper;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.table.DatabasePreparedTransaction;
import pl.memexurer.srakadb.sql.table.DatabaseTableColumn;
import pl.memexurer.srakadb.sql.table.TableInformationProvider;
import pl.memexurer.srakadb.sql.mapper.serializer.TableColumnValueDeserializer;
import pl.memexurer.srakadb.sql.util.ObjectProperty;
import pl.memexurer.srakadb.sql.util.ObjectProperty.FieldObjectProperty;

public class MappedDeserializer<T> implements TableInformationProvider<T> {

  private final Map<FieldObjectProperty, ColumnFieldPair> valueDeserializerMap = new HashMap<>();
  private final Class<T> tClass;

  public MappedDeserializer(Class<T> tClass) {
    this.tClass = tClass;
    for (FieldObjectProperty field : ObjectProperty.properties(tClass)) {
      ColumnFieldPair fieldPair = ColumnFieldPair.get(field.field());
      if (fieldPair == null) {
        continue;
      }
      valueDeserializerMap.put(field, fieldPair);
    }
  }

  @SneakyThrows
  private static <T> T createInstance(Class<T> tClass) {
    Constructor<T> tConstructor = null;
    try {
      tConstructor = tClass.getConstructor();
      return tConstructor.newInstance();
    } catch (IllegalAccessException e) {
      tConstructor.setAccessible(true);
      return tConstructor.newInstance();
    }
  }

  @Override
  @SneakyThrows
  public T deserialize(ResultSet set) throws SQLException {
    T instance = createInstance(tClass);

    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> rows : valueDeserializerMap.entrySet()) {
      Object deserialized = rows.getValue().deserializer().deserialize(set, rows.getValue().name());
      rows.getKey().setValue(instance, deserialized);
    }
    return null;
  }

  @Override
  public void generateTable(Map<String, DatabaseTableColumn> builder) {
    for (ColumnFieldPair rows : valueDeserializerMap.values()) {
      builder.put(rows.name(), rows);
    }
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void fillAllRows(T tValue, DatabasePreparedTransaction transaction) {
    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> rows : valueDeserializerMap.entrySet()) {
      Object value = rows.getKey().getValue(tValue);

      transaction.set(rows.getValue().name(),
          ((TableColumnValueDeserializer<Object>) rows.getValue().deserializer()).serialize(
              value));
    }
  }
}
