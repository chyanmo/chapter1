package org.smart4j.chapter1.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.smart4j.chapter1.utils.PropsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public final class DatabaseHelper {
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    private static final BasicDataSource DATA_SOURCE;

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    static {
        Properties properties = PropsUtil.loadProps("config.properties");
        DRIVER = properties.getProperty("jdbc.driver");
        URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        // 先从 ThreadLocal 获取
        Connection connection = CONNECTION_HOLDER.get();
        // 再从连接池获取
        if (connection == null ) {
            try {
                connection = DATA_SOURCE.getConnection();
            } catch (SQLException e){
                log.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
        return connection;
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        try {
            return QUERY_RUNNER.query(
                    getConnection(), sql, new BeanListHandler<>(entityClass), params);
        } catch (SQLException e) {
            log.error("query entity list failure: {}", sql, e);
            throw new IllegalStateException("Failed to query entity list", e);
        }
    }

    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        try {
            return QUERY_RUNNER.query(
                    getConnection(), sql, new BeanHandler<>(entityClass), params);
        } catch (SQLException e) {
            log.error("query entity failure: {}", sql, e);
            throw new IllegalStateException("Failed to query entity", e);
        }
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try {
            return QUERY_RUNNER.query(getConnection(), sql, new MapListHandler(), params);
        } catch (SQLException e) {
            log.error("execute query failure: {}", sql, e);
            throw new IllegalStateException("Failed to execute query", e);
        }
    }

    public static int executeUpdate(String sql, Object... params) {
        try {
            return QUERY_RUNNER.update(getConnection(), sql, params);
        } catch (SQLException e) {
            log.error("execute update failure: {}", sql, e);
            throw new IllegalStateException("Failed to execute update", e);
        }
    }

    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (MapUtils.isEmpty(fieldMap)) {
            log.error("can not insert entity: fieldMap is empty");
            return false;
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(fieldName);
            values.append("?");
        }

        String sql = "INSERT INTO " + getTableName(entityClass)
                + " (" + columns + ") VALUES (" + values + ")";
        return executeUpdate(sql, fieldMap.values().toArray()) == 1;
    }

    public static <T> boolean updateEntity(Class<T> entityClass, long id,
                                           Map<String, Object> fieldMap) {
        if (MapUtils.isEmpty(fieldMap)) {
            log.error("can not update entity: fieldMap is empty");
            return false;
        }

        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
            }
            columns.append(fieldName).append("=?");
        }

        String sql = "UPDATE " + getTableName(entityClass)
                + " SET " + columns + " WHERE id=?";
        List<Object> paramList = new ArrayList<>(fieldMap.values());
        paramList.add(id);
        return executeUpdate(sql, paramList.toArray()) == 1;
    }

    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }

    private static String getTableName(Class<?> entityClass) {
        // 实体类名
        String simpleName = entityClass.getSimpleName();
        StringBuilder tableName = new StringBuilder(simpleName.length());
        // 驼峰命名转下划线命名
        for (int i = 0; i < simpleName.length(); i++) {
            char ch = simpleName.charAt(i);
            if (Character.isUpperCase(ch) && i > 0) {
                tableName.append('_');
            }
            tableName.append(Character.toLowerCase(ch));
        }
        return tableName.toString();
    }
}
