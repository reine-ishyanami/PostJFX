package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.record.Log;
import com.reine.postjfx.utils.NodeManage;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

import java.io.IOException;

/**
 * 具体单个请求页面
 *
 * @author reine
 */
public class PostPageController extends SplitPane {

    @FXML
    private RequestController requestController;

    @FXML
    private ResponseController responseController;


    public PostPageController() throws IOException {
        initView();
    }

    /**
     * 根据传入的数据新建标签页
     *
     * @param log 日志记录
     * @throws IOException
     */
    public PostPageController(Log log) throws IOException {
        initView();
        // 初始化请求内容信息
        requestController.initData(log);
    }

    /**
     * 初始化页面
     *
     * @throws IOException
     */
    private void initView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("post-page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        TabPane tabPane = NodeManage.getTabPane();
        this.prefHeightProperty().bind(tabPane.heightProperty().subtract(10));
        this.prefWidthProperty().bind(tabPane.widthProperty());
    }

    @FXML
    void initialize() {
        // 绑定宽高
        binding();
        responseController.setMainController(this);
        requestController.setResponseController(responseController);
    }

    private void binding() {
        TabPane tabPane = NodeManage.getTabPane();
        ReadOnlyDoubleProperty heightProperty = tabPane.heightProperty();
        ReadOnlyDoubleProperty widthProperty = tabPane.widthProperty();
        requestController.prefWidthProperty().bind(widthProperty);
        responseController.prefWidthProperty().bind(widthProperty);
    }


}
