package com.reine.postjfx.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.Log;
import com.reine.postjfx.entity.ParamProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 日志操作工具类
 *
 * @author reine
 */
public class LogUtils {

    /**
     * 日期格式化器
     */
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * jackson 数据转换
     */
    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 右侧历史记录数据
     */
    public final static ObservableList<Log> logList = FXCollections.observableList(new ArrayList<>());

    /**
     * 获取用户家目录
     */
    private final static String userHomeDir = System.getProperty("user.home");

    /**
     * 项目名称
     */
    private final static String projectName = "PostJFX";

    /**
     * 数据库存储位置
     */
    private final static String logsPath = "logs";

    /**
     * 该监视属性用于与日期选择器的值进行绑定，方便其他组件调用修改日期选择器的值
     */
    public static final SimpleObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();


    /**
     * 初始化标志位
     */
    private static boolean initializing = true;

    /**
     * 数据库连接
     */
    private static final Connection connection;

    /**
     * 被删除的日志栈
     */
    private static final Deque<Log> logStack = new ArrayDeque<>();

    /**
     * 被删除的日志栈的大小
     */
    public static final SimpleIntegerProperty logStackSize = new SimpleIntegerProperty(0);

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
     * 建索引语句
     */
    private static final String indexSql = """
            CREATE INDEX IF NOT EXISTS datetime_index ON log(datetime);
            """;

    /**
     * 插入语句
     */
    private static final String insertSql = """
            INSERT INTO log('datetime', 'method','url', 'params','headers', 'body' )
            VALUES (?, ?, ?, ?, ?, ?);
            """;

    /**
     * 删除语句
     */
    private static final String deleteSql = """
            DELETE FROM log WHERE datetime = ?
            """;

    static {
        try {
            // 创建文件夹
            Path dir = Paths.get(userHomeDir, projectName, logsPath);
            Files.createDirectories(dir);
            // 加载数据库驱动
            Class.forName("org.sqlite.JDBC");
            // 建立数据库连接
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s/log.db", dir));
            PreparedStatement preparedStatement = connection.prepareStatement(tableSql);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(indexSql);
            preparedStatement.execute();
        } catch (ClassNotFoundException | SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        // 保存日志信息到数据库
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
                            PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
                            preparedStatement.setString(1, log.dateTime());
                            preparedStatement.execute();
                            push(log);
                        }
                    }
                } catch (SQLException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
        });
    }

    /**
     * 查询语句
     */
    private static final String querySql = """
            SELECT datetime, method, url, params, headers, body
            FROM log
            WHERE substr(datetime, 1, instr(datetime, '_') - 1) = ?
            ORDER BY datetime
            """;

    /**
     * 根据传入的日期查询当日日志信息
     */
    public static void readFromFileForLogList(LocalDate date) {
        initializing = true;
        String dateStr = date.format(formatter);
        logList.clear();
        try {
            // 查询当天的所有请求操作
            PreparedStatement preparedStatement = connection.prepareStatement(querySql);
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

    /**
     * 恢复一条被删除的数据
     */
    public static void restoreHistory(LocalDate date) {
        // 从栈中弹出一条最近的删除记录
        try {
            Log recentLog = pop();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, recentLog.dateTime());
            preparedStatement.setString(2, recentLog.method());
            preparedStatement.setString(3, recentLog.url());
            preparedStatement.setString(4, mapper.writeValueAsString(recentLog.params()));
            preparedStatement.setString(5, mapper.writeValueAsString(recentLog.headers()));
            preparedStatement.setString(6, recentLog.body());
            preparedStatement.execute();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        readFromFileForLogList(date);
    }

    /**
     * 将数据推入栈中
     */
    private static void push(Log log) {
        // 添加到被删除的日志栈中
        logStack.push(log);
        // 设置栈大小
        logStackSize.set(logStack.size());

    }

    /**
     * 将栈中数据弹出
     */
    private static Log pop() {
        Log recentLog = logStack.pop();
        logStackSize.set(logStack.size());
        return recentLog;
    }

}
