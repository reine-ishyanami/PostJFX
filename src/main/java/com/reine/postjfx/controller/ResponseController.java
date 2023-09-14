package com.reine.postjfx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * @author reine
 */
public class ResponseController extends VBox {
    @FXML
    private Label codeLabel;

    @FXML
    private TextArea dataTextArea;


    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("response-data.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void showResult(HttpResponse<String> response) {
        int i = response.statusCode();
        switch (i / 100) {
            case 2 -> codeLabel.setTextFill(Color.GREEN);
            case 3 -> codeLabel.setTextFill(Color.YELLOW);
            case 4, 5 -> codeLabel.setTextFill(Color.RED);
        }
        try {
            JsonNode jsonNode = mapper.readTree(response.body());
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            Platform.runLater(() -> {
                codeLabel.setText(String.valueOf(i));
                dataTextArea.setText(s);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void initialize() {

    }
}
