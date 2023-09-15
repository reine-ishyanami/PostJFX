package com.reine.postjfx.controller;

import com.reine.postjfx.HelloApplication;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.enums.HeaderTypeEnum;
import com.reine.postjfx.enums.RequestMethodEnum;
import com.reine.postjfx.utils.HttpUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;

public class RequestController extends VBox {

    @FXML
    private ChoiceBox<RequestMethodEnum> methodChoiceBox;

    @FXML
    private TextField urlField;

    @FXML
    private TableView<HeaderProperty> headersTableView;

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
        addRowOfHeadersTableView.setCellFactory(new Callback<>() {
            @Override
            public TableCell<HeaderProperty, Void> call(TableColumn<HeaderProperty, Void> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            HBox root = new HBox();
                            root.setAlignment(Pos.CENTER);
                            ImageView imageView = new ImageView("/image/add.png");
                            imageView.setFitWidth(10);
                            imageView.setFitHeight(10);
                            root.getChildren().add(imageView);
                            this.setGraphic(root);
                            root.setOnMouseClicked(event -> headersTableView.getItems().add(new HeaderProperty()));
                        } else {
                            this.setGraphic(null);
                        }
                    }
                };
            }
        });
        removeRowOfHeadersTableView.setCellFactory(new Callback<>() {
            @Override
            public TableCell<HeaderProperty, Void> call(TableColumn<HeaderProperty, Void> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            HBox root = new HBox();
                            root.setAlignment(Pos.CENTER);
                            ImageView imageView = new ImageView("/image/sub.png");
                            imageView.setFitWidth(10);
                            imageView.setFitHeight(10);
                            root.getChildren().add(imageView);
                            this.setGraphic(root);
                            root.setOnMouseClicked(event -> {
                                if (headersTableView.getItems().size() > 1)
                                    headersTableView.getItems().remove(this.getIndex());
                            });
                        } else {
                            this.setGraphic(null);
                        }
                    }
                };
            }
        });

    }

    /**
     * 初始化请求参数表
     */
    void initParamsTableView() {
        paramsTableView.setColumnResizePolicy(param -> true);
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
