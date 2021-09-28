package pl.memexurer.srakadb.sql.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.DatabasePreparedTransaction;
import pl.memexurer.srakadb.sql.DatabaseTable.TableBuilder;
import pl.memexurer.srakadb.sql.TableInformationProvider;
import pl.memexurer.srakadb.sql.mapper.serializer.TableRowValueDeserializer;

public class MappedDeserializer<T> implements TableInformationProvider<T> {

  private final Map<Field, RowFieldPair> valueDeserializerMap = new HashMap<>();
  private final Class<T> tClass;

  public MappedDeserializer(Class<T> tClass) {
    this.tClass = tClass;
    for (Field field : tClass.getDeclaredFields()) {
      RowFieldPair fieldPair = RowFieldPair.get(field);
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

    for (Map.Entry<Field, RowFieldPair> rows : valueDeserializerMap.entrySet()) {
      Object deserialized = rows.getValue().deserializer().deserialize(set, rows.getValue().name());
      try {
        rows.getKey().set(instance, deserialized);
      } catch (IllegalAccessException e) {
        rows.getKey().setAccessible(true);
        rows.getKey().set(instance, deserialized);
      }
    }
    return null;
  }

  @Override
  public void generateTable(TableBuilder builder) {
    for (RowFieldPair rows : valueDeserializerMap.values()) {
      builder.addColumn(rows.name(), rows.deserializer().getDataType(), rows.primary(),
          rows.nullable());
    }
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void fillAllRows(T tValue, DatabasePreparedTransaction transaction) {
    for (Map.Entry<Field, RowFieldPair> rows : valueDeserializerMap.entrySet()) {
      Object value;
      try {
        value = rows.getKey().get(tValue);
      } catch (IllegalAccessException e) {
        rows.getKey().setAccessible(true);
        value = rows.getKey().get(tValue);
      }

      transaction.set(rows.getValue().name(),
          ((TableRowValueDeserializer<Object>) rows.getValue().deserializer()).serialize(
              value));
    }
  }

  private record RowFieldPair(TableRowValueDeserializer<?> deserializer, String name,
                              boolean primary, boolean nullable) {

    static RowFieldPair get(Field field) {
      TableRowInfo rowInfo = field.getAnnotation(TableRowInfo.class);
      if (rowInfo == null) {
        return null;
      }

      TableRowValueDeserializer<?> deserializer = TableRowValueDeserializer.getDeserializer(field);

      return new RowFieldPair(deserializer,
          rowInfo.name().length() == 0 ? field.getName() : rowInfo.name(),
          rowInfo.primary(), rowInfo.nullable());
    }
  }
}
