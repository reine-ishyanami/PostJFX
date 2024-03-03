package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.utils.LogUtils;
import com.reine.postjfx.utils.Helper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.time.LocalDate;

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
                        cell.setLeft(method);
                        BorderPane.setAlignment(method, Pos.CENTER);
                        // 中间请求url
                        Label label = new Label(item.url());
                        label.setMaxWidth(180.0);
                        label.setMinWidth(180.0);
                        cell.setCenter(label);
                        BorderPane.setAlignment(label, Pos.CENTER_LEFT);
                        BorderPane.setMargin(label, new Insets(0, 0, 0, 10));
                        // 右侧删除按钮
                        FontIcon icon = new FontIcon(AntDesignIconsOutlined.DELETE);
                        icon.setPickOnBounds(true);
                        cell.setRight(icon);
                        BorderPane.setAlignment(icon, Pos.CENTER);
                        label.setTooltip(new Tooltip(item.url()));
                        // 点击按钮复现历史记录
                        method.setOnAction(event -> postTabController.addPostPageWithData(item));
                        // 点击删除按钮删除历史记录
                        icon.setOnMouseClicked(event -> LogUtils.logList.remove(item));
                        return cell;
                    }
                };
            }
        });
    }


    private boolean initialLayout = false;

    /**
     * 组件自适应宽高
     */
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (!initialLayout) {
            Stage stage = Helper.getPrimaryStage();
            ReadOnlyDoubleProperty heightProperty = stage.heightProperty();
            ReadOnlyDoubleProperty widthProperty = stage.widthProperty();
            // 右侧列表高度绑定
            historyListView.prefHeightProperty().bind(heightProperty.subtract(((HBox) datePicker.getParent()).getHeight()));
            // 左侧请求区域高度绑定
            postTabController.prefHeightProperty().bind(heightProperty);
            // 左侧请求区域高度绑定
            postTabController.prefWidthProperty().bind(widthProperty.subtract(historyListView.getWidth()));
            initialLayout = true;
        }
    }

    @FXML
    void cancelDelete() {
        LogUtils.restoreHistory(datePicker.getValue());
    }

}
