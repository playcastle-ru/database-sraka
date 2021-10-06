package pl.memexurer.srakadb.sql.mapper;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.table.DatabaseTableColumn;
import pl.memexurer.srakadb.sql.table.query.DatabaseQueryPair;
import pl.memexurer.srakadb.sql.util.ObjectProperty;
import pl.memexurer.srakadb.sql.util.ObjectProperty.FieldObjectProperty;

public class DataModelMapper<T> {

  private final Map<FieldObjectProperty, ColumnFieldPair> properties;
  private final Class<T> tClass;

  public DataModelMapper(Class<T> clazz) {
    this.properties = createPropertyMap(clazz);
    this.tClass = clazz;
  }

  private static Map<FieldObjectProperty, ColumnFieldPair> createPropertyMap(Class<?> clazz) {
    Map<FieldObjectProperty, ColumnFieldPair> propertyMap = new HashMap<>();

    for (FieldObjectProperty field : ObjectProperty.properties(clazz)) {
      ColumnFieldPair fieldPair = ColumnFieldPair.get(field.field());
      if (fieldPair == null) {
        continue;
      }

      propertyMap.put(field, fieldPair);
    }

    return propertyMap;
  }

  @SneakyThrows
  private static <T> T createObject(Class<T> tClass) {
    Constructor<T> constructor;
    try {
      constructor = tClass.getConstructor();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "No default constructor is present at deserialized class!");
    }

    try {
      return constructor.newInstance();
    } catch (IllegalAccessException e) {
      constructor.setAccessible(true);
      return constructor.newInstance();
    }
  }

  public DatabaseQueryPair[] createQueryPairs(T instance) {
    DatabaseQueryPair[] queryPairs = new DatabaseQueryPair[properties.size()];

    int index = 0;

    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> entry : properties.entrySet()) {
      queryPairs[index++] = new DatabaseQueryPair(entry.getValue(),
          entry.getKey().getValue(instance));
    }

    return queryPairs;
  }

  public DatabaseQueryPair createQueryPair(String fieldName, Object value) {
    return new DatabaseQueryPair(
        findByName(fieldName),
        value
    );
  }

  private ColumnFieldPair findByName(String name) {
    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> entry : properties.entrySet()) {
      if (entry.getKey().getName().equals(name)) {
        return entry.getValue();
      }
    }
    return null;
  }

  public Collection<? extends DatabaseTableColumn> getColumns() {
    return properties.values();
  }

  public T mapResultSet(ResultSet set) throws SQLException {
    T object = createObject(tClass);
    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> entry : properties.entrySet()) {
      entry.getKey().setValue(object,
          entry.getValue().deserializer().deserialize(set, entry.getValue().name()));
    }
    return object;
  }

  public int getColumnIndex(String column) {
    int index = 0;

    for (Map.Entry<FieldObjectProperty, ColumnFieldPair> entry : properties.entrySet()) {
      index++;

      if (entry.getValue().getColumnName().equals(column)) {
        return index;
      }
    }

    throw new IllegalArgumentException("No such column found!");
  }
}
