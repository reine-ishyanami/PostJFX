package com.reine.postjfx;

import atlantafx.base.theme.PrimerLight;
import com.reine.postjfx.config.AppProp;
import com.reine.postjfx.controller.TabHistoryController;
import com.reine.postjfx.repository.LogRepository;
import com.reine.postjfx.utils.Helper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        // 窗口最小宽高
        stage.setHeight(625.0);
        stage.setMinHeight(625.0);
        stage.setWidth(1100.0);
        stage.setMinWidth(1100.0);
        Helper.setPrimaryStage(stage);
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
        // stage.
    }

    @Override
    public void init() {
        LogRepository.getInstance().init();
    }

    @Override
    public void stop() throws Exception {
        LogRepository.getInstance().release();
    }

    public static void main(String[] args) {
        launch();
    }
}