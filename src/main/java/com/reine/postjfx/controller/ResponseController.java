package com.reine.postjfx.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import com.reine.postjfx.entity.property.ReadOnlyHeader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 下方响应内容
 *
 * @author reine
 */
public class ResponseController extends VBox {

    @FXML
    private HBox tip;

    @FXML
    private Button downloadButton;

    @FXML
    private Label codeLabel;

    @FXML
    private TextArea dataTextArea;

    @FXML
    private TabPane responseTabPane;

    @FXML
    private TableView<ReadOnlyHeader> requestHeaderTableView;

    @FXML
    private TableView<ReadOnlyHeader> responseHeaderTableView;

    /**
     * tabPane的header高度
     */
    private final double tabPaneHeaderHeight = 80;

    public void setMainController(PostPageController postPageController) {
        // 绑定计算
        // 当拖动分割面板的分割条时，下方响应内容区域高度也会随之改变
        // 计算规则 【 (- (分割条所占区域) + 1.0) * (主界面高度) - (响应码区域高度)】
        responseTabPane.prefHeightProperty().bind(postPageController.getDividers().get(0).positionProperty()
                .negate()
                .add(1.0)
                .multiply(postPageController.getPrefHeight())
                .subtract(tip.getPrefHeight()));
        requestHeaderTableView.prefHeightProperty().bind(responseTabPane.prefHeightProperty().subtract(tabPaneHeaderHeight));
        responseHeaderTableView.prefHeightProperty().bind(responseTabPane.prefHeightProperty().subtract(tabPaneHeaderHeight));
        dataTextArea.prefHeightProperty().bind(responseTabPane.prefHeightProperty().subtract(tabPaneHeaderHeight));
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public ResponseController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("response-data.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    private Tab bodyTab;

    @FXML
    private Tab requestTab;

    @FXML
    private Tab responseTab;

    public void showResult(HttpResponse<?> response) {
        // 更新请求头信息
        fillTableData(requestTab, requestHeaderTableView, response.request().headers());
        // 更新响应头信息
        HttpHeaders responseHeaders = response.headers();
        fillTableData(responseTab, responseHeaderTableView, responseHeaders);
        // 渲染响应体信息
        int code = response.statusCode();
        List<String> responseContentType = responseHeaders.allValues("content-type");
        switch (code / 100) {
            case 2 -> codeLabel.setTextFill(Color.GREEN);
            case 3 -> codeLabel.setTextFill(Color.YELLOW);
            case 4, 5 -> codeLabel.setTextFill(Color.RED);
        }
        if (responseContentType.stream().anyMatch(s -> s.contains("text"))) {
            Object message = response.body();
            downloadButton.setVisible(false);
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
        } else {
            String body = (String) response.body();
            byte[] bytes = body.getBytes();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                String fileType = FileTypeUtil.getType(inputStream);
                inputStream.reset();
                Platform.runLater(() -> {
                    codeLabel.setText(String.valueOf(code));
                    dataTextArea.setText(String.format("检测到%s类型文件，点击右上角下载", fileType != null ? fileType : "未知"));
                });
                // 显示下载按钮
                downloadButton.setVisible(true);
                // FIXME 具体下载操作
                downloadButton.setOnAction(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("保存文件");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有类型", "*.*"));
                    if (fileType != null) {
                        fileChooser.getExtensionFilters()
                                .add(new FileChooser.ExtensionFilter(fileType, String.format("*.%s", fileType)));
                    }
                    File file = fileChooser.showSaveDialog(getScene().getWindow());
                    Optional.ofNullable(file)
                            .ifPresent(f -> {
                                try (FileOutputStream outputStream = new FileOutputStream(f)) {
                                    byte[] byteArray = new byte[1024];
                                    while (inputStream.read(byteArray) != -1)
                                        outputStream.write(byteArray);
                                    outputStream.flush();
                                } catch (IOException innerE) {
                                    innerE.printStackTrace();
                                }
                            });
                });
            } catch (IOException outerE) {
                outerE.printStackTrace();
            }

        }
    }

    /**
     * 填充对应表数据
     *
     * @param tab     标签
     * @param table   对应标签表
     * @param headers 要进行处理的HttpHeaders信息
     */
    private void fillTableData(Tab tab, TableView<ReadOnlyHeader> table, HttpHeaders headers) {
        ObservableList<ReadOnlyHeader> headerList = FXCollections.observableArrayList();
        Map<String, List<String>> headersMap = headers.map();
        for (String key : headersMap.keySet())
            for (String s : headersMap.get(key))
                headerList.add(new ReadOnlyHeader(key, s));
        table.setItems(headerList);
        tab.setDisable(false);
    }

    public void showErrorMessage(String message) {
        responseTabPane.getSelectionModel().select(0);
        requestTab.setDisable(true);
        responseTab.setDisable(true);
        Platform.runLater(() -> {
            codeLabel.setText("");
            dataTextArea.setText(message);
        });
    }

    @FXML
    void initialize() {
        requestHeaderTableView.setColumnResizePolicy(param -> true);
        responseHeaderTableView.setColumnResizePolicy(param -> true);
    }
}
