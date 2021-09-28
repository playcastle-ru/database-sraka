package pl.memexurer.srakadb.sql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.memexurer.srakadb.sql.mapper.serializer.BasicRowValueDeserializer;
import pl.memexurer.srakadb.sql.mapper.serializer.TableRowValueDeserializer;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializableTableRow {
  Class<? extends TableRowValueDeserializer<?>> value() default BasicRowValueDeserializer.class;
}
