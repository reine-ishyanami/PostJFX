package com.reine.postjfx;

import com.reine.postjfx.controller.TabHistoryController;
import com.reine.postjfx.config.AppProp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
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
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}