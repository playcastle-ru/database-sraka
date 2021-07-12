package pl.memexurer.srakadb.tests;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.memexurer.srakadb.sql.DatabaseDatatype;
import pl.memexurer.srakadb.sql.DatabaseManager;
import pl.memexurer.srakadb.sql.DatabasePreparedTransaction;
import pl.memexurer.srakadb.sql.DatabaseQueryTransaction;
import pl.memexurer.srakadb.sql.DatabaseTable;

public class DupaTest {
    @Test
    public void test() throws Throwable {
        HikariDataSource dataSource;

        try (InputStream stream = getClass().getResourceAsStream("database.properties")) {
            Properties properties = new Properties();
            properties.load(stream);

            HikariConfig config = new HikariConfig(properties);
            dataSource = new HikariDataSource(config);
        }

        DatabaseManager manager = new DatabaseManager(dataSource.getConnection());
        DatabaseTable<UserDataModel> table = new DatabaseTable.Builder<>("dupers", set -> new UserDataModel(
                UUID.fromString(set.getString("uuid")),
                set.getString("name"),
                set.getBoolean("boost")))
                .addPrimaryColumn("uuid", DatabaseDatatype.character(36))
                .addColumn("name", DatabaseDatatype.character(16))
                .addNullableColumn("boost", DatabaseDatatype.bool())
                .build();
        manager.createTable(table);

        try (DatabasePreparedTransaction transaction = table.createUpdateAllColumnsTransaction()) {
            transaction.set("uuid", UUID.randomUUID().toString());
            transaction.set("name", "SRAKA");
            transaction.set("boost", false);
        }

        try (DatabaseQueryTransaction<UserDataModel> transaction = table.createQuery("uuid", "19e4ada9-2962-41a2-919e-8350e5e7cfd9")) {
            UserDataModel userDataModel;
            while ((userDataModel = transaction.readNextResult()) != null) {
                System.out.println("query response: " + userDataModel);
            }
        }

        try (DatabaseQueryTransaction<UserDataModel> transaction = table.createQueryAllRowsTransaction()) {
            UserDataModel userDataModel;
            while ((userDataModel = transaction.readNextResult()) != null) {
                System.out.println(userDataModel);
            }
        }
    }

    public record UserDataModel(UUID uuid, String name, boolean boost) { }
}
