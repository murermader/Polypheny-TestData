package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String NAMESPACE_DOC = "doc";
    public static final String NAMESPACE_REL = "rel";
    public static final String NAMESPACE_GRAPH = "graph";

    public static void main(String[] args) {
        logger.info("Polypheny-TestData started.");
        createNamespaces();
        insertTestData();
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
        executeUpdateSql(
                "DROP NAMESPACE IF EXISTS " + NAMESPACE_REL
        );
        executeUpdateSql(
                "CREATE RELATIONAL NAMESPACE IF NOT EXISTS " + NAMESPACE_REL
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

    private static void insertTestData() {
        logger.info("Inserting test data");
        executeMql("db.geoCollection2.insertMany( { test: \"Hello World!\" } )");
    }

    public static void executeMql(String mql) {
        logger.info("executeMql: {}", mql);
        HttpResponse<String> result = MongoConnection.executeGetResponse(mql, NAMESPACE_DOC);
        assert result.getStatus() == 200;
    }

    public static void executeCypher(String cypher) {
        logger.info("executeCypher: {}", cypher);
        HttpResponse<String> result = CypherConnection.executeGetResponse(cypher, NAMESPACE_GRAPH);
        assert result.getStatus() == 200;
    }

    public static void executeUpdateSql(String sql) {
        try (JdbcConnection polyphenyDbConnection = new JdbcConnection(true)) {
            Connection connection = polyphenyDbConnection.getConnection();
            Statement statement = connection.createStatement();
            logger.info("executeUpdateSql: {}", sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

    }
}

