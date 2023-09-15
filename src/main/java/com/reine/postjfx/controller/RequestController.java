package com.reine.postjfx.controller;

import com.reine.postjfx.HelloApplication;
import com.reine.postjfx.component.AddButton;
import com.reine.postjfx.component.SubButton;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import com.reine.postjfx.entity.Result;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.HttpUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestController extends VBox {

    @FXML
    private ChoiceBox<RequestMethodEnum> methodChoiceBox;

    @FXML
    private TextField urlField;

    @FXML
    private TableView<HeaderProperty> headersTableView;

    @FXML
    private TableView<ParamProperty> paramsTableView;

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
        String url = urlField.getText().split("\\?")[0];
        if (!isUri(url)) responseController.showResult(new Result(500, "非法地址"));
        RequestMethodEnum item = methodChoiceBox.getSelectionModel().getSelectedItem();
        switch (item) {
            case GET -> {
                ObservableList<HeaderProperty> items = headersTableView.getItems();
                items.removeIf(headerProperty -> headerProperty.getHeaderTypeEnum() == null);
                HttpUtils.get(url, null, items).thenAccept((response) -> {
                    responseController.showResult(new Result(response.statusCode(), response.body()));
                }).exceptionally(throwable -> {
                    responseController.showResult(new Result(500, throwable.getMessage()));
                    return null;
                });
            }
        }
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
        nameColOfHeadersTableView.setCellFactory(ChoiceBoxTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(HeaderTypeEnum object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public HeaderTypeEnum fromString(String string) {
                return HeaderTypeEnum.valueOf(string);
            }
        }, HeaderTypeEnum.values()));
        valueColOfHeadersTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfHeadersTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        addRowOfHeadersTableView.setCellFactory(param -> new AddButton<>(headersTableView, HeaderProperty.class));
        removeRowOfHeadersTableView.setCellFactory(param -> new SubButton<>(headersTableView));
    }


    @FXML
    private TableColumn<ParamProperty, String> keyColOfParamsTableView;

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
        paramsTableView.getItems().add(new ParamProperty());
        keyColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("key"));
        keyColOfParamsTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColOfParamsTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfParamsTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        addRowOfParamsTableView.setCellFactory(param -> new AddButton<>(paramsTableView, ParamProperty.class));
        removeRowOfParamsTableView.setCellFactory(param -> new SubButton<>(paramsTableView));
    }

    /**
     * 初始化顶部请求操作组件
     */
    void initTopRequestNode() {
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
