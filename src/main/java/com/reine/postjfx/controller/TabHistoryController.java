package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.Log;
import com.reine.postjfx.utils.LogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 左侧tab标签页右侧历史记录
 * @author reine
 */
public class TabHistoryController extends HBox {

    @FXML
    private PostTabController postTabController;

    @FXML
    private ListView<Log> historyListView;

    @FXML
    private DatePicker datePicker;


    public TabHistoryController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tab-history.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }


    @FXML
    void initialize() {
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> LogUtils.readFromFileForLogList(newValue));
        // 查询当天日志
        // LogUtils.readFromFileForLogList(LocalDate.now());
        datePicker.setValue(LocalDate.now());

        historyListView.setItems(LogUtils.logList);
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
                        BorderPane cell = new BorderPane();
                        Button method = new Button(item.method());
                        method.setPrefWidth(60);
                        Label label = new Label(item.url());
                        HBox center = new HBox(10);
                        center.setAlignment(Pos.CENTER_LEFT);
                        center.getChildren().addAll(method, label);
                        cell.setCenter(center);
                        ImageView delete = new ImageView(new Image(
                                Objects.requireNonNull(getClass().getResource("/image/del.png")).toString()
                        ));
                        delete.setFitWidth(20);
                        delete.setFitHeight(20);
                        delete.setPickOnBounds(true);
                        cell.setRight(delete);
                        label.setTooltip(new Tooltip(item.url()));
                        setGraphic(cell);
                        // 点击按钮复现历史记录
                        method.setOnAction(event -> postTabController.addPostPageWithData(item));
                        // 点击删除按钮删除历史记录
                        delete.setOnMouseClicked(event -> LogUtils.logList.remove(item));
                    }
                };
            }
        });
    }

}
