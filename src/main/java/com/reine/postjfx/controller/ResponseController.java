package com.reine.postjfx.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import com.reine.postjfx.entity.property.ReadOnlyHeader;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.nio.charset.StandardCharsets;
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

    @FXML
    private TableColumn<ReadOnlyHeader, String> nameColOfRequestHeader;
    @FXML
    private TableColumn<ReadOnlyHeader, String> valueColOfRequestHeader;
    @FXML
    private TableColumn<ReadOnlyHeader, String> nameColOfResponseHeader;
    @FXML
    private TableColumn<ReadOnlyHeader, String> valueColOfResponseHeader;

    /**
     * 组件自适应宽高
     */
    @Override
    protected void layoutChildren()  {
        super.layoutChildren();
        PostPageController postPageController =
                (PostPageController) this.getParent().getParent().getParent();
        // 绑定计算
        // 当拖动分割面板的分割条时，下方响应内容区域高度也会随之改变
        // 计算规则 【 (- (分割条所占区域) + 1.0) * (主界面高度) - (响应码区域高度)】
        responseTabPane.prefHeightProperty().bind(postPageController.getDividers().getFirst().positionProperty()
                .negate()
                .add(1.0)
                .multiply(postPageController.prefHeightProperty())
                .subtract(tip.prefHeightProperty()));

        DoubleBinding tableViewWidthPropertyDivide2 = responseTabPane.prefWidthProperty().subtract(20).divide(2.0);
        DoubleBinding doubleBinding = responseTabPane.prefHeightProperty().subtract(tip.getPrefHeight());
        requestHeaderTableView.prefHeightProperty().bind(doubleBinding);
        nameColOfRequestHeader.prefWidthProperty().bind(tableViewWidthPropertyDivide2);
        valueColOfRequestHeader.prefWidthProperty().bind(tableViewWidthPropertyDivide2);
        responseHeaderTableView.prefHeightProperty().bind(doubleBinding);
        nameColOfResponseHeader.prefWidthProperty().bind(tableViewWidthPropertyDivide2);
        valueColOfResponseHeader.prefWidthProperty().bind(tableViewWidthPropertyDivide2);
        dataTextArea.prefHeightProperty().bind(doubleBinding);
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

    public void showResult(HttpResponse<byte[]> response) {
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
        // 如果是文本类型的响应内容
        if (responseContentType.stream().anyMatch(s -> s.contains("text") || s.contains("json"))) {
            byte[] message = response.body();
            downloadButton.setVisible(false);
            try {
                JsonNode jsonNode = mapper.readTree(new String(message, StandardCharsets.UTF_8));
                String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                Platform.runLater(() -> {
                    codeLabel.setText(String.valueOf(code));
                    dataTextArea.setText(s);
                });
            } catch (JsonProcessingException e) {
                Platform.runLater(() -> {
                    codeLabel.setText(String.valueOf(code));
                    dataTextArea.setText(new String(message, StandardCharsets.UTF_8));
                });
            }
            // 非文本类型的响应内容
        } else {
            byte[] body = response.body();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(body)) {
                String fileType = FileTypeUtil.getType(inputStream);
                inputStream.reset();
                Platform.runLater(() -> {
                    codeLabel.setText(String.valueOf(code));
                    dataTextArea.setText(String.format("检测到 %s 类型文件，点击右上角下载", fileType != null ? fileType : "未知"));
                    // 显示下载按钮
                    downloadButton.setVisible(true);
                });
                // 具体下载操作
                downloadButton.setOnAction(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("保存文件");
                    fileChooser.setInitialFileName(String.format("example.%s", fileType));
                    ObservableList<FileChooser.ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
                    if (fileType != null)
                        extensionFilters.add(new FileChooser.ExtensionFilter(fileType, String.format("*.%s", fileType)));
                    extensionFilters.add(new FileChooser.ExtensionFilter("所有类型", "*.*"));
                    File file = fileChooser.showSaveDialog(getScene().getWindow());
                    Optional.ofNullable(file)
                            .ifPresent(f -> {
                                try (FileOutputStream outputStream = new FileOutputStream(f)) {
                                    byte[] byteArray = new byte[1024];
                                    while (inputStream.read(byteArray) != -1)
                                        outputStream.write(byteArray);
                                    outputStream.flush();
                                } catch (IOException ignored) {
                                }
                            });
                    // 重置输入流，以便二次下载
                    inputStream.reset();
                });
            } catch (IOException ignored) {
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

    public void showError(Throwable exception) {
        responseTabPane.getSelectionModel().select(0);
        requestTab.setDisable(true);
        responseTab.setDisable(true);
        Platform.runLater(() -> {
            codeLabel.setText("");
            dataTextArea.setText(exception.getLocalizedMessage());
        });
    }

    @FXML
    void initialize() {
        // requestHeaderTableView.setColumnResizePolicy(param -> true);
        // responseHeaderTableView.setColumnResizePolicy(param -> true);
        ReadOnlyDoubleProperty widthProperty = this.widthProperty();
        ReadOnlyDoubleProperty heightProperty = this.heightProperty();
        requestHeaderTableView.prefWidthProperty().bind(widthProperty);
        responseHeaderTableView.prefWidthProperty().bind(widthProperty);
        dataTextArea.prefWidthProperty().bind(widthProperty);
    }
}
