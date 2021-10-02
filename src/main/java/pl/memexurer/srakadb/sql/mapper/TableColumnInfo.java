package pl.memexurer.srakadb.sql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.memexurer.srakadb.sql.mapper.serializer.PlaceholderColumnValueDeserializer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnInfo {
  TypedTableColumn typed() default @TypedTableColumn(value = "");

  SerializableTableColumn serialized() default @SerializableTableColumn(value = PlaceholderColumnValueDeserializer.class);

  String name() default "";

  boolean primary() default false;

  boolean nullable() default true;
}
