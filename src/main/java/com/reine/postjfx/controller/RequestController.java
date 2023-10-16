package com.reine.postjfx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reine.postjfx.App;
import com.reine.postjfx.component.*;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.ParamTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Objects;
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
        httpMethod.http(url, params, headers, bodyTextArea.getText())
                .thenAcceptAsync(response -> {
                    responseController.showResult(response);
                    sendButton.setVisible(true);
                    sendingProgress.setVisible(false);
                })
                .exceptionallyAsync(throwable -> {
                    responseController.showErrorMessage(throwable.getMessage());
                    sendButton.setVisible(true);
                    sendingProgress.setVisible(false);
                    return null;
                });
    }

    @FXML
    void clear() {
        urlTextField.clear();
    }


    @FXML
    void initialize() {
        initHeadersTableView();
        initParamsTableView();
        initBodyTab();
        initTopRequestNode();
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
        headersTableView.setColumnResizePolicy(param -> true);
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
        paramsTableView.setColumnResizePolicy(param -> true);
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
    void formatBody(){
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
    }

    /**
     * 验证url地址是否合法
     *
     * @param input
     * @return
     */
    private boolean isHttpUri(String input) {
        String regex = "^(http|https)://[a-zA-Z0-9.-]+(:[0-9]+)?(/[a-zA-Z0-9/]+)*(\\?[a-zA-Z0-9&=]+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
