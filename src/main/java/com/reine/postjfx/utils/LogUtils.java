package com.reine.postjfx.utils;

import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.repository.LogRepository;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;
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
     * 右侧历史记录数据
     */
    public final static ObservableList<Log> logList = FXCollections.observableList(new ArrayList<>());

    /**
     * 该监视属性用于与日期选择器的值进行绑定，方便其他组件调用修改日期选择器的值
     */
    public static final SimpleObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();

    /**
     * 初始化标志位
     */
    private static boolean initializing = false;

    /**
     * 被删除的日志栈
     */
    private static final Deque<Log> logStack = new ArrayDeque<>();

    /**
     * 被删除的日志栈的大小
     */
    public static final SimpleIntegerProperty logStackSize = new SimpleIntegerProperty(0);

    static {
        LogRepository.createTable();
        LogRepository.createIndex();
        // 保存日志信息到数据库
        logList.addListener((ListChangeListener<Log>) c -> {
            // 如果是应用启动时的初始化数据操作，则不进行数据列表的响应操作
            if (!initializing)
                while (c.next()) {
                    // 往日志列表中写入数据时，将新数据写入到数据库
                    if (c.wasAdded()) {
                        Log log = c.getAddedSubList().get(0);
                        // 向数据库中写入一条数据
                        LogRepository.insertOne(log);
                    }
                    // 删除日志列表中的数据时，删除数据库中的日志信息
                    if (c.wasRemoved()) {
                        Log log = c.getRemoved().get(0);
                        // 删除数据库中指定日期时间的数据
                        LogRepository.removeOne(log);
                        push(log);
                    }
                }
        });

        // 日期改变时，查询对应日期的日志
        dateProperty.addListener(((observable, oldValue, newValue) -> {
            readFromDbForLogList(newValue);
        }));
    }

    /**
     * 根据传入的日期查询当日日志信息
     */
    private static void readFromDbForLogList(LocalDate date) {
        // 设置为正在初始化数据
        initializing = true;
        // 清空数据，等待重新装填
        logList.clear();
        List<Log> logs = LogRepository.selectListByDate(date);
        logList.addAll(logs);
        // 初始化完成，将初始化标志字段取反
        initializing = false;
    }

    /**
     * 恢复一条被删除的数据
     */
    public static void restoreHistory(LocalDate date) {
        // 从栈中弹出一条最近的删除记录
        Log recentLog = pop();
        // 将数据重新插入数据库中
        LogRepository.insertOne(recentLog);
        // 重新加载数据
        readFromDbForLogList(date);
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
