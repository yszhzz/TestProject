package cn.mypro.utils;

import cn.mypro.utils.exception.ConnectException;
import oracle.jdbc.OracleConnection;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DataBaseUtils {

    private static final String driver_class = "oracle.jdbc.driver.OracleDriver";

    public static Connection getConnection(DbName dbName, boolean autoCommit) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        Properties properties = Configs.getProperties(dbName.getName());
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pwd = properties.getProperty("db.pwd");

        return connectDataBase(url, user, pwd, autoCommit);
    }

    public static Connection getConnection(DbName dbName) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        return getConnection(dbName, false);
    }

    private static Connection connectDataBase(String url, String user, String password, boolean autoCommit) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        try {
            Class.forName(driver_class).newInstance();
            Properties info = new Properties();
            if (user != null) {
                info.put("user", user);
            }
            if (password != null) {
                info.put("password", password);
            }
            info.put(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "30000");
            Connection connection = DriverManager.getConnection(url, info);
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 测试连接
     *
     * @param connection 需测试的连接
     * @throws ConnectException
     */
    public static void testDataBaseConnection(Connection connection) throws ConnectException {
        if (connection == null) {
            throw new ConnectException("connection is null");
        }
        try {
            if (connection.isClosed()) {
                throw new ConnectException("连接已关闭！");
            }
            queryString(connection, "SELECT 1 FROM DUAL", 1);
        } catch (SQLException e) {
            throw new ConnectException("数据库连接测试失败！", e);
        }
    }

    public static Connection ensureDataBaseConnection(DbName dbName, Connection connection) {
        Connection conn = connection;
        while (true) {
            try {
                if (conn == null || conn.isClosed()) {
                    conn = DataBaseUtils.getConnection(dbName);
                }
                testDataBaseConnection(conn);
                break;
            } catch (Exception e) {
                DataBaseUtils.closeQuietly(conn);
                conn = null;
                e.printStackTrace();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e2) {
                    //
                }
            }
        }
        return conn;
    }

    public static Connection ensureDataBaseConnection(DbName dbName) {
        return ensureDataBaseConnection(dbName, null);
    }

    /**
     * 执行批处理
     *
     * @param ps 需执行批处理的语句
     * @throws SQLException
     */
    public static List<int[]> executeBatch(PreparedStatement... ps) throws SQLException {
        List<int[]> resultList = new ArrayList<>();
        for (PreparedStatement preparedStatement : ps) {
            int[] ns = preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            resultList.add(ns);
        }
        return resultList;
    }

    /**
     * 释放资源
     *
     * @param autoCloseables 需释放的资源
     */
    public static void closeQuietly(AutoCloseable... autoCloseables) {
        for (AutoCloseable closeable : autoCloseables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    /**
     * 回滚
     *
     * @param connections 需回滚的连接
     */
    public static void rollbackSiently(Connection... connections) {
        for (Connection connection : connections) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                //
            }
        }
    }

    /**
     * 查询一列（字符串）
     *
     * @param connection 连接
     * @param sql        语句
     * @param index      列
     * @param params     参数
     * @return 列值
     * @throws SQLException 异常
     */
    public static String queryString(Connection connection, String sql, int index, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setMaxRows(1);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(index);
            } else {
                return null;
            }
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    /**
     * 查询一列（字符串）
     *
     * @param connection 连接
     * @param sql        语句
     * @param index      列
     * @param params     参数
     * @return 列值
     * @throws SQLException 异常
     */
    public static <T> T queryOne(Connection connection, String sql, int index, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return (T) resultSet.getObject(index);
            } else {
                return null;
            }
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    /**
     * 查询一列（字符串）
     *
     * @param connection 连接
     * @param sql        语句
     * @param index      列
     * @param params     参数
     * @return 列值
     * @throws SQLException 异常
     */
    public static <T> T queryOne(Connection connection, String sql, int index, Class<T> clz, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (clz.equals(String.class)) {
                    return (T) resultSet.getString(index);
                } else if (clz.equals(Long.class)) {
                    Long value = resultSet.getLong(index);
                    if (value == 0 && resultSet.wasNull()) {
                        return null;
                    } else {
                        return (T) value;
                    }
                } else if (clz.equals(Integer.class)) {
                    Integer value = resultSet.getInt(index);
                    if (value == 0 && resultSet.wasNull()) {
                        return null;
                    } else {
                        return (T) value;
                    }
                } else {
                    return (T) resultSet.getObject(index);
                }
            } else {
                return null;
            }
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    public static Long queryLong(Connection connection, String sql, int index, Object... params) throws SQLException {
        return queryOne(connection, sql, index, Long.class, params);
    }

    public static Integer queryInteger(Connection connection, String sql, int index, Object... params) throws SQLException {
        return queryOne(connection, sql, index, Integer.class, params);
    }

    /**
     * 查询第一条记录
     *
     * @param connection 连接
     * @param sql        语句
     * @param params     参数
     * @return 值Map
     * @throws SQLException 异常
     */
    public static Map<String, Object> queryMap(Connection connection, String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return processToMap(resultSet);
            } else {
                return null;
            }
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    /**
     * 查询记录列表
     *
     * @param connection 连接
     * @param sql        语句
     * @param params     参数
     * @return 值MapList
     * @throws SQLException 异常
     */
    public static List<Map<String, Object>> queryMapList(Connection connection, String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(processToMap(resultSet));
            }
            return list;
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    /**
     * 查询一列列表
     *
     * @param connection 连接
     * @param sql        语句
     * @param params     参数
     * @return 值List
     * @throws SQLException 异常
     */
    public static <T> List<T> queryList(Connection connection, String sql, int index, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add((T) resultSet.getObject(index));
            }
            return list;
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement, resultSet);
        }
    }

    public static int executeUpdate(Connection connection, String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            fillStatement(preparedStatement, params);
            return preparedStatement.executeUpdate();
        } finally {
            DataBaseUtils.closeQuietly(preparedStatement);
        }
    }

    private static Map<String, Object> processToMap(ResultSet resultSet) throws SQLException {
        Map<String, Object> map = new CaseInsensitiveHashMap();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            String columnName = rsmd.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(i);
            }
            map.put(columnName, resultSet.getObject(i));
        }

        return map;

    }

    public static String getSysGuid(Connection connection) throws SQLException {
        return queryString(connection, "SELECT RAWTOHEX(SYS_GUID()) FROM DUAL", 1);
    }

    private static void fillStatement(PreparedStatement preparedStatement, Object... params) throws SQLException {
        int index = 1;
        for (Object param : params) {
            if (param == null) {
                preparedStatement.setNull(index++, Types.VARCHAR);
            } else if (param instanceof String) {
                preparedStatement.setString(index++, (String) param);
            } else if (param instanceof Integer) {
                preparedStatement.setInt(index++, (int) param);
            } else if (param instanceof Character) {
                preparedStatement.setString(index++, String.valueOf(param));
            } else if (param instanceof Float) {
                preparedStatement.setFloat(index++, (float) param);
            } else if (param instanceof Long) {
                preparedStatement.setLong(index++, (long) param);
            } else if (param instanceof Boolean) {
                preparedStatement.setBoolean(index++, (boolean) param);
            } else if (param instanceof Double) {
                preparedStatement.setDouble(index++, (double) param);
            } else if (param instanceof Byte) {
                preparedStatement.setByte(index++, (byte) param);
            } else if (param instanceof Short) {
                preparedStatement.setShort(index++, (short) param);
            } else if (param instanceof java.sql.Date) {
                preparedStatement.setDate(index++, (java.sql.Date) param);
            } else if (param instanceof Time) {
                preparedStatement.setTime(index++, (Time) param);
            } else if (param instanceof Timestamp) {
                preparedStatement.setTimestamp(index++, (Timestamp) param);
            } else if (param instanceof Blob) {
                preparedStatement.setBlob(index++, (Blob) param);
            } else if (param instanceof Clob) {
                preparedStatement.setClob(index++, (Clob) param);
            } else if (param instanceof byte[]) {
                preparedStatement.setBytes(index++, (byte[]) param);
            } else if (param instanceof InputStream) {
                preparedStatement.setBinaryStream(index++, (InputStream) param);
            } else {
                preparedStatement.setObject(index++, param);
            }
        }
    }


    /**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because
     * databases don't consistently handle the casing of column names.
     * <p>
     * <p>The keys are stored as they are given [BUG #DBUTILS-34], so we maintain
     * an internal mapping from lowercase keys to the real keys in order to
     * achieve the case insensitive lookup.
     * <p>
     * <p>Note: This implementation does not allow {@code null}
     * for key, whereas {@link LinkedHashMap} does, because of the code:
     * <pre>
     * key.toString().toLowerCase()
     * </pre>
     */
    private static class CaseInsensitiveHashMap extends LinkedHashMap<String, Object> {
        /**
         * The internal mapping from lowercase keys to the real keys.
         * <p>
         * <p>
         * Any query operation using the key
         * ({@link #get(Object)}, {@link #containsKey(Object)})
         * is done in three steps:
         * <ul>
         * <li>convert the parameter key to lower case</li>
         * <li>get the actual key that corresponds to the lower case key</li>
         * <li>query the map with the actual key</li>
         * </ul>
         * </p>
         */
        private final Map<String, String> lowerCaseMap = new HashMap<>();

        /**
         * Required for serialization support.
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = -2848100435296897392L;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.containsKey(realKey);
            // Possible optimisation here:
            // Since the lowerCaseMap contains a mapping for all the keys,
            // we could just do this:
            // return lowerCaseMap.containsKey(key.toString().toLowerCase());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.get(realKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object put(String key, Object value) {
            /*
             * In order to keep the map and lowerCaseMap synchronized,
             * we have to remove the old mapping before putting the
             * new one. Indeed, oldKey and key are not necessaliry equals.
             * (That's why we call super.remove(oldKey) and not just
             * super.put(key, value))
             */
            Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putAll(Map<? extends String, ?> m) {
            for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                this.put(key, value);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
            return super.remove(realKey);
        }
    }
}
