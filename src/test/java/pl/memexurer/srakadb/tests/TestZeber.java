package pl.memexurer.srakadb.tests;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.memexurer.srakadb.sql.mapper.DataModelMapper;
import pl.memexurer.srakadb.sql.mapper.SerializableTableColumn;
import pl.memexurer.srakadb.sql.mapper.TableColumnInfo;
import pl.memexurer.srakadb.sql.mapper.serializer.UuidValueDeserializer;
import pl.memexurer.srakadb.sql.table.DatabaseTable;
import pl.memexurer.srakadb.sql.table.query.DatabaseFetchQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery;
import pl.memexurer.srakadb.sql.table.query.DatabaseInsertQuery.UpdateType;

public class TestZeber {

  @Test
  public void modelTest() {
    DatabaseTable<TestDataModel> databaseTable = new DatabaseTable<>("memerurki",
        TestDataModel.class);

    DataModelMapper<TestDataModel> dataModelMapper = new DataModelMapper<>(TestDataModel.class);
    TestDataModel dataModel = new TestDataModel("Memerurka", UUID.randomUUID());

    databaseTable.executeInsertQuery(new DatabaseInsertQuery(UpdateType.REPLACE)
            .values(dataModelMapper.createQueryPairs(dataModel)))
        .close();

    System.out.println(databaseTable.executeFetchQuery(new DatabaseFetchQuery()
            .precondition(dataModelMapper.createQueryPair("uuid", dataModel.uuid)))
        .readResult());
  }


  public record TestDataModel(@TableColumnInfo String name,
                              @TableColumnInfo(serialized = @SerializableTableColumn(UuidValueDeserializer.class)) UUID uuid) {

    @Override
    public String toString() {
      return "TestDataModel{" +
          "name='" + name + '\'' +
          ", uuid=" + uuid +
          '}';
    }
  }
}
