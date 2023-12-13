package com.reine.postjfx.utils;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * @author reine
 */
public class NodeManage {

    private static Stage PRIMARY_STAGE;

    private static TabPane TAB_PANE;

    public static Stage getPrimaryStage() {
        return PRIMARY_STAGE;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        NodeManage.PRIMARY_STAGE = primaryStage;
    }

    public static TabPane getTabPane() {
        return TAB_PANE;
    }

    public static void setTabPane(TabPane tabPane) {
        NodeManage.TAB_PANE = tabPane;
    }
}
