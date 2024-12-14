package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.polypheny.jdbc.PolyConnection;
import org.polypheny.jdbc.multimodel.DocumentResult;
import org.polypheny.jdbc.multimodel.PolyStatement;
import org.polypheny.jdbc.multimodel.Result;
import org.polypheny.jdbc.types.PolyDocument;
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
        try (JdbcConnection polyphenyDbConnection = new JdbcConnection(true)) {
            PolyConnection polyConnection = (PolyConnection) polyphenyDbConnection.getConnection();
            PolyStatement polyStatement = polyConnection.createPolyStatement();
            logger.info("executeMql: {}", mql);
            // TODO Caused by: org.polypheny.jdbc.PrismInterfaceServiceException: Language mql not supported.
            Result result = polyStatement.execute(NAMESPACE_DOC, "mql", mql);
            logger.info("Result: {}", result);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
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
