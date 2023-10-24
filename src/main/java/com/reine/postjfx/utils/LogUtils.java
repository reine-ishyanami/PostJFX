package com.reine.postjfx.utils;

import com.reine.postjfx.entity.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * 日志操作工具类
 * @author reine
 */
public class LogUtils {

    public final static ObservableList<Log> logList = FXCollections.observableList(new ArrayList<>());

    /**
     * 保存日志信息到文件
     */
    public static void saveLogListToFile(){

    }

    /**
     * 读取文件信息写入日志列表
     */
    public static void readFromFileForLogList(){

    }

}
