package com.reine.postjfx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import com.reine.postjfx.entity.Result;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("response-data.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void showResult(Result result) {
        int i = result.code();
        switch (i / 100) {
            case 2 -> codeLabel.setTextFill(Color.GREEN);
            case 3 -> codeLabel.setTextFill(Color.YELLOW);
            case 4, 5 -> codeLabel.setTextFill(Color.RED);
        }
        try {
            JsonNode jsonNode = mapper.readTree(result.message());
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            Platform.runLater(() -> {
                codeLabel.setText(String.valueOf(i));
                dataTextArea.setText(s);
            });
        } catch (JsonProcessingException e) {
            Platform.runLater(() -> {
                codeLabel.setText(String.valueOf(i));
                dataTextArea.setText(result.message());
            });
            throw new RuntimeException(e);
        }
    }


    @FXML
    void initialize() {

    }
}
