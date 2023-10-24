package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.Log;
import com.reine.postjfx.utils.LogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author reine
 */
public class TabHistoryController extends HBox {

    @FXML
    private PostTabController postTabController;

    @FXML
    private ListView<Log> historyListView;

    private final ObservableList<Log> logList = FXCollections.observableList(new ArrayList<>());

    public TabHistoryController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tab-history.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }


    @FXML
    void initialize() {
        LogUtils.logList = logList;
        historyListView.setItems(logList);
        historyListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Log> call(ListView<Log> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Log item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            return;
                        }
                        HBox cell = new HBox(10);
                        cell.setAlignment(Pos.CENTER_LEFT);
                        Button button = new Button(item.method());
                        Label label = new Label(item.url());
                        cell.getChildren().addAll(button, label);
                        setGraphic(cell);
                        // TODO 按钮点击事件
                    }
                };
            }
        });
    }
}
