package pl.memexurer.srakadb.sql.mapper.serializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.SneakyThrows;
import pl.memexurer.srakadb.sql.type.BitFlag;

public class BitFlagValueDeserializer implements TableRowValueDeserializer<BitFlag> {

  private final Class<? extends BitFlag> bitFlagClass;

  public BitFlagValueDeserializer(Class<? extends BitFlag> bitFlagClass) {
    this.bitFlagClass = bitFlagClass;
  }

  @SneakyThrows
  private static <T extends BitFlag> T createBitFlagObject(Class<T> bitFlagClass) {
    Constructor<T> constructor;
    try {
      constructor = bitFlagClass.getConstructor();
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

    for (Field field : bitFlagClass.getFields()) {
      try {
        baseValue |= field.getBoolean(fieldValue) ? 1 : 0;
      } catch (IllegalAccessException e) {
        field.setAccessible(true);
        baseValue |= field.getBoolean(fieldValue) ? 1 : 0;
      }
    }

    return baseValue;
  }

  @Override
  @SneakyThrows
  public BitFlag deserialize(ResultSet set, String row) throws SQLException {
    BitFlag bitFlagObject = createBitFlagObject(bitFlagClass);

    int value = set.getInt(row);
    for (int index = 0; index < value; index++) {
      Field currentField = bitFlagClass.getFields()[index];

      boolean val = (value & (2 << index)) == index; // 1 is skipped
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
