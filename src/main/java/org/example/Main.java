package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Hello, World!");

        createNamespaces();

    }

    private static void createNamespaces() {
        // Create namespaces
        for (String namespace : List.of("doc", "rel")){
            executeUpdate(
                    "DROP NAMESPACE IF EXISTS " + namespace
            );
            executeUpdate(
                    "CREATE RELATIONAL NAMESPACE IF NOT EXISTS " + namespace
            );
        }

        // Graph
        // TODO: Does not work...
//        executeUpdate(
//                "DROP NAMESPACE IF EXISTS \"graph\""
//        );
//        executeUpdate(
//                "CREATE DATABASE graph ON STORE hsqldb"
//        );
    }

    public static void executeUpdate(String sql) {
        try (JdbcConnection polyphenyDbConnection = new JdbcConnection(true)) {
            Connection connection = polyphenyDbConnection.getConnection();
            Statement statement = connection.createStatement();
            logger.info("executeUpdate: {}", sql);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
