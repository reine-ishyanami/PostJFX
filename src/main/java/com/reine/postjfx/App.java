package com.reine.postjfx;

import com.reine.postjfx.controller.TabHistoryController;
import com.reine.postjfx.config.AppProp;
import com.reine.postjfx.utils.NodeManage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 窗口最小宽高
        stage.setHeight(625.0);
        stage.setMinHeight(625.0);
        stage.setWidth(1100.0);
        stage.setMinWidth(1100.0);
        NodeManage.setPrimaryStage(stage);
        TabHistoryController root = new TabHistoryController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(App.class.getResource(AppProp.cssPath)).toString()
        );
        stage.setTitle(AppProp.projectName);
        stage.setScene(scene);
        stage.getIcons().add(
                new Image(Objects.requireNonNull(App.class.getResource(AppProp.logoPath)).toString())
        );
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}