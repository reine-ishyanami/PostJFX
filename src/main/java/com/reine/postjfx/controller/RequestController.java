package com.reine.postjfx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.reine.postjfx.HelloApplication;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.HttpUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class RequestController extends VBox {

    @FXML
    private ChoiceBox<RequestMethodEnum> methodChoiceBox;

    @FXML
    private TextField urlField;

    private ResponseController responseController;

    public RequestController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("request-form.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void setResponseController(ResponseController responseController) {
        this.responseController = responseController;
    }

    @FXML
    void sendRequest() {
        String url = urlField.getText();
        RequestMethodEnum item = methodChoiceBox.getSelectionModel().getSelectedItem();
        switch (item) {
            case GET -> HttpUtils.get(url, null, null).thenAccept((response) -> {
                responseController.showResult(response);
            });
        }
    }

    @FXML
    void initialize() {
        methodChoiceBox.getItems().addAll(RequestMethodEnum.GET, RequestMethodEnum.POST, RequestMethodEnum.PUT, RequestMethodEnum.DELETE);
        methodChoiceBox.setValue(RequestMethodEnum.GET);
        methodChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RequestMethodEnum object) {
                return object.getName();
            }

            @Override
            public RequestMethodEnum fromString(String string) {
                return null;
            }
        });

    }

}
