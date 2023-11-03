package com.reine.postjfx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

/**
 * 下方响应内容
 *
 * @author reine
 */
public class ResponseController extends VBox {

    @FXML
    private HBox tip;

    @FXML
    private Label codeLabel;

    @FXML
    private TextArea dataTextArea;

    @FXML
    private TabPane responseTabPane;

    @FXML
    private TableView<Object> requestHeaderTableView;

    @FXML
    private TableView<Object> responseHeaderTableView;

    /**
     * tabpane的header高度
     */
    private final double tabPaneHeaderHeight = 33;

    public void setMainController(PostPageController postPageController) {
        // 绑定计算
        // 当拖动分割面板的分割条时，下方响应内容区域高度也会随之改变
        // 计算规则 【 (- (分割条所占区域) + 1.0) * (主界面高度) - (响应码区域高度)】
        responseTabPane.prefHeightProperty().bind(postPageController.getDividers().get(0).positionProperty()
                .negate()
                .add(1.0)
                .multiply(postPageController.getPrefHeight())
                .subtract(tip.getPrefHeight()));
        requestHeaderTableView.prefHeightProperty().bind(responseTabPane.heightProperty().subtract(33));
        responseHeaderTableView.prefHeightProperty().bind(responseTabPane.heightProperty().subtract(33));
        dataTextArea.prefHeightProperty().bind(responseTabPane.heightProperty().subtract(33));

    }

    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("response-data.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void showResult(HttpResponse<?> response) {
        System.out.println("======request======");
        HttpHeaders requestHeaders = response.request().headers();
        System.out.println(requestHeaders.map());
        System.out.println("======response======");
        HttpHeaders responseHeaders = response.headers();
        System.out.println(responseHeaders.map());
        int code = response.statusCode();
        Object message = response.body();
        switch (code / 100) {
            case 2 -> codeLabel.setTextFill(Color.GREEN);
            case 3 -> codeLabel.setTextFill(Color.YELLOW);
            case 4, 5 -> codeLabel.setTextFill(Color.RED);
        }
        try {
            JsonNode jsonNode = mapper.readTree(message.toString());
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            Platform.runLater(() -> {
                codeLabel.setText(String.valueOf(code));
                dataTextArea.setText(s);
            });
        } catch (JsonProcessingException e) {
            Platform.runLater(() -> {
                codeLabel.setText(String.valueOf(code));
                dataTextArea.setText(message.toString());
            });
        }
    }

    public void showErrorMessage(String message) {
        Platform.runLater(() -> {
            codeLabel.setText("");
            dataTextArea.setText(message);
        });
    }

    @FXML
    void initialize() {

    }
}
