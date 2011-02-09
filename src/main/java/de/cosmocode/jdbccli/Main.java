package de.cosmocode.jdbccli;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

/**
 * @author Tobias Sarnowski
 */
public final class Main {

    private static final Options options = new Options();

    private Main() {
    }

    public static void main(String[] arguments) {
        final CmdLineParser parser = new CmdLineParser(options);

        // parse options
        try {
            parser.parseArgument(arguments);
        } catch (CmdLineException e) {
            System.err.println("Usage:  java [options] jdbcUrl");
            parser.printUsage(System.err);
            throw new IllegalArgumentException(e);
        }

        if (options.getLoad() != null) {
            try {
                Class.forName(options.getLoad());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        final Properties connectionProps = new Properties();
        if (options.getUsername() != null) {
            connectionProps.put("user", options.getUsername());
            connectionProps.put("password", options.getPassword());
        }

        // establish connection
        final Connection connection;
        try {
            connection = DriverManager.getConnection(options.getJdbcUrl(), connectionProps);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        // read input and execute
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String sql;
        try {
            while ((sql = in.readLine()) != null) {
                sql = sql.trim();
                if (sql.length() == 0) {
                    continue;
                }

                // exit?
                if ("QUIT".equals(sql.toUpperCase())) {
                    System.exit(0);
                }

                // prepare the query
                final PreparedStatement stmt = connection.prepareStatement(sql);

                // and execute it
                final JsonFactory jsonFactory = new JsonFactory();
                final JsonGenerator json = jsonFactory.createJsonGenerator(System.out, JsonEncoding.UTF8);

                if (!sql.toUpperCase().startsWith("SELECT ")) {
                    final int result = stmt.executeUpdate();
                    json.writeStartObject();
                    json.writeObjectField("update", result);
                    json.writeEndObject();
                } else {
                    ResultSet result = stmt.executeQuery();
                    json.writeStartArray();
                    while (result.next()) {
                        json.writeStartObject();
                        final ResultSetMetaData meta = result.getMetaData();
                        final int count = meta.getColumnCount();
                        for (int n = 1; n <= count; n++) {
                            final String rowName = meta.getColumnName(n);
                            final String rowValue = result.getString(n);
                            json.writeObjectField(rowName, rowValue);
                        }
                        json.writeEndObject();
                    }
                    json.writeEndArray();
                }

                json.flush();
                System.out.println();

                if (options.isSingle()) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
