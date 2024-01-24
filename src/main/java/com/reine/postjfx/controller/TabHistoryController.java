package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.utils.LogUtils;
import com.reine.postjfx.utils.NodeManage;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 左侧tab标签页右侧历史记录
 *
 * @author reine
 */
public class TabHistoryController extends HBox {

    @FXML
    private PostTabController postTabController;

    @FXML
    private ListView<Log> historyListView;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button cancelBtn;


    public TabHistoryController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("tab-history.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    void initialize() {
        // 如果栈中数据大于0，则按钮可点击
        LogUtils.logStackSize.addListener((observable, oldValue, newValue) -> cancelBtn.setDisable(newValue.intValue() <= 0));

        // 将工具类中的日期监视属性与日期选择器的值进行双向绑定
        datePicker.valueProperty().bindBidirectional(LogUtils.dateProperty);

        // 将日期设置为当天，查询当天的数据
        datePicker.setValue(LocalDate.now());

        // 设置历史记录列表以展示
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
                        Node cell = initListCell(item);
                        setGraphic(cell);
                    }

                    /**
                     * 构建列表中的每一行
                     * @param item 每一条日志信息
                     * @return 构建完成后的节点
                     */
                    private Node initListCell(Log item) {
                        BorderPane cell = new BorderPane();
                        // 左侧请求类型按钮
                        Button method = new Button(item.method());
                        method.setPrefWidth(60);
                        // 中间请求url
                        Label label = new Label(item.url());
                        label.setMaxWidth(180.0);
                        label.setMinWidth(180.0);
                        HBox center = new HBox(10);
                        center.setAlignment(Pos.CENTER_LEFT);
                        center.getChildren().addAll(method, label);
                        cell.setLeft(center);
                        // 右侧删除按钮
                        ImageView delete = new ImageView(new Image(
                                Objects.requireNonNull(getClass().getResource("/image/del.png")).toString()
                        ));
                        delete.setFitWidth(20);
                        delete.setFitHeight(20);
                        delete.setPickOnBounds(true);
                        VBox right = new VBox();
                        right.setAlignment(Pos.CENTER);
                        right.getChildren().add(delete);
                        cell.setRight(right);
                        label.setTooltip(new Tooltip(item.url()));
                        // 点击按钮复现历史记录
                        method.setOnAction(event -> postTabController.addPostPageWithData(item));
                        // 点击删除按钮删除历史记录
                        delete.setOnMouseClicked(event -> LogUtils.logList.remove(item));
                        return cell;
                    }
                };
            }
        });
    }

    /**
     * 组件自适应宽高
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        Stage stage = NodeManage.getPrimaryStage();
        ReadOnlyDoubleProperty heightProperty = stage.heightProperty();
        ReadOnlyDoubleProperty widthProperty = stage.widthProperty();
        // 右侧列表高度绑定
        historyListView.prefHeightProperty().bind(heightProperty.subtract(((HBox) datePicker.getParent()).getHeight()));
        // 左侧请求区域高度绑定
        postTabController.prefHeightProperty().bind(heightProperty);
        // 左侧请求区域高度绑定
        postTabController.prefWidthProperty().bind(widthProperty.subtract(historyListView.getWidth()));
    }

    @FXML
    void cancelDelete() {
        LogUtils.restoreHistory(datePicker.getValue());
    }

}
