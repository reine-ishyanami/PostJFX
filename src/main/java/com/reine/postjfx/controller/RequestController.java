package com.reine.postjfx.controller;

import com.reine.postjfx.HelloApplication;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.HttpUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;

public class RequestController extends VBox {

    @FXML
    private ChoiceBox<RequestMethodEnum> methodChoiceBox;

    @FXML
    private TextField urlField;

    @FXML
    private TableView<HeaderProperty> headerTableView;

    @FXML
    private TableColumn<HeaderProperty, HeaderTypeEnum> nameColOfHeaderTableView;
    @FXML
    private TableColumn<HeaderProperty, String> valueColOfHeaderTableView;

    @FXML
    private TableView<?> paramsTableView;

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
        nameColOfHeaderTableView.setCellValueFactory(new PropertyValueFactory<>("headerTypeEnum"));
        nameColOfHeaderTableView.setCellFactory(ChoiceBoxTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(HeaderTypeEnum object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public HeaderTypeEnum fromString(String string) {
                return HeaderTypeEnum.valueOf(string);
            }
        }, HeaderTypeEnum.values()));
        valueColOfHeaderTableView.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColOfHeaderTableView.setCellFactory(TextFieldTableCell.forTableColumn());
        headerTableView.setColumnResizePolicy(param -> true);
        headerTableView.getItems().addAll(new HeaderProperty(HeaderTypeEnum.CONTENT_TYPE, "application/json"));
        paramsTableView.setColumnResizePolicy(param -> true);
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
