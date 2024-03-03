package com.reine.postjfx.utils;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * 辅助类
 * @author reine
 */
public class Helper {

    private static Stage PRIMARY_STAGE;

    private static TabPane TAB_PANE;

    public static Stage getPrimaryStage() {
        return PRIMARY_STAGE;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Helper.PRIMARY_STAGE = primaryStage;
    }

    public static TabPane getTabPane() {
        return TAB_PANE;
    }

    public static void setTabPane(TabPane tabPane) {
        Helper.TAB_PANE = tabPane;
    }
}
