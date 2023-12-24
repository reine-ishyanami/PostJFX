package com.reine.postjfx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import com.reine.postjfx.component.*;
import com.reine.postjfx.entity.property.HeaderProperty;
import com.reine.postjfx.entity.property.ParamProperty;
import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.ParamTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.LogUtils;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上方操作区域
 *
 * @author reine
 */
public class RequestController extends VBox {

    @FXML
    private TableView<HeaderProperty> headersTableView;

    @FXML
    private TableView<ParamProperty> paramsTableView;

    @FXML
    private TextArea bodyTextArea;

    public RequestController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("request-form.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    private ResponseController responseController;

    public void setResponseController(ResponseController responseController) {
        this.responseController = responseController;
    }

    /**
     * 接收发送请求的线程
     */
    private Optional<CompletableFuture<HttpResponse<byte[]>>> http = Optional.empty();

    @FXML
    private Button cancelButton;

    @FXML
    void sendRequest() {
        sendButton.setVisible(false);
        sendingProgress.setVisible(true);
        String url = urlTextField.getText().split("\\?")[0];
        RequestMethodEnum httpMethod = methodComboBox.getSelectionModel().getSelectedItem();
        ObservableList<HeaderProperty> headers = headersTableView.getItems();
        headers.removeIf(headerProperty -> headerProperty.getHeaderTypeEnum() == null);
        ObservableList<ParamProperty> params = paramsTableView.getItems();
        params.removeIf(paramProperty -> paramProperty.getKey() == null || Objects.equals(paramProperty.getKey(), ""));
        this.http = Optional.of(httpMethod.http(url, params, headers, bodyTextArea.getText()));
        cancelButton.setVisible(true);
        http.ifPresent(http -> http.thenAcceptAsync(response -> {
                    responseController.showResult(response);
                    sendButton.setVisible(true);
                    sendingProgress.setVisible(false);
                    cancelButton.setVisible(false);
                    this.http = Optional.empty();
                    // 强制更新右侧日期选择器时间
                    Platform.runLater(() -> LogUtils.dateProperty.set(LocalDate.now()));
                    writeLog(httpMethod.getName(), url, params, headers, bodyTextArea.getText());
                })
                .exceptionally(throwable -> {
                    responseController.showError(throwable);
                    sendButton.setVisible(true);
                    sendingProgress.setVisible(false);
                    cancelButton.setVisible(false);
                    this.http = Optional.empty();
                    return null;
                }));
    }


    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm:ss");

    /**
     * 成功响应时写日志
     *
     * @param url
     * @param params
     * @param headers
     * @param body
     */
    private void writeLog(String method,
                          String url,
                          ObservableList<ParamProperty> params,
                          ObservableList<HeaderProperty> headers,
                          String body) {
        LocalDateTime dateTime = LocalDateTime.now();
        Log log = new Log(dateTime.format(formatter), method, url, params, headers, body);
        // 向日志列表头部插入一条日志
        Platform.runLater(() -> LogUtils.logList.add(0, log));
    }

    @FXML
    void clear() {
        urlTextField.clear();
    }

    @FXML
    void cancel() {
        http.ifPresent(http -> http.cancel(true));
    }

    @FXML
    void initialize() {
        initHeadersTableView();
        initParamsTableView();
        initBodyTab();
        initTopRequestNode();
        binding();
    }

    private void binding() {
        ReadOnlyDoubleProperty widthProperty = this.widthProperty();
        headersTableView.prefWidthProperty().bind(widthProperty);
        DoubleBinding headersTableViewWidthPropertyDivide2 = headersTableView.prefWidthProperty().subtract(60).divide(2);
        nameColOfHeadersTableView.prefWidthProperty().bind(headersTableViewWidthPropertyDivide2);
        valueColOfHeadersTableView.prefWidthProperty().bind(headersTableViewWidthPropertyDivide2);
        paramsTableView.prefWidthProperty().bind(widthProperty);
        DoubleBinding paramsTableViewWidthPropertyDivide2 = paramsTableView.prefWidthProperty().subtract(60).divide(2);
        keyColOfParamsTableView.prefWidthProperty().bind(paramsTableViewWidthPropertyDivide2.subtract(100));
        valueColOfParamsTableView.prefWidthProperty().bind(paramsTableViewWidthPropertyDivide2);
        bodyTextArea.prefWidthProperty().bind(widthProperty);
    }

    @FXML
    private TableColumn<HeaderProperty, HeaderTypeEnum> nameColOfHeadersTableView;

    @FXML
    private TableColumn<HeaderProperty, String> valueColOfHeadersTableView;

    @FXML
    private TableColumn<HeaderProperty, Void> addRowOfHeadersTableView;

    @FXML
    private TableColumn<HeaderProperty, Void> removeRowOfHeadersTableView;

    /**
     * 初始化请求标头表
     */
    void initHeadersTableView() {
        // headersTableView.setColumnResizePolicy(param -> true);
        nameColOfHeadersTableView.setCellValueFactory(new PropertyValueFactory<>("headerTypeEnum"));
        nameColOfHeadersTableView.setCellFactory(param -> new ComboBoxHeadersTableCell());
        valueColOfHeadersTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfHeadersTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        addRowOfHeadersTableView.setCellFactory(param -> new AddButton<>());
        removeRowOfHeadersTableView.setCellFactory(param -> new SubButton<>());
    }

    /**
     * 如果请求标头表为空，则点击添加一行
     */
    @FXML
    void addColIfNoneOfHeadersTableView() {
        if (headersTableView.getItems().isEmpty()) {
            headersTableView.getItems().add(new HeaderProperty());
        }
    }


    @FXML
    private TableColumn<ParamProperty, String> keyColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, ParamTypeEnum> typeColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, String> valueColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, Void> addRowOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, Void> removeRowOfParamsTableView;


    /**
     * 初始化请求参数表
     */
    void initParamsTableView() {
        // paramsTableView.setColumnResizePolicy(param -> true);
        keyColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyColOfParamsTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        typeColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("paramTypeEnum"));
        typeColOfParamsTableView.setCellFactory(param -> new ComboBoxParamsTableCell());
        valueColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfParamsTableView.setCellFactory(param -> new FileOrTextTableCell());
        addRowOfParamsTableView.setCellFactory(param -> new AddButton<>());
        removeRowOfParamsTableView.setCellFactory(param -> new SubButton<>());
    }


    /**
     * 如果请求参数表为空，则点击添加一行
     */
    @FXML
    void addColIfNoneOfParamsTableView() {
        if (paramsTableView.getItems().isEmpty()) {
            paramsTableView.getItems().add(new ParamProperty("", "", ParamTypeEnum.TEXT));
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    private Button formatButton;


    private void initBodyTab() {
        // 判断body中的字符串是否是json字符串，如果是则可以格式化
        bodyTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                mapper.readTree(newValue);
                formatButton.setDisable(false);
            } catch (JsonProcessingException e) {
                formatButton.setDisable(true);
            }
        });
    }


    @FXML
    void formatBody() {
        try {
            String body = bodyTextArea.getText();
            JsonNode jsonNode = mapper.readTree(body);
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            bodyTextArea.setText(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private ComboBox<RequestMethodEnum> methodComboBox;

    @FXML
    private TextField urlTextField;

    @FXML
    private Button sendButton;

    @FXML
    private ProgressIndicator sendingProgress;

    @FXML
    private Tab bodyTab;

    private final Tooltip urlToolTip = new Tooltip("非法地址");

    /**
     * 初始化顶部请求操作组件
     */
    void initTopRequestNode() {
        // 如果是GET、DELETE方法，则不可携带请求体以及不可发送文件参数
        methodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean enable = newValue.equals(RequestMethodEnum.GET) || newValue.equals(RequestMethodEnum.DELETE);
            bodyTab.setDisable(enable);
            typeColOfParamsTableView.setEditable(!enable);
        });
        methodComboBox.getItems().addAll(RequestMethodEnum.values());
        methodComboBox.setValue(RequestMethodEnum.GET);
        methodComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RequestMethodEnum object) {
                return object.getName();
            }

            @Override
            public RequestMethodEnum fromString(String string) {
                return null;
            }
        });

        // 当输入的URL不合法时，提示用户，并使发送按钮不可点击
        urlTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (isHttpUri(newValue)) {
                urlTextField.setTooltip(null);
                sendButton.setDisable(false);
            } else {
                urlTextField.setTooltip(urlToolTip);
                sendButton.setDisable(true);
            }
        }));

        // 输入框回车提交事件
        urlTextField.setOnAction(e -> {
            if (isHttpUri(urlTextField.getText())) {
                sendButton.fire();
                urlTextField.setTooltip(null);
                sendButton.setDisable(false);
            } else {
                urlTextField.setTooltip(urlToolTip);
                sendButton.setDisable(true);
            }
        });
    }

    /**
     * 验证url地址是否合法
     *
     * @param input
     * @return
     */
    private boolean isHttpUri(String input) {
        String regex = "^(http|https)://[a-zA-Z0-9.-]+(:[0-9]+)?(/\\S+)*(\\?\\S+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }


    /**
     * 初始化时如果有传入属性，则初始化控件数据
     *
     * @param log 日志记录
     */
    public void initData(Log log) {
        RequestMethodEnum methodEnum = RequestMethodEnum.valueOf(log.method());
        methodComboBox.setValue(methodEnum);
        urlTextField.setText(log.url());
        paramsTableView.setItems(log.params());
        headersTableView.setItems(log.headers());
        bodyTextArea.setText(log.body());
    }
}
