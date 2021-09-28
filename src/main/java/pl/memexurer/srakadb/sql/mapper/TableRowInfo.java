package pl.memexurer.srakadb.sql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.memexurer.srakadb.sql.mapper.serializer.PlaceholderRowValueDeserializer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRowInfo {
  TypedTableRow typed() default @TypedTableRow(value = "");

  SerializableTableRow serialized() default @SerializableTableRow(value = PlaceholderRowValueDeserializer.class);

  String name() default "";

  boolean primary() default false;

  boolean nullable() default true;
}
