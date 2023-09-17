package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.component.*;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.entity.Result;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.ParamTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.HttpUtils;
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

public class RequestController extends VBox {

    @FXML
    private ChoiceBox<RequestMethodEnum> methodChoiceBox;

    @FXML
    private TextField urlTextField;

    @FXML
    private TableView<HeaderProperty> headersTableView;

    @FXML
    private TableView<ParamProperty> paramsTableView;

    @FXML
    private TextArea bodyTextArea;

    private ResponseController responseController;

    public RequestController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("request-form.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    public void setResponseController(ResponseController responseController) {
        this.responseController = responseController;
    }

    @FXML
    void sendRequest() {
        String url = urlTextField.getText().split("\\?")[0];
        if (!isUri(url)) responseController.showResult(new Result(500, "非法地址"));
        RequestMethodEnum item = methodChoiceBox.getSelectionModel().getSelectedItem();
        ObservableList<HeaderProperty> headers = headersTableView.getItems();
        headers.removeIf(headerProperty -> headerProperty.getHeaderTypeEnum() == null);
        ObservableList<ParamProperty> params = paramsTableView.getItems();
        params.removeIf(paramProperty -> paramProperty.getKey() == null || Objects.equals(paramProperty.getKey(), ""));
        switch (item) {
            case GET -> HttpUtils.get(url, params, headers)
                    .thenAccept((response) -> responseController.showResult(new Result(response.statusCode(), response.body())))
                    .exceptionally(throwable -> {
                        responseController.showResult(new Result(500, throwable.getMessage()));
                        return null;
                    });
            case POST -> HttpUtils.post(url, params, headers, bodyTextArea.getText())
                    .thenAccept(response -> responseController.showResult(new Result(response.statusCode(), response.body())))
                    .exceptionally(throwable -> {
                        responseController.showResult(new Result(500, throwable.getMessage()));
                        return null;
                    });
        }
    }

    @FXML
    void clear() {
        urlTextField.clear();
    }

    private boolean isUri(String input) {
        String regex = "^(?i)(?:([a-z]+):)(?:(?://)([^/?#]*))?([^?#]*)(?:\\?([^#]*))?(?:#(.*))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    @FXML
    void initialize() {
        initHeadersTableView();
        initParamsTableView();
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
        headersTableView.getItems().addAll(new HeaderProperty(HeaderTypeEnum.CONTENT_TYPE, "application/json"));
        nameColOfHeadersTableView.setCellValueFactory(new PropertyValueFactory<>("headerTypeEnum"));
        nameColOfHeadersTableView.setCellFactory(param -> new ComboBoxHeadersTableCell());
        valueColOfHeadersTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfHeadersTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        addRowOfHeadersTableView.setCellFactory(param -> new AddButton<>());
        removeRowOfHeadersTableView.setCellFactory(param -> new SubButton<>());
    }


    @FXML
    private TableColumn<ParamProperty, String> keyColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, String> valueColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, ParamTypeEnum> typeColOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, Void> addRowOfParamsTableView;

    @FXML
    private TableColumn<ParamProperty, Void> removeRowOfParamsTableView;


    /**
     * 初始化请求参数表
     */
    void initParamsTableView() {
        paramsTableView.setColumnResizePolicy(param -> true);
        paramsTableView.getItems().add(new ParamProperty("", "", ParamTypeEnum.TEXT));
        keyColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyColOfParamsTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfParamsTableView.setCellFactory(param -> new FileOrTextTableCell());
        typeColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("paramTypeEnum"));
        typeColOfParamsTableView.setCellFactory(param -> new ComboBoxParamsTableCell());
        addRowOfParamsTableView.setCellFactory(param -> new AddButton<>());
        removeRowOfParamsTableView.setCellFactory(param -> new SubButton<>());
    }

    @FXML
    private Tab paramsTab;

    /**
     * 初始化顶部请求操作组件
     */
    void initTopRequestNode() {
        // 如果是GET、DELETE方法，则不可携带请求体以及不可发送文件参数
        methodChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean enable = newValue.equals(RequestMethodEnum.GET) || newValue.equals(RequestMethodEnum.DELETE);
            paramsTab.setDisable(enable);
            typeColOfParamsTableView.setEditable(!enable);
        });
        methodChoiceBox.getItems().addAll(RequestMethodEnum.values());
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
