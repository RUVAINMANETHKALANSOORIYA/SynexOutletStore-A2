package fakes;

import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbcFakes {

    public static Connection createFakeConnection() {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("setAutoCommit")) return null;
                    if (method.getName().equals("commit")) return null;
                    if (method.getName().equals("rollback")) return null;
                    if (method.getName().equals("close")) return null;
                    if (method.getName().equals("isClosed")) return false;
                    if (method.getName().equals("prepareStatement")) return createFakePreparedStatement();
                    if (method.getName().equals("createStatement")) return createFakeStatement();
                    return null;
                }
        );
    }

    private static Statement createFakeStatement() {
        return (Statement) Proxy.newProxyInstance(
                Statement.class.getClassLoader(),
                new Class[]{Statement.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("executeQuery")) return createFakeResultSet(1);
                    if (method.getName().equals("executeUpdate")) return 1;
                    if (method.getName().equals("close")) return null;
                    return null;
                }
        );
    }

    private static PreparedStatement createFakePreparedStatement() {
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("executeQuery")) return createFakeResultSet(1);
                    if (method.getName().equals("executeUpdate")) return 1; // Return 1 for success
                    if (method.getName().startsWith("set")) return null;
                    if (method.getName().equals("close")) return null;
                    if (method.getName().equals("getGeneratedKeys")) return createFakeResultSet(1);
                    return null;
                }
        );
    }

    private static ResultSet createFakeResultSet(int rowCount) {
        java.util.concurrent.atomic.AtomicInteger current = new java.util.concurrent.atomic.AtomicInteger(0);
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class[]{ResultSet.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("next")) {
                        return current.getAndIncrement() < rowCount;
                    }
                    if (method.getName().equals("close")) return null;
                    if (method.getName().equals("getLong")) return 1L;
                    if (method.getName().equals("getString")) return "dummy";
                    if (method.getName().equals("getBigDecimal")) return java.math.BigDecimal.ONE;
                    if (method.getName().equals("getInt")) return 1;
                    if (method.getName().equals("getDouble")) return 1.0;
                    if (method.getName().equals("getDate")) return java.sql.Date.valueOf(java.time.LocalDate.now());
                    if (method.getName().equals("getTimestamp")) return java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
                    return null;
                }
        );
    }

    public static class FakeDriver implements Driver {
        static {
            try {
                DriverManager.registerDriver(new FakeDriver());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            if (acceptsURL(url)) return createFakeConnection();
            return null;
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return url != null && url.startsWith("jdbc:fake:");
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) { return new DriverPropertyInfo[0]; }
        @Override
        public int getMajorVersion() { return 1; }
        @Override
        public int getMinorVersion() { return 0; }
        @Override
        public boolean jdbcCompliant() { return false; }
        @Override
        public Logger getParentLogger() { return null; }
    }
}
