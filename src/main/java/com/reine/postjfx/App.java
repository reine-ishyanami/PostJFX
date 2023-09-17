package com.reine.postjfx;

import com.reine.postjfx.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController mainController = new MainController();
        Scene scene = new Scene(mainController);
        stage.setTitle("PostJFX");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/image/logo.png"));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}