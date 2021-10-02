package pl.memexurer.srakadb.tests;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.memexurer.srakadb.sql.mapper.SerializableTableColumn;
import pl.memexurer.srakadb.sql.mapper.TableColumnInfo;
import pl.memexurer.srakadb.sql.mapper.serializer.UuidValueDeserializer;
import pl.memexurer.srakadb.sql.table.query.DatabaseFetchQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseQueryColumn;
import pl.memexurer.srakadb.sql.table.query.DatabaseQueryPair;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery.UpdateType;

public class TestZeber {

  @Test
  public void test() {
    UUID uuid = UUID.randomUUID();
    String name = "Memerurka";

    DatabaseInsertQuery update = new DatabaseInsertQuery(UpdateType.REPLACE)
        .values(DatabaseQueryPair.of(
            DatabaseQueryColumn.from("uuid"),
            uuid
        ), DatabaseQueryPair.of(
            DatabaseQueryColumn.from("name"),
            name
        ));

    DatabaseFetchQuery fetchQuery = new DatabaseFetchQuery()
        .columnsAll()
        .precondition(DatabaseQueryPair.of(
            DatabaseQueryColumn.from("uuid"),
            uuid
        ), DatabaseQueryPair.of(
            DatabaseQueryColumn.from("name"),
            name
        ));
  }


  public static class TestDataModel {

    @TableColumnInfo
    private String name;
    @TableColumnInfo(serialized = @SerializableTableColumn(UuidValueDeserializer.class))
    private UUID uuid;
  }
}
