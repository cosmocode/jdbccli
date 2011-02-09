package de.cosmocode.jdbccli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * @author Tobias Sarnowski
 */
final class Options {

    @Argument(required = true)
    private String jdbcUrl;

    @Option(name = "-u", required = false, aliases = "--username", usage = "Username to access the database.")
    private String username;

    @Option(name = "-p", required = false, aliases = "--password", usage = "Password to access the database.")
    private String password;

    @Option(name = "-l", required = false, aliases = "--load", usage = "Class name to manually load.")
    private String load;

    @Option(name = "-s", required = false, aliases = "--single", usage = "Exit after one query.")
    private boolean single;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getLoad() {
        return load;
    }

    public boolean isSingle() {
        return single;
    }

    @Override
    public String toString() {
        return "Options{" +
                "jdbcUrl='" + jdbcUrl + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", load='" + load + '\'' +
                ", single='" + single + '\'' +
                '}';
    }
}
