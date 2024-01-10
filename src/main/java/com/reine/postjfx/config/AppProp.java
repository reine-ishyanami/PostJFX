package com.reine.postjfx.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 应用配置属性类
 *
 * @author reine
 */
public class AppProp {

    /**
     * 日志文件夹
     */
    public static final String logDir = "logs";

    /**
     * 应用图标路径
     */
    public static final String logoPath = "/image/logo.png";

    /**
     * 应用样式路径
     */
    public static final String cssPath = "css/default.css";

    /**
     * 获取当前用户家目录
     */
    private final static String userHomeDir = System.getProperty("user.home");


    /**
     * 项目名称
     */
    public static final String projectName = "PostJFX";

    /**
     * 版本号
     */
    public static String version;

    /**
     * 数据库存储目录
     */
    public static Path logDbDir = Paths.get(userHomeDir, projectName, logDir);


    static {
        try {
            // 调试应用时执行
            // 读取 build.gradle 文件中的应用版本属性
            Properties properties = new Properties();
            properties.load(new FileInputStream("gradle.properties"));
            // 字符串处理
            version = properties.getProperty("version");
        } catch (IOException e) {
            // 打包成可执行程序时执行
            // 读取环境变量的应用版本属性
            if (e instanceof FileNotFoundException) {
                version = System.getProperty("jpackage.app-version");
            } else throw new RuntimeException(e);
        }
    }
}
