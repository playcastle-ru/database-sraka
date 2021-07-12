package pl.memexurer.srakadb.sql;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface DatabaseDatatype {
    String sqlString();

    final record Character(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "CHAR(" + maxSize + ")";
        }
    }

    static DatabaseDatatype character(int maxSize) {
        return new Character(maxSize);
    }

    final record VarCharacter(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "VARCHAR(" + maxSize + ")";
        }
    }

    static DatabaseDatatype varCharacter(int maxSize) {
        return new VarCharacter(maxSize);
    }

    final record Binary(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "BINARY(" + maxSize + ")";
        }
    }

    static DatabaseDatatype binary(int maxSize) {
        return new Binary(maxSize);
    }

    final record VarBinary(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "VARBINARY(" + maxSize + ")";
        }
    }

    static DatabaseDatatype varBinary(int maxSize) {
        return new VarBinary(maxSize);
    }

    final record BinaryBlob(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "BLOB(" + maxSize + ")";
        }
    }

    static DatabaseDatatype binaryBlob(int maxSize) {
        return new BinaryBlob(maxSize);
    }

    //glupie w chuj
    final record Enum(String[] elements) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "ENUM(" + Arrays.stream(elements).map(str -> '\'' + str + '\'').collect(Collectors.joining(",")) + ")";
        }
    }

    static DatabaseDatatype enumType(String[] elements) {
        return new Enum(elements);
    }

    //to tez glupie w chuj
    final record FlagSet(String[] elements) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "SET(" + Arrays.stream(elements).map(str -> '\'' + str + '\'').collect(Collectors.joining(",")) + ")";
        }
    }

    static DatabaseDatatype flagSet(String[] elements) {
        return new FlagSet(elements);
    }

    final record Bit(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "BIT(" + maxSize + ")";
        }
    }

    static DatabaseDatatype bit(int maxSize) {
        return new Bit(maxSize);
    }

    final record TinyInt(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "TINYINT(" + maxSize + ")";
        }
    }

    static DatabaseDatatype tinyInt(int maxSize) {
        return new TinyInt(maxSize);
    }

    final record SmallInt(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "SMALLINT(" + maxSize + ")";
        }
    }

    static DatabaseDatatype smallInt(int maxSize) {
        return new SmallInt(maxSize);
    }

    final record Int(int maxSize) implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "INT(" + maxSize + ")";
        }
    }

    static DatabaseDatatype integer(int maxSize) {
        return new Int(maxSize);
    }

    final record Boolean() implements DatabaseDatatype {
        @Override
        public String sqlString() {
            return "BOOLEAN";
        }
    }

    static DatabaseDatatype bool() {
        return new Boolean();
    }
}
