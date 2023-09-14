package com.reine.postjfx.controller;

import com.reine.postjfx.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

import java.io.IOException;

/**
 * @author reine
 */
public class MainController extends SplitPane {

    @FXML
    private RequestController requestController;

    @FXML
    private ResponseController responseController;

    public MainController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    void initialize() {
        requestController.setResponseController(responseController);
    }


}
