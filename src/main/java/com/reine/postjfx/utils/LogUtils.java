package com.reine.postjfx.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.Log;
import com.reine.postjfx.entity.ParamProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志操作工具类
 *
 * @author reine
 */
public class LogUtils {

    public final static ObservableList<Log> logList = FXCollections.observableList(new ArrayList<>());

    /**
     * 数据库存储位置
     */
    private final static String logsPath = "logs";

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 初始化标志位
     */
    private static boolean initializing = true;

    /**
     * 数据库连接
     */
    private static final Connection connection;


    static {
        try {
            // 创建文件夹
            Files.createDirectories(Paths.get(logsPath));
            // 加载数据库驱动
            Class.forName("org.sqlite.JDBC");
            // 建立数据库连接
            connection = DriverManager.getConnection("jdbc:sqlite:logs/log.db");
            // 创建数据表
            String tableSql = """
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
            PreparedStatement preparedStatement = connection.prepareStatement(tableSql);
            preparedStatement.execute();
            // 创建索引
            String indexSql = """
                    CREATE INDEX IF NOT EXISTS datetime_index ON log(datetime);
                    """;
            preparedStatement = connection.prepareStatement(indexSql);
            preparedStatement.execute();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        String insertSql = """
                INSERT INTO log('datetime', 'method','url', 'params','headers', 'body' )
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        String daleteSql = """
                DELETE FROM log WHERE datetime = ?
                """;
        // 保存日志信息到文件
        logList.addListener((ListChangeListener<Log>) c -> {
            // 如果是应用启动时的初始化数据操作，则不进行数据列表的响应操作
            if (!initializing)
                try {
                    while (c.next()) {
                        // 当请求发送成功时，往日志数据库中添加一条数据
                        if (c.wasAdded()) {
                            Log log = c.getAddedSubList().get(0);
                            // 向数据库中写入一条数据
                            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                            preparedStatement.setString(1, log.dateTime());
                            preparedStatement.setString(2, log.method());
                            preparedStatement.setString(3, log.url());
                            preparedStatement.setString(4, mapper.writeValueAsString(log.params()));
                            preparedStatement.setString(5, mapper.writeValueAsString(log.headers()));
                            preparedStatement.setString(6, log.body());
                            preparedStatement.execute();
                        }
                        // 点击日志项的删除按钮时，删除数据库中的日志信息
                        if (c.wasRemoved()) {
                            Log log = c.getRemoved().get(0);
                            // 删除数据库中指定日期时间的数据
                            PreparedStatement preparedStatement = connection.prepareStatement(daleteSql);
                            preparedStatement.setString(1, log.dateTime());
                            preparedStatement.execute();
                        }
                    }
                } catch (SQLException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
        });
    }

    /**
     * 根据传入的日期查询当日日志信息
     */
    public static void readFromFileForLogList(LocalDate date) {
        initializing = true;
        String dateStr = date.format(formatter);
        logList.clear();
        try {
            // 查询当天的所有请求操作
            String sql = "SELECT datetime, method, url, params, headers, body from log where substr(datetime, 1, instr(datetime, '_') - 1) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, dateStr);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            // 初始化完成，将初始化标志字段取反
            initializing = false;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
