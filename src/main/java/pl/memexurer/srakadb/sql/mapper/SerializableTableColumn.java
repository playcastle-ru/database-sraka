package pl.memexurer.srakadb.sql.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import pl.memexurer.srakadb.sql.mapper.serializer.BasicColumnValueDeserializer;
import pl.memexurer.srakadb.sql.mapper.serializer.TableColumnValueDeserializer;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializableTableColumn {
  Class<? extends TableColumnValueDeserializer<?>> value() default BasicColumnValueDeserializer.class;
}
