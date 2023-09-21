package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

import java.io.IOException;

/**
 * 具体单个请求页面
 * @author reine
 */
public class PostPageController extends SplitPane {

    @FXML
    private RequestController requestController;

    @FXML
    private ResponseController responseController;

    public PostPageController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("post-page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    void initialize() {
        requestController.setResponseController(responseController);
        responseController.setMainController(this);
    }


}
