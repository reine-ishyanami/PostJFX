package com.reine.postjfx.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 常量类
 *
 * @author reine
 */
public class Constant {

    /**
     * 项目名称
     */
    public static final String projectName = "PostJFX";

    /**
     * 日志文件夹
     */
    public static final String logDir = "logs";

    /**
     * 版本号
     */
    public static final String version = "1.2.5";

    /**
     * 应用图标路径
     */
    public static final String logoPath = "/image/logo.png";

    /**
     * 应用样式路径
     */
    public static final String cssPath = "/css/default.css";

    /**
     * 获取当前用户家目录
     */
    private final static String userHomeDir = System.getProperty("user.home");

    /**
     * 数据库存储目录
     */
    public static final Path logDbDir = Paths.get(userHomeDir, projectName, logDir);

}
