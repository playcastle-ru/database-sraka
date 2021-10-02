package pl.memexurer.srakadb.sql.mapper;

import java.lang.reflect.Field;
import pl.memexurer.srakadb.sql.mapper.serializer.BasicColumnValueDeserializer;
import pl.memexurer.srakadb.sql.mapper.serializer.TableColumnValueDeserializer;

public record ColumnFieldPair(TableColumnValueDeserializer<?> deserializer, String name,
                              boolean primary, boolean nullable) {

  public static ColumnFieldPair get(Field field) {
    TableColumnInfo rowInfo = field.getAnnotation(TableColumnInfo.class);
    if (rowInfo == null) {
      return null;
    }

    TableColumnValueDeserializer<?> deserializer = TableColumnValueDeserializer.getDeserializer(
        field);

    return new ColumnFieldPair(deserializer,
        rowInfo.name().length() == 0 ? field.getName() : rowInfo.name(),
        rowInfo.primary(), rowInfo.nullable());
  }

  public static ColumnFieldPair get(String name, String datatype, boolean primary, boolean nullable) {
    return new ColumnFieldPair(new BasicColumnValueDeserializer(datatype),
        name, primary, nullable);
  }
}