package pl.memexurer.srakadb.sql.mapper.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArrayValueDeserializer implements TableColumnValueDeserializer<Object> {

  private static void write(Object objects, DataOutputStream outputStream) throws IOException {
    outputStream.writeShort(Array.getLength(objects));

    Class<?> arrayType = objects.getClass();
    if (int[].class.equals(arrayType)) {
      outputStream.write(0x00);
      for (int _integer : (int[]) objects) {
        outputStream.writeInt(_integer);
      }
    } else if (long[].class.equals(arrayType)) {
      outputStream.write(0x01);
      for (long _long : (long[]) objects) {
        outputStream.writeLong(_long);
      }
    } else if (short[].class.equals(arrayType)) {
      outputStream.write(0x02);
      for (short _short : (short[]) objects) {
        outputStream.writeShort(_short);
      }
    } else if (double[].class.equals(arrayType)) {
      outputStream.write(0x03);
      for (double _double : (double[]) objects) {
        outputStream.writeDouble(_double);
      }
    } else if (float[].class.equals(arrayType)) {
      outputStream.write(0x04);
      for (float _float : (float[]) objects) {
        outputStream.writeFloat(_float);
      }
    } else if (String[].class.equals(arrayType)) {
      outputStream.write(0x05);
      for (String _string : (String[]) objects) {
        outputStream.writeUTF(_string);
      }
    } else if (byte[].class.equals(arrayType)) {
      outputStream.write(0x06);
      for (byte _byte : (byte[]) objects) {
        outputStream.writeByte(_byte);
      }
    } else if (boolean[].class.equals(arrayType)) {
      outputStream.write(0x07);
      for (boolean _boolean : (boolean[]) objects) {
        outputStream.writeBoolean(_boolean);
      }
    } else {
      throw new IllegalArgumentException("Unsupported type!");
    }
  }

  private static Object read(DataInputStream inputStream) throws IOException {
    short arrayLength = inputStream.readShort();

    switch (inputStream.readByte()) {
      case 0x00 -> {
        Object array = Array.newInstance(int[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readInt());
        }

        return array;
      }
      case 0x01 -> {
        Object array = Array.newInstance(long[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readLong());
        }

        return array;
      }
      case 0x02 -> {
        Object array = Array.newInstance(short[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readShort());
        }

        return array;
      }
      case 0x03 -> {
        Object array = Array.newInstance(double[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readDouble());
        }

        return array;
      }
      case 0x04 -> {
        Object array = Array.newInstance(float[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readFloat());
        }

        return array;
      }
      case 0x05 -> {
        Object array = Array.newInstance(String[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readUTF());
        }

        return array;
      }
      case 0x06 -> {
        Object array = Array.newInstance(byte[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readByte());
        }

        return array;
      }
      case 0x07 -> {
        Object array = Array.newInstance(boolean[].class, arrayLength);

        for (int i = 0; i < arrayLength; i++) {
          Array.set(array, i, inputStream.readBoolean());
        }

        return array;
      }
      default -> throw new IllegalArgumentException("Unsupported array type!");
    }
  }

  @Override
  public Object deserialize(ResultSet set, String column) throws SQLException {
    InputStream stream = set.getBinaryStream(column);
    if(set.wasNull())
      return null;

    try (DataInputStream inputStream = new DataInputStream(stream)) {
      return read(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object serialize(Object fieldValue) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream)) {
      write(fieldValue, outputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getDataType() {
    return "blob";
  }
}
