package org.example;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.FileContentReader.*;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String NAMESPACE_DOC = "doc";
    public static final String NAMESPACE_GRAPH = "graph";

    public static void main(String[] args) throws IOException {
        logger.info("Polypheny-TestData started.");
        String currentPath = System.getProperty("user.home");
        File file = new File(currentPath, "RO");
        if (file.exists()) {
            boolean fileDeleted = file.delete();
            assert fileDeleted;
            logger.info("File {} deleted.", file.getAbsolutePath());
        }

        createNamespaces();
        insertTestData();

        boolean fileCreated = file.createNewFile();
        assert fileCreated;
        logger.info("File {} created.", file.getAbsolutePath());
        logger.info("Done!");
    }

    private static void createNamespaces() {
        logger.info("Creating namespaces");

        executeUpdateSql(
                "DROP NAMESPACE IF EXISTS " + NAMESPACE_DOC
        );
        executeUpdateSql(
                "CREATE DOCUMENT NAMESPACE IF NOT EXISTS " + NAMESPACE_DOC
        );

        // Graph
        // TODO: Does not work...
//        executeUpdate(
//                "DROP NAMESPACE IF EXISTS \"graph\""
//        );
//        executeUpdate(
//                "CREATE DATABASE graph ON STORE hsqldb"
//        );
    }


    private static void insertTestData() throws IOException {
        logger.info("Inserting test data");

        String individualCoords = readFileFromResources("mql/individualCoords.mql");
        executeMql(individualCoords);

//        List<String> indis = readFilesFromResources("mql/indi/*.mql");
//        indis.forEach(Main::executeMql);

//        List<String> indis = readFilesFromResourcesByList(List.of(
//                "mql/indi/indi_1.mql",
//                "mql/indi/indi_2.mql",
//                "mql/indi/indi_3.mql",
//                "mql/indi/indi_4.mql",
//                "mql/indi/indi_5.mql",
//                "mql/indi/indi_6.mql",
//                "mql/indi/indi_7.mql",
//                "mql/indi/indi_8.mql",
//                "mql/indi/indi_9.mql",
//                "mql/indi/indi_10.mql",
//                "mql/indi/indi_11.mql",
//                "mql/indi/indi_12.mql",
//                "mql/indi/indi_13.mql",
//                "mql/indi/indi_14.mql",
//                "mql/indi/indi_15.mql",
//                "mql/indi/indi_16.mql",
//                "mql/indi/indi_17.mql",
//                "mql/indi/indi_18.mql",
//                "mql/indi/indi_19.mql",
//                "mql/indi/indi_20.mql",
//                "mql/indi/indi_21.mql",
//                "mql/indi/indi_22.mql",
//                "mql/indi/indi_23.mql",
//                "mql/indi/indi_24.mql",
//                "mql/indi/indi_25.mql",
//                "mql/indi/indi_26.mql",
//                "mql/indi/indi_27.mql",
//                "mql/indi/indi_28.mql",
//                "mql/indi/indi_29.mql",
//                "mql/indi/indi_30.mql",
//                "mql/indi/indi_31.mql",
//                "mql/indi/indi_32.mql",
//                "mql/indi/indi_33.mql",
//                "mql/indi/indi_34.mql",
//                "mql/indi/indi_35.mql",
//                "mql/indi/indi_36.mql",
//                "mql/indi/indi_37.mql",
//                "mql/indi/indi_38.mql",
//                "mql/indi/indi_39.mql",
//                "mql/indi/indi_40.mql",
//                "mql/indi/indi_41.mql",
//                "mql/indi/indi_42.mql",
//                "mql/indi/indi_43.mql",
//                "mql/indi/indi_44.mql",
//                "mql/indi/indi_45.mql",
//                "mql/indi/indi_46.mql",
//                "mql/indi/indi_47.mql",
//                "mql/indi/indi_48.mql",
//                "mql/indi/indi_49.mql",
//                "mql/indi/indi_50.mql"
//        ));
//        indis.forEach(Main::executeMql);

        executeUpdateSql("""
                DROP TABLE IF EXISTS BernTempSensors
                """);
        executeUpdateSql("""
                CREATE TABLE BernTempSensors (
                    stationId INT NOT NULL,
                    temp DOUBLE NOT NULL,
                    relativeHumidity DOUBLE NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    Location GEOMETRY NOT NULL,
                    PRIMARY KEY (stationId));
                """);
        String bern = readFileFromResources("bern.sql");
        executeUpdateSql(bern);


        executeUpdateSql("""
                DROP TABLE IF EXISTS arrondissement
                """);
        executeUpdateSql("""
                CREATE TABLE arrondissement (
                    num INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    Location GEOMETRY NOT NULL,
                    PRIMARY KEY (num));
                """);
        String arron = readFileFromResources("arron.sql");
        executeUpdateSql(arron);

        executeUpdateSql("""
                DROP TABLE IF EXISTS restaurants
                """);
        executeUpdateSql("""
                CREATE TABLE restaurants (
                   restaurant_link VARCHAR(255) NOT NULL,
                   name VARCHAR(255) NOT NULL,
                   address VARCHAR(255) NOT NULL,
                   Location GEOMETRY NOT NULL,
                   top_tags VARCHAR(255) NOT NULL,
                   price_range VARCHAR(255) NOT NULL,
                   cuisines VARCHAR(255) NOT NULL,
                   special_diets VARCHAR(255) NOT NULL,
                   excellent INT NOT NULL,
                   very_good INT NOT NULL,
                   average INT NOT NULL,
                   poor INT NOT NULL,
                   terrible INT NOT NULL,
                   price_level INT NOT NULL,
                   total_ratings INT NOT NULL,
                   PRIMARY KEY (restaurant_link));
                """);
        String restaurants = readFileFromResources("restaurants.sql");
        executeUpdateSql(restaurants);
    }

    public static void executeMql(String mql) {
        logger.info("executeMql: {}", mql.length() > 50 ? mql.substring(0, 50) : mql);
        HttpResponse<String> result = MongoConnection.executeGetResponse(mql, NAMESPACE_DOC);
        assert result.getStatus() == 200;
    }

    public static void executeCypher(String cypher) {
        logger.info("executeCypher: {}", cypher.length() > 50 ? cypher.substring(0, 50) : cypher);
        HttpResponse<String> result = CypherConnection.executeGetResponse(cypher, NAMESPACE_GRAPH);
        assert result.getStatus() == 200;
    }

    public static void executeUpdateSql(String sql) {
        try (JdbcConnection polyphenyDbConnection = new JdbcConnection(true)) {
            Connection connection = polyphenyDbConnection.getConnection();
            Statement statement = connection.createStatement();
            logger.info("executeUpdateSql: {}", sql.length() > 50 ? sql.substring(0, 50) : sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

    }
}

