package com.reine.postjfx;

import com.reine.postjfx.controller.PostTabController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        PostTabController postTabController = new PostTabController();
        Scene scene = new Scene(postTabController);
        stage.setTitle("PostJFX");
        stage.setScene(scene);
        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource("/image/logo.png")).toString())
        );
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}