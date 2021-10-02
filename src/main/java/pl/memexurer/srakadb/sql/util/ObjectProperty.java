package pl.memexurer.srakadb.sql.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;

public interface ObjectProperty<T> {
  String getName();

  T getValue(Object instance);

  void setValue(Object instance, T value);

  @SneakyThrows
  static FieldObjectProperty property(Class<?> clazz, String fieldName) {
    Field field;

    try {
      field = clazz.getField(fieldName);
    } catch (NoSuchFieldException ex1) {
      try {
        field = clazz.getDeclaredField(fieldName);
      } catch (NoSuchElementException ex2) {
        throw new IllegalArgumentException("Unknown field!");
      }
    }

    return new FieldObjectProperty(field);
  }

  static FieldObjectProperty[] properties(Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
        .map(FieldObjectProperty::new)
        .toArray(FieldObjectProperty[]::new);
  }
  static FieldObjectProperty[] properties(Class<?> clazz, String... fieldNames) {
    return Arrays.stream(fieldNames)
        .map(str -> property(clazz, str))
        .toArray(FieldObjectProperty[]::new);
  }

  record FieldObjectProperty(Field field) implements ObjectProperty<Object> {

    @Override
    public String getName() {
      return field.getName();
    }

    @Override
    @SneakyThrows
    public Object getValue(Object instance) {
      try {
        return field.get(instance);
      } catch (IllegalAccessException ex) {
        field.setAccessible(true);
        return field.get(instance);
      }
    }

    @Override
    @SneakyThrows
    public void setValue(Object instance, Object value) {
      try {
        field.set(instance, value);
      } catch (IllegalAccessException ex) {
        field.setAccessible(true);
        field.set(instance, value);
      }
    }
  }
}
