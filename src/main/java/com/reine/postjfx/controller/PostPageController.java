package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.record.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;

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
    }

    @FXML
    void initialize() {
        requestController.setResponseController(responseController);
        responseController.setMainController(this);
    }


}
