package com.reine.postjfx.controller;

import com.reine.postjfx.App;
import com.reine.postjfx.entity.HeaderProperty;
import com.reine.postjfx.entity.ParamProperty;
import javafx.collections.ObservableList;
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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("post-page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * 根据传入的数据新建标签页
     *
     * @param method  请求方法
     * @param url     请求url
     * @param params  请求参数
     * @param headers 请求头
     * @param body    请求体
     * @throws IOException
     */
    public PostPageController(String method,
                              String url,
                              ObservableList<ParamProperty> params,
                              ObservableList<HeaderProperty> headers,
                              String body) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("post-page.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        // 初始化请求内容信息
        requestController.initData(method, url, params, headers, body);

    }

    @FXML
    void initialize() {
        requestController.setResponseController(responseController);
        responseController.setMainController(this);
    }


}
