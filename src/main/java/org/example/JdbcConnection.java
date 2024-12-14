package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Wrapper;

import static org.example.Main.logger;

public class JdbcConnection implements AutoCloseable {

    private final static String dbHost = "localhost";
    private final static int port = 20590;

    private final Connection conn;

    public JdbcConnection( boolean autoCommit ) throws SQLException {
        try {
            Class.forName( "org.polypheny.jdbc.PolyphenyDriver" );
        } catch ( ClassNotFoundException e ) {
            logger.error( "Polypheny JDBC Driver not found", e );
        }
        final String url = "jdbc:polypheny://" + dbHost + ":" + port + "/?strict=false";
        conn = DriverManager.getConnection( url, "pa", "" );
        conn.setAutoCommit( autoCommit );
    }

    public Connection getConnection() {
        return conn;
    }

    @Override
    public void close() throws SQLException {
        if ( conn.isClosed() ) {
            return;
        }
        if ( !conn.getAutoCommit() ) {
            conn.commit();
        }
        conn.close();
    }
}
