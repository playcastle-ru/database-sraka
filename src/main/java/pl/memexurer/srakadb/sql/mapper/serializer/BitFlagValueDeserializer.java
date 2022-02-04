package pl.memexurer.srakadb.sql.mapper.serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.type.BitFlag;

public class BitFlagValueDeserializer implements TableColumnValueDeserializer<BitFlag> {

  private final Class<? extends BitFlag> bitFlagClass;

  public BitFlagValueDeserializer(Class<? extends BitFlag> bitFlagClass) {
    this.bitFlagClass = bitFlagClass;
  }

  @SneakyThrows
  private static <T extends BitFlag> T createBitFlagObject(Class<T> bitFlagClass) {
    Constructor<T> constructor;
    try {
      constructor = bitFlagClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("No default constructor is present at bit flag class!");
    }

    try {
      return constructor.newInstance();
    } catch (IllegalAccessException e) {
      constructor.setAccessible(true);
      return constructor.newInstance();
    }
  }

  @Override
  @SneakyThrows
  public Object serialize(BitFlag fieldValue) {
    int baseValue = 0;

    for (int index = 0; index < bitFlagClass.getDeclaredFields().length; index++) {
      Field currentField = bitFlagClass.getDeclaredFields()[index];

      try {
        baseValue |= currentField.getBoolean(fieldValue) ? (2 << index) : 0;
      } catch (IllegalAccessException e) {
        currentField.setAccessible(true);
        baseValue |= currentField.getBoolean(fieldValue) ? (2 << index) : 0;
      }
    }

    return baseValue;
  }

  @Override
  public BitFlag deserialize(ResultSet set, String column) throws SQLException {
    int value = set.getInt(column);
    return deserialize(value);
  }

  @SneakyThrows
  public BitFlag deserialize(int value) {
    BitFlag bitFlagObject = createBitFlagObject(bitFlagClass);

    for (int index = 0; index < bitFlagClass.getDeclaredFields().length; index++) {
      Field currentField = bitFlagClass.getDeclaredFields()[index];

      boolean val = (value & (2 << index)) == (2 << index); // 1 is skipped
      try {
        currentField.setBoolean(bitFlagObject, val);
      } catch (IllegalAccessException e) {
        currentField.setAccessible(true);
        currentField.setBoolean(bitFlagObject, val);
      }
    }
    return bitFlagObject;
  }

  @Override
  public String getDataType() {
    return "int(" + bitFlagClass.getFields().length + ")";
  }
}
