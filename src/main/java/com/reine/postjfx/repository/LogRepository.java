package com.reine.postjfx.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.entity.property.HeaderProperty;
import com.reine.postjfx.entity.property.ParamProperty;
import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.utils.Constant;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志数据表操作类
 *
 * @author reine
 */
public class LogRepository {

    /**
     * jackson 数据转换
     */
    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 数据库连接
     */
    private static final Connection connection;


    static {
        try {
            // 创建文件夹
            Path dir = Constant.logDbDir;
            Files.createDirectories(dir);
            // 加载数据库驱动
            Class.forName("org.sqlite.JDBC");
            // 建立数据库连接
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/log.db", dir));
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 建表语句
     */
    private static final String tableSql = """
            CREATE TABLE IF NOT EXISTS log
            (
                datetime TEXT,
                method   TEXT,
                params   TEXT,
                headers  TEXT,
                body     TEXT,
                url      TEXT
            );
            """;

    /**
     * 创建数据库
     */
    public static void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(tableSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 建索引语句
     */
    private static final String indexSql = """
            CREATE INDEX IF NOT EXISTS datetime_index ON log(datetime);
            """;


    /**
     * 创建索引
     */
    public static void createIndex() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(indexSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 插入语句
     */
    private static final String insertSql = """
            INSERT INTO log('datetime', 'method','url', 'params','headers', 'body' )
            VALUES (?, ?, ?, ?, ?, ?);
            """;


    /**
     * 往数据库中插入一条数据
     */
    public static void insertOne(Log log) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, log.dateTime());
            preparedStatement.setString(2, log.method());
            preparedStatement.setString(3, log.url());
            preparedStatement.setString(4, mapper.writeValueAsString(log.params()));
            preparedStatement.setString(5, mapper.writeValueAsString(log.headers()));
            preparedStatement.setString(6, log.body());
            preparedStatement.execute();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 删除语句
     */
    private static final String deleteSql = """
            DELETE FROM log WHERE datetime = ?
            """;

    /**
     * 删除数据库中指定一条数据
     */
    public static void removeOne(Log log) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.setString(1, log.dateTime());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期格式化器
     */
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 查询语句
     */
    private static final String querySql = """
            SELECT datetime, method, url, params, headers, body
            FROM log
            WHERE substr(datetime, 1, instr(datetime, '_') - 1) = ?
            ORDER BY datetime DESC
            """;

    /**
     * 从数据库中查询指定数据
     */
    public static List<Log> selectListByDate(LocalDate date) {
        try {
            String dateStr = date.format(formatter);
            PreparedStatement preparedStatement = connection.prepareStatement(querySql);
            preparedStatement.setString(1, dateStr);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Log> logList = new ArrayList<>();
            while (resultSet.next()) {
                String datetime = resultSet.getString("datetime");
                String method = resultSet.getString("method");
                String url = resultSet.getString("url");
                String paramsStr = resultSet.getString("params");
                List<ParamProperty> params = mapper.readValue(paramsStr, new TypeReference<>() {
                });
                String headersStr = resultSet.getString("headers");
                List<HeaderProperty> headers = mapper.readValue(headersStr, new TypeReference<>() {
                });
                String body = resultSet.getString("body");
                Log log = new Log(datetime,
                        method,
                        url,
                        FXCollections.observableList(params),
                        FXCollections.observableList(headers),
                        body);
                // 添加到日志列表中
                logList.add(log);
            }
            return logList;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
